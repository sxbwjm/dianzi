package com.example.dianzi.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Transaction;

@Entity(tableName = "cashflow_payable")
public class CashflowPayable extends Cashflow {
    public static String TYPE_TRANSACTION = "送货";
    public static String TYPE_INTEREST = "利息";
    public static String TYPE_ADJ = "调整";

    @NonNull
    @ColumnInfo(name="payable_type")
    public String payableType;
    @ColumnInfo(name="payable_batch_id")
    public long payableBatchId;

    public CashflowPayable() {

    }

    public CashflowPayable(TransactionData transactionData) {
        transactionId = transactionData.transactionId;
        name = transactionData.senderName;
        amount = transactionData.payable;
        flowDate = transactionData.inputDate;
        payableType = TYPE_TRANSACTION;
    }
}
