package com.example.dianzi.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dianzi.MainApplication;
import com.example.dianzi.R;
import com.example.dianzi.activity.MainActivity;
import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.databinding.FragmentTopBinding;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.db.DataSet;
import com.example.dianzi.entity.Statistics;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class TopFragment extends Fragment {

    private FragmentTopBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTopBinding.inflate(inflater, container, false);
        showSummary(binding);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).setTitle(R.string.bottom_nav_top);


    }

    public void showSummary(FragmentTopBinding binding) {
      //  MainActivity activity = (MainActivity)getActivity();
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.obj != null) {
                    Statistics statistics = DataSet.getInstance().statistics;
                    if(statistics.totalTransactionNumber > 0) {
                        binding.totalTransactionNumber.setText(statistics.totalTransactionNumber + "");
                        binding.totalProfit.setText(CommonFunc.getAmountTextInt(statistics.getTotalProfit()));
                        binding.totalDays.setText(statistics.getTotalDays() + "");
                        binding.dailyProfit.setText(CommonFunc.getAmountTextInt(statistics.getDailyProfit()));

                        binding.principle.setText(CommonFunc.getAmountTextInt(statistics.totalPrinciple));
                        binding.cashAvailable.setText(CommonFunc.getAmountTextInt(statistics.getCashAvailable()));
                        binding.totalUnreceived.setText(CommonFunc.getAmountTextInt(statistics.totalUnreceivedCashflow));

//                        List<PieEntry> entries = new ArrayList<PieEntry>();
//                        entries.add(new PieEntry(statistics.getCashAvailable(), getResources().getString(R.string.cash_available)));
//                        entries.add(new PieEntry(statistics.totalUnreceivedCashflow, getResources().getString(R.string.total_unreceived)));
//                        PieDataSet pieDataSet = new PieDataSet(entries, getResources().getString(R.string.cash));
//                        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//                        binding.pieChartCash.setData(new PieData(pieDataSet));
//                        binding.pieChartCash.getLegend().setEnabled(false);
//                        binding.pieChartCash.getDescription().setText("");

                        List<BarEntry> entries = new ArrayList<BarEntry>();
                        for(int i = 0; i < statistics.monthlyProfit.size(); i++) {
                            entries.add(new BarEntry(i, statistics.monthlyProfit.get(i)));
                        }

                        BarDataSet barDataSet = new BarDataSet(entries, "profit");
                        BarData barData = new BarData(barDataSet);
                        binding.chartMonthlyProfit.setData(barData);
                        binding.chartMonthlyProfit.invalidate();
                    }

                }
            }
        };

        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.getStatistics(handler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}