package com.example.dianzi.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Account {
    @NonNull
    @ColumnInfo(name="account_id")
    @PrimaryKey(autoGenerate = true)
    public long account_id;

    public String name;
}
