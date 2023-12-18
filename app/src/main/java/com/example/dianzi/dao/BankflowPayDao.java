package com.example.dianzi.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.dianzi.entity.BankflowPay;

import java.util.List;
@Dao
public interface BankflowPayDao {
    @Query("select * from bankflow_pay order by flow_id desc")
    List<BankflowPay> getAll();

    @Query("select sum(amount) from bankflow_pay where payable_batch_id = :payableBatchId")
    float getPaidAmount(long payableBatchId);

    @Insert
    long insert(BankflowPay bankflowPay);

    @Query("delete from bankflow_pay")
    void deleteAll();
}
