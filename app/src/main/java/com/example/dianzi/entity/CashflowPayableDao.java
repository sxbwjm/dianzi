package com.example.dianzi.entity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CashflowPayableDao {
    @Query("select * from cashflow_payable")
    List<CashflowPayable> getAll();
    @Query("select * from cashflow_payable where flow_id = :id")
    CashflowPayable getById(long id);

    @Query("select a.* from cashflow_payable a join payable_batch b on a.payable_batch_id = b.payable_batch_id where b.pay_date is null")
    List<CashflowPayable> getUnPaid();


    @Query("select sum(amount) from cashflow_payable where payable_batch_id = :payableBatchId")
    float getPayableAmount(long payableBatchId);
    @Insert
    long insert(CashflowPayable cashflowPayable);
    @Update
    void update(CashflowPayable cashflowPayable);

    @Query("delete from cashflow_payable where flow_id = :flowId")
    void deleteById(long flowId);
}
