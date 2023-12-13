package com.example.dianzi.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class Flow {
    @NonNull
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="flow_id")
    public long flowId;
    @NonNull @ColumnInfo(name="flow_date")
    public String flowDate;
    @ColumnInfo(name="name")
    public String name;
    @NonNull
    public float amount;
    public String note;
}
