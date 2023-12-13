package com.example.dianzi.entity;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface PayableBatchWithBreakdownDao {
    @Transaction
    @Query("select * from payable_batch where payable_batch_id in (select max(payable_batch_id) from payable_batch group by payee) order by pay_date desc")
    List<PayableBatchWithBreakdown> getLatest();

}
