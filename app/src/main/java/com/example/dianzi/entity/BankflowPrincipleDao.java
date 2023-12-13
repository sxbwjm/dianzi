package com.example.dianzi.entity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BankflowPrincipleDao {
    @Query("select * from bankflow_principle order by flow_date desc, flow_id desc")
    List<BankflowPrinciple> getAll();

    @Insert
    long insert(BankflowPrinciple bankflowPrinciple);
    @Update
    void update(BankflowPrinciple bankflowPrinciple);
}
