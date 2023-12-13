package com.example.dianzi.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "payable_batch")
public class PayableBatch {
    @NonNull @ColumnInfo(name="payable_batch_id")
    @PrimaryKey(autoGenerate = true)
    public long payableBatchId;

    @NonNull @ColumnInfo(name="payee")
    public String payee;
    @ColumnInfo(name="pay_date")
    public String payDate = null;

    public PayableBatch() {

    }


    public PayableBatch(BankflowPay bankflowPay) {
        this.payee = bankflowPay.name;

    }
    public void addCashPayable(CashflowPayable cashflowPayable){
        cashflowPayable.payableBatchId = this.payableBatchId;
    }

//    public void addBankflowPay(BankflowPay bankflowPay){
//        bankflowPay.payableBatchId = this.payableBatchId;
//    }
}
