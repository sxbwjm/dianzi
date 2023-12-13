package com.example.dianzi.entity;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Sender {
    @NonNull
    @ColumnInfo(name="sender_id")
    @PrimaryKey(autoGenerate = true)
    public long senderId;

    public String name;
}
