package com.example.dianzi.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.dianzi.entity.BankflowReceive;

import java.util.List;

@Dao
public interface BankflowReceiveDao {
    @Query("select * from bankflow_receive order by flow_date desc")
    List<BankflowReceive> getAll();

    @Insert
    long insert(BankflowReceive bankflowReceive);
    @Query("delete from bankflow_receive")
    void deleteAll();
}
