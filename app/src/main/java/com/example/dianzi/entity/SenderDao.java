package com.example.dianzi.entity;

import androidx.room.Query;

import java.util.List;

public interface SenderDao {
    @Query("select * from sender")
    List<Sender> getAll();
}
