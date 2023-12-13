package com.example.dianzi.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PayableDetails {
    @Embedded
    public PayableBatch payment;
    @Relation (parentColumn = "payable_id",entityColumn = "payable_id")
    List<TransactionData> transactions;

    @Relation(parentColumn = "payable_id", entityColumn = "payable_id")
    List<BankflowPay> bankflowPays;
}
