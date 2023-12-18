package com.example.dianzi.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.dianzi.entity.DailyCash;

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

    @Query("select distinct substr(input_date, 1, 6) from transaction_data order by 1 asc")
    List<String> getTransactionMonthList();

    @Query("select substr(input_date, 1, 6) as month, sum(fee) amount from transaction_data group by substr(input_date, 1, 6) order by substr(input_date, 1, 6) asc")
    List<MonthlyProfit> getTransactionMonthProfit();

    @Query("with all_bankflow as (\n" +
            "select flow_date, -amount as amount from bankflow_pay\n" +
            "union all\n" +
            "select flow_date, amount from bankflow_receive\n" +
            "union all\n" +
            "select flow_date, amount from bankflow_principle\n" +
            ")\n" +
            "select flow_date as date, (select sum(amount) from all_bankflow b where b.flow_date <= a.flow_date) as amount from all_bankflow a " +
            "order by flow_date")
    List<DailyCash> getDailyAvailableCash();

}
