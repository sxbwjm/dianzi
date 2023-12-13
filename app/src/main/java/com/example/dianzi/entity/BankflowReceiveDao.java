package com.example.dianzi.entity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BankflowReceiveDao {
    @Query("select * from bankflow_receive order by flow_id desc")
    List<BankflowReceive> getAll();

    @Insert
    long insert(BankflowReceive bankflowReceive);
    @Query("delete from bankflow_receive")
    void deleteAll();
}
