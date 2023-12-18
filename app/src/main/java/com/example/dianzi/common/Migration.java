package com.example.dianzi.common;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.example.dianzi.MainApplication;
import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.db.AppDatabase;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.entity.BankflowPay;
import com.example.dianzi.entity.BankflowPrinciple;
import com.example.dianzi.entity.BankflowReceive;
import com.example.dianzi.entity.CashflowPayable;
import com.example.dianzi.entity.CashflowReceivable;
import com.example.dianzi.entity.PayableBatch;
import com.example.dianzi.entity.TransactionData;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Migration {
    Context context;
    AppDatabase db = MainApplication.instance.getDB();
    DBAsyncTask dbAsyncTask = new DBAsyncTask(db);
    public Migration(Context context) {
        this.context = context;
    }
    public void import_excel(Uri uri) throws Exception{
        Toast.makeText(context, "start", Toast.LENGTH_LONG).show();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {

                Workbook workbook = null;
                try {
                    InputStream stream = context.getContentResolver().openInputStream(uri);
                    workbook = WorkbookFactory.create(stream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Sheet sheet = workbook.getSheet("交易");


                db.deleteAllMigrationHelper();
                migrationTransactionData(sheet);

                sheet = workbook.getSheet("到账");
                migrateBankflowReceive(sheet);

                sheet = workbook.getSheet("本金");
                migratePrincipal(sheet);

            }
        });
           // FileInputStream stream = new FileInputStream(new File(path));


    }

    private void migrationTransactionData(Sheet sheet) {
        int rowNum = 1;

        Row row = sheet.getRow(rowNum);
        Map<String, String> prevPayDateMap = new HashMap<String, String>();
        Map<String, PayableBatch> prevPayableBatchMap = new HashMap<String, PayableBatch>();

        String senderName = row.getCell(0).getStringCellValue();
        String payDate = CommonFunc.dateToStr(row.getCell(13).getDateCellValue());
        prevPayDateMap.put(senderName, payDate);
        while (row.getCell(0).getStringCellValue() != null && !row.getCell(0).getStringCellValue().isEmpty()) {
            senderName = row.getCell(0).getStringCellValue();
            // settlement payment
            Date date = row.getCell(13).getDateCellValue();
            if(date != null) {
                payDate = CommonFunc.dateToStr(date);
                String prevPayDate = prevPayDateMap.get(senderName);

                // settle the previous batch
                if (prevPayDate != null && !payDate.equals(prevPayDate)) {

                    PayableBatch prevPayableBatch = prevPayableBatchMap.get(senderName);
                    createSettlePayment(senderName, prevPayDate, prevPayableBatch, db);
                }
            }


            // new transaction
            TransactionData transactionData = new TransactionData();
            transactionData.senderName = row.getCell(0).getStringCellValue();
            String temp = row.getCell(1).getStringCellValue();
            transactionData.inputDate = temp.substring(0, 8);
            transactionData.sequence = temp.substring(8, 12);
            transactionData.plateNumber = row.getCell(2).getStringCellValue();
            transactionData.accountName = row.getCell(4).getStringCellValue();
            transactionData.weight = (float) row.getCell(5).getNumericCellValue();
            transactionData.transactionId = dbAsyncTask.inserTransactionHelper(transactionData);


            // update result
            Float price = (float) row.getCell(6).getNumericCellValue();
            if(price != null && price > 0) {
                transactionData.price = (float) row.getCell(6).getNumericCellValue();

                transactionData.receivable = (float) row.getCell(7).getNumericCellValue();
                transactionData.updateFeePayable();
                dbAsyncTask.updateTransationHelper(transactionData);
            }

            // adjustment
            Float adjust = (float) row.getCell(9).getNumericCellValue();
            if (adjust != null && adjust != 0) {
                CashflowPayable cashflowPayable = new CashflowPayable();
                cashflowPayable.flowDate = transactionData.inputDate;
                cashflowPayable.name = transactionData.senderName;
                cashflowPayable.amount = adjust;
                cashflowPayable.payableType = CashflowPayable.TYPE_ADJ;
                db.insertCashflowPayable(cashflowPayable);
            }

            // prepayment
            Float prepay = (float) row.getCell(11).getNumericCellValue();
            if (prepay != null && prepay > 0) {
                BankflowPay bankflowPay = new BankflowPay();
                bankflowPay.flowDate = transactionData.inputDate;
                bankflowPay.name = transactionData.senderName;
                bankflowPay.amount = prepay;
                bankflowPay.payType = BankflowPay.TYPE_PREPAY;
                db.insertPrepaymentHelper(bankflowPay);
            }


            if(price > 0 || adjust != 0 || prepay > 0) {
                PayableBatch payableBatch = db.payableBatchDao().getCurrentPayable(senderName);
                prevPayableBatchMap.put(senderName, payableBatch);
                prevPayDateMap.put(senderName, payDate);
            }

            System.out.println(transactionData);
            rowNum++;
            row = sheet.getRow(rowNum);

        }

        // unhandled settlement
        for(String sender : prevPayDateMap.keySet()) {
            String prevPayDate = prevPayDateMap.get(sender);
            PayableBatch prevPayableBatch = prevPayableBatchMap.get(sender);
            createSettlePayment(sender, prevPayDate, prevPayableBatch, db);
        }

    }

    private void createSettlePayment(String senderName, String prevPayDate, PayableBatch prevPayableBatch, AppDatabase db) {
        System.out.println("create settlement:");


            prevPayableBatch.payDate = prevPayDate;

            BankflowPay bankflowPay = new BankflowPay();
            // DateFormat df = new SimpleDateFormat(Config.DATE_FORMAT);
            bankflowPay.flowDate = prevPayDate;
            bankflowPay.name = senderName;
            bankflowPay.amount = db.payableBatchDao().getPayableAmount(prevPayableBatch.payableBatchId) - db.payableBatchDao().getPaidAmount(prevPayableBatch.payableBatchId);
            bankflowPay.payableBatchId = prevPayableBatch.payableBatchId;
            bankflowPay.payType = BankflowPay.TYPE_SETTLE;

            System.out.println("payable batch id:" + prevPayableBatch.payableBatchId);
            System.out.println("settlement amount:" +  bankflowPay.amount);

            db.insertSettlePaymentHelper(bankflowPay, prevPayableBatch);

    }

    private void migrateBankflowReceive(Sheet sheet){


        int rowNum = 1;

        Row row = sheet.getRow(rowNum);
        String accountName = row.getCell(0).getStringCellValue();
        while (accountName != null && !accountName.isEmpty()) {

            BankflowReceive bankflowReceive = new BankflowReceive();
            bankflowReceive.name = accountName;
            bankflowReceive.flowDate = CommonFunc.dateToStr(row.getCell(1).getDateCellValue());
            bankflowReceive.amount = (float)row.getCell(2).getNumericCellValue();
            bankflowReceive.receiveType = BankflowReceive.TYPE_TRANSACTION;

            List<CashflowReceivable> unReceivedCashflowReceivable = db.cashflowReceivableDao().getUnReceived();
            List<CashflowReceivable> candidates = bankflowReceive.getCandidateCashflowReceivableList(unReceivedCashflowReceivable);
            List<CashflowReceivable> match = bankflowReceive.match(candidates);
            db.insertBankflowReceiveHelper(bankflowReceive, match);


            rowNum++;
            row = sheet.getRow(rowNum);
            accountName = row.getCell(0).getStringCellValue();
        }
    }

    private void migratePrincipal(Sheet sheet) {

        int rowNum = 1;

        Row row = sheet.getRow(rowNum);
        Date flowDate = row.getCell(0).getDateCellValue();
        while (flowDate != null) {
            BankflowPrinciple bankflowPrinciple = new BankflowPrinciple();
            bankflowPrinciple.flowDate = CommonFunc.dateToStr(flowDate);
            bankflowPrinciple.note = row.getCell(1).getStringCellValue();
            bankflowPrinciple.amount = (float)row.getCell(2).getNumericCellValue();

            db.bankflowPrincipleDao().insert(bankflowPrinciple);

            rowNum++;
            row = sheet.getRow(rowNum);
            flowDate = row.getCell(0).getDateCellValue();
        }

    }
}
