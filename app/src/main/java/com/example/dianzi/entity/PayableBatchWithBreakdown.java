package com.example.dianzi.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PayableBatchWithBreakdown {
    @Embedded
    public PayableBatch payableBatch;

    @Relation(parentColumn = "payable_batch_id", entityColumn = "payable_batch_id")
    public List<CashflowPayable> cashflowPayableList;

    @Relation(parentColumn = "payable_batch_id", entityColumn = "payable_batch_id")
    public List<BankflowPay> bankflowPayList;
}
