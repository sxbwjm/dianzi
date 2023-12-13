package com.example.dianzi.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "cashflow_receivable")
public class CashflowReceivable extends Cashflow {
    public static String TYPE_TRANSACTION = "送货";

    @NonNull
    @ColumnInfo(name="receivable_type")
    public String receivableType = TYPE_TRANSACTION;

    @ColumnInfo(name="bankflow_id")
    public long bankflowId;

    public CashflowReceivable() {

    }

    public CashflowReceivable(TransactionData transactionData) {
        receivableType = TYPE_TRANSACTION;
        flowDate = transactionData.inputDate;
        transactionId = transactionData.transactionId;
        name = transactionData.accountName;
        amount = transactionData.receivable;
    }
}
