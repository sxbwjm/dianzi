package com.example.dianzi.db;

import com.example.dianzi.entity.BankflowPrinciple;
import com.example.dianzi.entity.BankflowReceive;
import com.example.dianzi.entity.CashflowPayable;
import com.example.dianzi.entity.CashflowReceivable;
import com.example.dianzi.entity.PayableBatch;
import com.example.dianzi.entity.BankflowPay;
import com.example.dianzi.entity.PayableBatchWithBreakdown;
import com.example.dianzi.entity.Statistics;

import java.util.List;

public class DataSet {
    private static DataSet instance;

    public List<BankflowPay> bankflowPayList;
    public List<PayableBatch> currentPayableBatchList;
    public List<BankflowReceive> bankflowReceiveList;
    public List<BankflowPrinciple> bankflowPrinciplesList;

   // public List<CashflowPayable> unpaidCashflowPayableList;
    public List<PayableBatchWithBreakdown> latestPayableBatchWithBreakdownList;

    public List<CashflowReceivable> unReceivedCashflowReceivalbeList;
    public Statistics statistics;
    private DataSet() {

    }

    public static DataSet getInstance() {
        if(instance == null) {
            instance = new DataSet();
        }

        return instance;
    }
}
