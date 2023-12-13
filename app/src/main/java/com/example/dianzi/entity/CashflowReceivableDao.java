package com.example.dianzi.entity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CashflowReceivableDao {
    @Query("select * from cashflow_receivable")
    List<CashflowReceivable> getAll();

    @Query("select * from cashflow_receivable where flow_id = :id")
    CashflowReceivable getById(long id);
    @Query("select a.* from cashflow_receivable a left join bankflow_receive b on a.bankflow_id = b.flow_id where b.flow_id is null")
    List<CashflowReceivable> getUnReceived();

    @Insert
    long insert(CashflowReceivable cashflowReceivable);
    @Update
    void update(CashflowReceivable cashflowReceivable);

    @Query("delete from cashflow_receivable where flow_id = :flowId")
    void deleteById(long flowId);
}
