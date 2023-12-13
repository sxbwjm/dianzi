package com.example.dianzi.entity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TransactionDao {
    @Query("select * from transaction_data order by input_date desc, sequence desc")
    List<TransactionData> getAll();

    @Insert
    long insert(TransactionData transaction);

    @Update
    void update(TransactionData transaction);

    @Delete
    void delete(TransactionData transaction);

    @Query("delete from transaction_data")
    void deleteAll();

}
