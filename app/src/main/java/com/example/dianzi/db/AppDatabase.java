package com.example.dianzi.db;

import android.os.Handler;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.dianzi.entity.Account;
import com.example.dianzi.dao.BankflowPayDao;
import com.example.dianzi.entity.BankflowPrinciple;
import com.example.dianzi.dao.BankflowPrincipleDao;
import com.example.dianzi.entity.BankflowReceive;
import com.example.dianzi.dao.BankflowReceiveDao;
import com.example.dianzi.entity.BankflowPay;
import com.example.dianzi.entity.CashflowPayable;
import com.example.dianzi.dao.CashflowPayableDao;
import com.example.dianzi.entity.CashflowReceivable;
import com.example.dianzi.dao.CashflowReceivableDao;
import com.example.dianzi.entity.PayableBatch;
import com.example.dianzi.dao.PayableBatchDao;
import com.example.dianzi.dao.PayableBatchWithBreakdownDao;
import com.example.dianzi.entity.Sender;
import com.example.dianzi.dao.StatisticsDao;
import com.example.dianzi.entity.TransactionData;
import com.example.dianzi.dao.TransactionDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities =
        {TransactionData.class, CashflowPayable.class, CashflowReceivable.class,
        PayableBatch.class, BankflowPay.class, BankflowReceive.class, Sender.class, Account.class, BankflowPrinciple.class
        },
        version=20, exportSchema = false
       )
    public abstract class AppDatabase extends RoomDatabase {
    public abstract TransactionDao transactionDao();
    public abstract CashflowPayableDao cashflowPayableDao();

    public abstract CashflowReceivableDao cashflowReceivableDao();

    public abstract PayableBatchDao payableBatchDao();

    public abstract BankflowPayDao bankflowPayDao();

    public abstract BankflowReceiveDao bankflowReceiveDao();

    public abstract PayableBatchWithBreakdownDao payableBatchWithBreakdownDao();

    public abstract StatisticsDao statisticsDao();

    public abstract BankflowPrincipleDao bankflowPrincipleDao();

    public void deleteAllMigrationHelper() {
        transactionDao().deleteAll();
        bankflowPayDao().deleteAll();
        bankflowReceiveDao().deleteAll();
        cashflowReceivableDao().deleteAll();
        cashflowPayableDao().deleteAll();
        payableBatchDao().deleteAll();
        bankflowPrincipleDao().deleteAll();

    }

    public void insertPrepaymentHelper(BankflowPay prepayment) {
        PayableBatch payableBatch = payableBatchDao().getCurrentPayable(prepayment.name);
        long payableBatchId = 0;
        if(payableBatch == null) {
            payableBatch = new PayableBatch(prepayment);
//                    payableBatch.payee = bankflowPay.name;
//                    payableBatch.amount = -bankflowPay.amount;
            payableBatchId = payableBatchDao().insert(payableBatch);
        } else {
            //  prepayment.payableBatchId = payableBatch.payableBatchId;
            //payableBatch.addBankflowPay(prepayment);
            //     payableBatch.amount = payableBatch.amount - bankflowPay.amount;
            //  db.payableBatchDao().update(payableBatch);

            payableBatchId = payableBatch.payableBatchId;
        }

        prepayment.payableBatchId = payableBatchId;
        bankflowPayDao().insert(prepayment);
    }

    public void insertCashflowPayable(CashflowPayable cashflowPayable) {


        PayableBatch payableBatch = payableBatchDao().getCurrentPayable(cashflowPayable.name);
        long payableBatchId = 0;
        if(payableBatch == null) {
            payableBatch = new PayableBatch();
            payableBatch.payee = cashflowPayable.name;
            payableBatchId = payableBatchDao().insert(payableBatch);


        } else {
            payableBatchId = payableBatch.payableBatchId;
            payableBatch.addCashPayable(cashflowPayable);
            payableBatchDao().update(payableBatch);
        }

        cashflowPayable.payableBatchId = payableBatchId;
        cashflowPayableDao().insert(cashflowPayable);

    }

    public void insertSettlePaymentHelper(BankflowPay settlePayment, PayableBatch payableBatch) {
        settlePayment.payableBatchId = payableBatch.payableBatchId;
        bankflowPayDao().insert(settlePayment);
        payableBatchDao().update(payableBatch);
    }

    public void insertBankflowReceiveHelper(BankflowReceive bankflowReceive, List<CashflowReceivable> matchList) {

        long bankflowId = bankflowReceiveDao().insert(bankflowReceive);
        for(CashflowReceivable c : matchList) {
            c.bankflowId = bankflowId;
            cashflowReceivableDao().update(c);
        }
    }


}
