package com.example.dianzi.entity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PaymentDao {
    @Query("select * from BankflowPay")
    List<BankflowPay> getAll();
    @Insert
    long insert(BankflowPay bankflowPay);
    @Update
    void update(BankflowPay bankflowPay);
}
