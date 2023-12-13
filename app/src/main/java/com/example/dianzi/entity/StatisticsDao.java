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

    @Query("select substr(input_date, 1, 6) from transaction_data order by 1 asc")
    List<String> getTransactionMonthList();

    @Query("select sum(fee) from transaction_data group by substr(input_date, 1, 6) order by substr(input_date, 1, 6) asc")
    List<Float> getTransactionMonthProfit();


}
