package com.example.dianzi.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class Cashflow extends Flow {
    @ColumnInfo(name = "transaction_id")
    public long transactionId;
}
