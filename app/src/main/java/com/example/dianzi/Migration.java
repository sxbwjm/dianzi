package com.example.dianzi;

import android.content.Context;
import android.net.Uri;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dianzi.activity.MainActivity;
import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.db.AppDatabase;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.db.DataSet;
import com.example.dianzi.entity.BankflowPay;
import com.example.dianzi.entity.CashflowPayable;
import com.example.dianzi.entity.PayableBatch;
import com.example.dianzi.entity.Statistics;
import com.example.dianzi.entity.StatisticsDao;
import com.example.dianzi.entity.TransactionData;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Migration {
    public static void import_excel(Context context, Uri uri) throws Exception{
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
                Sheet sheet = workbook.getSheet("Transactions");
                AppDatabase db = MainApplication.instance.getDB();
                DBAsyncTask dbAsyncTask = new DBAsyncTask(db);

                dbAsyncTask.deleteAllMigrationHelper();

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
                    payDate = CommonFunc.dateToStr(row.getCell(13).getDateCellValue());
                    String prevPayDate = prevPayDateMap.get(senderName);

                    // settle the previous batch
                    if(prevPayDate != null && !payDate.equals(prevPayDate)) {

                        PayableBatch prevPayableBatch = prevPayableBatchMap.get(senderName);
                        createSettlePayment(senderName, prevPayDate, prevPayableBatch, db);
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
                    transactionData.price = (float) row.getCell(6).getNumericCellValue();
                    transactionData.receivable = (float) row.getCell(7).getNumericCellValue();
                    transactionData.updateFeePayable();
                    dbAsyncTask.updateTransationHelper(transactionData);

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


                    PayableBatch payableBatch = db.payableBatchDao().getCurrentPayable(senderName);
                    prevPayableBatchMap.put(senderName, payableBatch);
                    prevPayDateMap.put(senderName, payDate);

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
        });
           // FileInputStream stream = new FileInputStream(new File(path));


    }

    private static void createSettlePayment(String senderName, String prevPayDate, PayableBatch prevPayableBatch, AppDatabase db) {
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
}
