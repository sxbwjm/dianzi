package com.example.dianzi.fragment;

import android.graphics.Color;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
                        //binding.totalProfit.setText(CommonFunc.getAmountTextInt(statistics.getTotalProfit()));
                        binding.totalDays.setText(statistics.getTotalDays() + "");
                        binding.dailyProfit.setText(CommonFunc.getAmountTextInt(statistics.getDailyProfit()));

                       // binding.principle.setText(CommonFunc.getAmountTextInt(statistics.totalPrinciple));
                      //  binding.cashAvailable.setText(CommonFunc.getAmountTextInt(statistics.getCashAvailable()));
                     //   binding.totalUnreceived.setText(CommonFunc.getAmountTextInt(statistics.totalUnreceivedCashflow));

                        buildPrinciplePieChart(statistics);
                        buildCashPieChart(statistics);
                        buildMonthlyProfitChart(statistics);
                        buildDailylyAvailableCashChart(statistics);
                    }


                }
            }
        };

        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.getStatistics(handler);
    }

    private void buildPrinciplePieChart(Statistics statistics) {
        List<PieEntry> entries = new ArrayList<PieEntry>();
        entries.add(new PieEntry(statistics.getTotalProfit(), getResources().getString(R.string.total_profit)));
        entries.add(new PieEntry(statistics.totalPrinciple, getResources().getString(R.string.principle)));
        PieDataSet pieDataSet = new PieDataSet(entries, getResources().getString(R.string.cash));
        pieDataSet.setValueTextSize(12);
        pieDataSet.setValueTextColor(Color.WHITE);


        pieDataSet.setColors(Color.GREEN, Color.BLUE);
        PieChart chart =   binding.chartPrincipleProfit;
        chart.setData(new PieData(pieDataSet));
        chart.getLegend().setEnabled(false);
        chart.getDescription().setText("");
        chart.setEntryLabelTextSize(8);
        chart.setCenterText(getResources().getString(R.string.cash));
        chart.setHoleRadius(40);
        chart.setTransparentCircleRadius(40);

        chart.invalidate();
    }

    private void buildCashPieChart(Statistics statistics) {
        List<PieEntry> entries = new ArrayList<PieEntry>();
        entries.add(new PieEntry(statistics.getCashAvailable(), getResources().getString(R.string.cash_status_available)));
        entries.add(new PieEntry(statistics.totalUnreceivedCashflow, getResources().getString(R.string.cash_status_unreceived)));
        PieDataSet pieDataSet = new PieDataSet(entries, getResources().getString(R.string.cash_status));
        pieDataSet.setValueTextSize(10);
        pieDataSet.setValueTextColor(Color.WHITE);


        pieDataSet.setColors(Color.BLUE, Color.RED);
        PieChart chart =   binding.chartCashStatus;
        chart.setData(new PieData(pieDataSet));
        chart.getLegend().setEnabled(false);
        chart.getDescription().setText("");
        chart.setEntryLabelTextSize(8);
        chart.setCenterText(getResources().getString(R.string.cash_status));
        chart.setHoleRadius(40);
        chart.setTransparentCircleRadius(40);

        chart.invalidate();
    }

    private void buildMonthlyProfitChart(Statistics statistics) {
            List<BarEntry> entries = new ArrayList<BarEntry>();
            for(int i = 0; i < statistics.monthlyProfitList.size(); i++) {
                entries.add(new BarEntry(i + 1, statistics.monthlyProfitList.get(i).amount));
            }

            BarDataSet barDataSet = new BarDataSet(entries, "profit");
            barDataSet.setColor(Color.BLUE);
            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(0.8f);
            barData.setValueTextSize(10);


            BarChart barChart = binding.chartMonthlyProfit;

            barChart.getLegend().setEnabled(false);
            barChart.getAxisRight().setEnabled(false);
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getXAxis().setCenterAxisLabels(false);
          //  barChart.getXAxis().setAxisMinimum(1f);
            barChart.getXAxis().setGranularity(1f);
            barChart.getXAxis().setDrawGridLines(false);
           // barChart.getXAxis().setDrawAxisLine(false);
            barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            barChart.getDescription().setEnabled(false);


            barChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    int i = (int)value;
                    if(i == 0 || i > statistics.monthlyProfitList.size()) {
                        return "";
                    }else
                    {
                        String label = statistics.monthlyProfitList.get(i - 1).month;
                        return label.substring(0,4) + "/" + label.substring(4,6);
                    }
                }
            });
            barChart.setData(barData);
            barChart.invalidate();

    }

    private void buildDailylyAvailableCashChart(Statistics statistics) {
        List<Entry> entries = new ArrayList<Entry>();
        for(int i = 0; i < statistics.dailyAvailableCashList.size(); i++) {
            entries.add(new BarEntry(i, statistics.dailyAvailableCashList.get(i).amount));
        }

        LineDataSet lineDataSet = new LineDataSet(entries,"Cash");
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawCircles(false);
        LineData lineData = new LineData(lineDataSet);
        LineChart chart = binding.chartDailyAvailableCash;

        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
       // chart.getAxisLeft().setDrawGridLines(false);
      //  chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getDescription().setEnabled(false);
        chart.setData(lineData);

        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int i = (int)value;
                if(i >= statistics.dailyAvailableCashList.size()) {
                    return "";
                }else
                {
                    String label = statistics.dailyAvailableCashList.get(i).date;
                    return label.substring(4,6) + "/" + label.substring(6,8);
                }
            }
        });

        chart.invalidate();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}