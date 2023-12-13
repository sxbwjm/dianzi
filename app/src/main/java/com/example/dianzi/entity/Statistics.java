package com.example.dianzi.entity;

import com.example.dianzi.common.CommonFunc;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Statistics {
    public String startDate;
    public int totalTransactionNumber;
    //public int totalDays;
    public float totalPayable;
    public float totalReceivable;
    public float totalProfit;
    public float dailyProfit;

    public float totalUnreceivedCashflow;
    public float totalPrinciple;

    public long getTotalDays() {
        Date sysDate = new Date();
        Date start = CommonFunc.strToDate(startDate);
        return TimeUnit.DAYS.convert(sysDate.getTime() - start.getTime(), TimeUnit.MILLISECONDS);
    }

    public float getTotalProfit() {
        return totalReceivable - totalPayable;
    }

    public float getDailyProfit() {
        long totalDays = getTotalDays();
        if(totalDays == 0) {
            return 0;
        }
        else {
            return getTotalProfit() / totalDays;
        }

    }
    public float getCashAvailable() {
        return totalPrinciple + getTotalProfit() - totalUnreceivedCashflow;
    }
}
