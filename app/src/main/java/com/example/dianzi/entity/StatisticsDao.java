package com.example.dianzi.entity;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StatisticsDao {

    @Query("select count(*) from transaction_data")
    int getTotalTransactionNumber();
    @Query("select sum(amount) from cashflow_receivable")
    float getTotalReceivable();

    @Query("select sum(amount) from cashflow_payable")
    float getTotalPayable();

    @Query("select min(input_date) from transaction_data")
    String getStartDate();

    @Query("select sum(a.amount) from cashflow_receivable a left join bankflow_receive b on a.bankflow_id = b.flow_id where b.flow_id is null")
    float getTotalUnreceivedCashflow();

    @Query("select sum(amount) from bankflow_principle")
    float getTotalPrinciple();


}
