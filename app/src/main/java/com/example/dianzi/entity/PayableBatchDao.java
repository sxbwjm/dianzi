package com.example.dianzi.entity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
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


    @Query("select sum(a.amount)" +
            " from cashflow_payable a" +
            " inner join payable_batch b" +
            " on a.payable_batch_id = b.payable_batch_id" +
            " where b.payable_batch_id = :id")
    float getPayableAmount(long id);
    @Query("select sum(a.amount)" +
            " from bankflow_pay a" +
            " inner join payable_batch b" +
            " on a.payable_batch_id = b.payable_batch_id" +
            " where b.payable_batch_id = :id")
    float getPaidAmount(long id);

    @Insert
    long insert(PayableBatch payableBatch);
    @Update
    void update(PayableBatch payableBatch);
    @Query("delete from payable_batch")
    void deleteAll();
}
