package com.example.dianzi.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.io.Serializable;

@Entity(tableName ="bankflow_receive")
public class BankflowReceive extends Bankflow implements Serializable {
    public static String TYPE_TRANSACTION = "交易";

    @ColumnInfo(name="receive_type")
    public String receiveType = TYPE_TRANSACTION;

}
