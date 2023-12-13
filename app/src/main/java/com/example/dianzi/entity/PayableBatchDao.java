package com.example.dianzi.entity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PayableBatchDao {
    @Query("select * from payable_batch where payee = :payee and pay_date is null")
    PayableBatch getCurrentPayable(String payee);

    @Query("select * from payable_batch where pay_date is null")
    List<PayableBatch> getCurrentPayables();

    @Query("select * from payable_batch where payable_batch_id = :payableBatchId")
    PayableBatch getPayableById(Long payableBatchId);

    @Insert
    long insert(PayableBatch payableBatch);
    @Update
    void update(PayableBatch payableBatch);
}
