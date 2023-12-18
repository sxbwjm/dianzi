package com.example.dianzi.entity;

import android.widget.TextView;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.example.dianzi.R;
import com.example.dianzi.adapter.FlowRecyclerViewAdapter;
import com.example.dianzi.common.SubsetSum;
import com.example.dianzi.db.DataSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName ="bankflow_receive")
public class BankflowReceive extends Bankflow implements Serializable {
    public static String TYPE_TRANSACTION = "交易";

    @ColumnInfo(name="receive_type")
    public String receiveType = TYPE_TRANSACTION;

    public List<CashflowReceivable> match(List<CashflowReceivable>candidateMatchList) {


        if(candidateMatchList.size() == 0) {
            return null;
        }

        // reset bankflowId
        for(CashflowReceivable c : candidateMatchList) {
            c.bankflowId = 0;
        }

        int[] numbers = new int[candidateMatchList.size()];
        int sum = (int)(amount * 10);
        for(int i = 0; i < candidateMatchList.size(); i++) {
            numbers[i] = (int)(candidateMatchList.get(i).amount * 10);
            System.out.print(numbers[i] + ",");
        }
        System.out.println();
        System.out.println("numbers:" + numbers);
        System.out.println("sum:" + sum);
        SubsetSum subsetSum = new SubsetSum();
        ArrayList<ArrayList<Integer>> result = subsetSum.findAllSubsets(numbers, sum);

        if(result.size() > 0) {
            List<CashflowReceivable> resultList = new ArrayList<CashflowReceivable>();
            ArrayList<Integer> firstMatchPath = result.get(0);
            System.out.println("firstMatchPath:" + firstMatchPath);
            for(CashflowReceivable c : candidateMatchList) {
                if(firstMatchPath.contains((int) (c.amount * 10))) {
                    resultList.add(c);
                    c.bankflowId = -1;
                }
            }
            return resultList;
        }

        return null;
    }

    public List<CashflowReceivable> getCandidateCashflowReceivableList(List<CashflowReceivable> unReceivedCashflowReceivalbeList) {
        List<CashflowReceivable>  candidateMatchList = new ArrayList<CashflowReceivable>();
        for(CashflowReceivable c : unReceivedCashflowReceivalbeList) {
            if(c.name.equals(name) && c.flowDate.compareTo(flowDate) < 0) {
                candidateMatchList.add(c);
            }
        }

        return candidateMatchList;

    }

}
