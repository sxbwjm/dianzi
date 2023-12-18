package com.example.dianzi.dao;

import androidx.room.Query;

import com.example.dianzi.entity.Sender;

import java.util.List;

public interface SenderDao {
    @Query("select * from sender")
    List<Sender> getAll();
}
