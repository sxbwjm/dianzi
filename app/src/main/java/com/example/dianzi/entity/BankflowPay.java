package com.example.dianzi.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "bankflow_pay")
public class BankflowPay extends Bankflow implements Serializable {

    public static String TYPE_PREPAY = "预付";
    public static String TYPE_SETTLE = "结算";

    @ColumnInfo(name="pay_type")
    public String payType;
    @ColumnInfo(name="payable_batch_id")
    public long payableBatchId;
}
