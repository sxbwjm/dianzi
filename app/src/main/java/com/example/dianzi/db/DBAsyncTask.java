package com.example.dianzi.db;


import android.os.Handler;

import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.entity.BankflowPrinciple;
import com.example.dianzi.entity.BankflowReceive;
import com.example.dianzi.entity.CashflowPayable;
import com.example.dianzi.entity.CashflowReceivable;
import com.example.dianzi.entity.PayableBatch;
import com.example.dianzi.entity.BankflowPay;
import com.example.dianzi.entity.Statistics;
import com.example.dianzi.entity.StatisticsDao;
import com.example.dianzi.entity.TransactionData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DBAsyncTask {
    private AppDatabase db;
    public List<TransactionData> transactions;

    public List<BankflowPay> bankflowPays;
    public DBAsyncTask(AppDatabase db) {
        this.db = db;
    }

    public void getStatistics(Handler handler){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Statistics statistics = new Statistics();
                StatisticsDao statisticsDao = db.statisticsDao();
                statistics.totalTransactionNumber = statisticsDao.getTotalTransactionNumber();
                statistics.totalPayable = statisticsDao.getTotalPayable();
                statistics.totalReceivable = statisticsDao.getTotalReceivable();
                statistics.startDate = statisticsDao.getStartDate();
                statistics.totalUnreceivedCashflow = statisticsDao.getTotalUnreceivedCashflow();
                statistics.totalPrinciple = statisticsDao.getTotalPrinciple();
                statistics.transactionMonthList = statisticsDao.getTransactionMonthList();
                statistics.monthlyProfit = statisticsDao.getTransactionMonthProfit();

                DataSet.getInstance().statistics = statistics;
                handler.sendMessage(handler.obtainMessage(1, "OK"));
            }
        });
    }

    public long inserTransactionHelper(TransactionData transaction) {
       return db.transactionDao().insert(transaction);
    }
    public void insertTransaction(TransactionData transaction) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                inserTransactionHelper(transaction);
            }
        });
    }

    public void deleteTransaction(TransactionData transaction, Handler handler) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {

                System.out.println("cashflowPayableId:" + transaction.cashflowPayableId);

                //TODO: need to check if already paid
                if(transaction.cashflowPayableId != 0) {
                    db.cashflowPayableDao().deleteById(transaction.cashflowPayableId);
                }
                if(transaction.cashflowReceivableId != 0) {
                    db.cashflowReceivableDao().deleteById(transaction.cashflowReceivableId);
                }

                db.transactionDao().delete(transaction);
                handler.sendMessage(handler.obtainMessage(1, "OK"));
            }
        });
    }

    public void updateTransationHelper(TransactionData transaction) {
        CashflowPayable cashflowPayable = null;
        CashflowReceivable cashflowReceivable = null;
        // fist time input result
        if(transaction.cashflowPayableId <=0) {
            cashflowPayable = new CashflowPayable(transaction);
            cashflowPayable.flowId = db.cashflowPayableDao().insert(cashflowPayable);
            transaction.cashflowPayableId = cashflowPayable.flowId;

            cashflowReceivable = new CashflowReceivable(transaction);
            cashflowReceivable.flowId = db.cashflowReceivableDao().insert(cashflowReceivable);
            transaction.cashflowReceivableId = cashflowReceivable.flowId;
        }
        // update result
        else {
            cashflowPayable = db.cashflowPayableDao().getById(transaction.cashflowPayableId);
            cashflowPayable.amount = transaction.payable;
            db.cashflowPayableDao().update(cashflowPayable);

            cashflowReceivable = db.cashflowReceivableDao().getById(transaction.cashflowReceivableId);
            cashflowReceivable.amount = transaction.receivable;
            db.cashflowReceivableDao().update(cashflowReceivable);
        }

        // add to payable batch
        PayableBatch payableBatch = db.payableBatchDao().getCurrentPayable(cashflowPayable.name);
        long payableBatchId = 0;
        if(payableBatch == null) {
            payableBatch = new PayableBatch();
            payableBatch.payee = cashflowPayable.name;
            payableBatchId = db.payableBatchDao().insert(payableBatch);


        } else {
            payableBatchId = payableBatch.payableBatchId;
            payableBatch.addCashPayable(cashflowPayable);
            db.payableBatchDao().update(payableBatch);
        }

        cashflowPayable.payableBatchId = payableBatchId;
        db.cashflowPayableDao().update(cashflowPayable);


        //     PayableBatch payableBatch = db.payableBatchDao().getPayableById(cashflowPayable.payableBatchId);
        // update payable if not paid yet
//                if(payableBatch.payDate == null) {
//                    payableBatch.amount = payableBatch.amount + transaction.payable;
//                    db.payableBatchDao().update(payableBatch);
//                }
//
//                else {
//
//                }
        //TODO: generate adjust after paid

        db.transactionDao().update(transaction);
    }
    public void updateTransaction(TransactionData transaction) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                updateTransationHelper(transaction);
            }
        });
    }

    public void deleteAllMigrationHelper() {
        db.transactionDao().deleteAll();
        db.bankflowPayDao().deleteAll();
        db.cashflowReceivableDao().deleteAll();
        db.cashflowPayableDao().deleteAll();
        db.payableBatchDao().deleteAll();
    }


    public void getAllTransactions(Handler handler){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                transactions = db.transactionDao().getAll();
                handler.sendMessage(handler.obtainMessage(1, transactions));
               // view.setAdapter(new TransactionRecyclerViewAdapter(transactions));
            }
        });
    }

    public void getAllBankflowPay(Handler handler){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DataSet.getInstance().bankflowPayList = db.bankflowPayDao().getAll();
                handler.sendMessage(handler.obtainMessage(1, "OK"));
            }
        });
    }

//    public void getUnpaidCashflowPayables(Handler handler){
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                DataSet.getInstance().unpaidCashflowPayableList = db.cashflowPayableDao().getUnPaid();
//                handler.sendMessage(handler.obtainMessage(1, "OK"));
//            }
//        });
//    }

    public void getUnpaidPayableBatchWithBreakdown(Handler handler){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DataSet.getInstance().latestPayableBatchWithBreakdownList = db.payableBatchWithBreakdownDao().getLatest();
                handler.sendMessage(handler.obtainMessage(1, "OK"));
            }
        });
    }

    public void getUnReceivedCashflowReceivables(Handler handler){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DataSet.getInstance().unReceivedCashflowReceivalbeList = db.cashflowReceivableDao().getUnReceived();
                handler.sendMessage(handler.obtainMessage(1, "OK"));
            }
        });
    }

    public void getAllCashflowReceivables(Handler handler){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DataSet.getInstance().allCashflowReceivalbeList = db.cashflowReceivableDao().getAll();
                handler.sendMessage(handler.obtainMessage(1, "OK"));
            }
        });
    }


//    public void getCurrentPayableBatches2(Handler handler) {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                List<PayableBatch> currentPayableBatchList = db.payableBatchDao().getCurrentPayables();
//
//                for(PayableBatch payableBatch : currentPayableBatchList) {
//                    payableBatch.amount = db.cashflowPayableDao().getPayableAmount(payableBatch.payableBatchId) -
//                    db.bankflowPayDao().getPaidAmount(payableBatch.payableBatchId);
//                }
//                DataSet.getInstance().currentPayableBatchList = currentPayableBatchList;
//                handler.sendMessage(handler.obtainMessage(1, "OK"));
//            }
//        });
//    }



    public void insertPrePayment(BankflowPay prepayment) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.insertPrepaymentHelper(prepayment);
            }
        });
    }

    public void insertSettlePaymentHelper(BankflowPay settlePayment, PayableBatch payableBatch) {
        settlePayment.payableBatchId = payableBatch.payableBatchId;
        db.bankflowPayDao().insert(settlePayment);


        db.payableBatchDao().update(payableBatch);
    }

    public void insertSettlePayment(BankflowPay settlePayment, PayableBatch payableBatch, Handler handler) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //PayableBatch payableBatch = db.payableBatchDao().getCurrentPayable(settlePayment.name);
                insertSettlePaymentHelper(settlePayment, payableBatch);
                handler.sendMessage(handler.obtainMessage(1, "OK"));
            }
        });
    }


    public void getAllBankflowReceive(Handler handler){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DataSet.getInstance().bankflowReceiveList = db.bankflowReceiveDao().getAll();
                handler.sendMessage(handler.obtainMessage(1, "OK"));
            }
        });
    }
    public void insertBankflowReceive(BankflowReceive bankflowReceive, List<CashflowReceivable> matchList) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {

                long bankflowId = db.bankflowReceiveDao().insert(bankflowReceive);
                for(CashflowReceivable c : matchList) {
                    c.bankflowId = bankflowId;
                    db.cashflowReceivableDao().update(c);
                }
            }
        });
    }

    public void insertBankflowPrinciple(BankflowPrinciple bankflowPrinciple) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.bankflowPrincipleDao().insert(bankflowPrinciple);
            }
        });
    }

    public void updateBankflowPrinciple(BankflowPrinciple bankflowPrinciple) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.bankflowPrincipleDao().update(bankflowPrinciple);
            }
        });
    }


    public void getAllBankflowPrinciple(Handler handler){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DataSet.getInstance().bankflowPrinciplesList = db.bankflowPrincipleDao().getAll();
                handler.sendMessage(handler.obtainMessage(1, "OK"));
            }
        });
    }
}
