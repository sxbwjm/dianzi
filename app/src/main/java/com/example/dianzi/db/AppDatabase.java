package com.example.dianzi.db;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.dianzi.entity.Account;
import com.example.dianzi.entity.BankflowPayDao;
import com.example.dianzi.entity.BankflowPrinciple;
import com.example.dianzi.entity.BankflowPrincipleDao;
import com.example.dianzi.entity.BankflowReceive;
import com.example.dianzi.entity.BankflowReceiveDao;
import com.example.dianzi.entity.BankflowPay;
import com.example.dianzi.entity.CashflowPayable;
import com.example.dianzi.entity.CashflowPayableDao;
import com.example.dianzi.entity.CashflowReceivable;
import com.example.dianzi.entity.CashflowReceivableDao;
import com.example.dianzi.entity.PayableBatch;
import com.example.dianzi.entity.PayableBatchDao;
import com.example.dianzi.entity.PayableBatchWithBreakdownDao;
import com.example.dianzi.entity.PaymentDao;
import com.example.dianzi.entity.Sender;
import com.example.dianzi.entity.StatisticsDao;
import com.example.dianzi.entity.TransactionData;
import com.example.dianzi.entity.TransactionDao;

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


}
