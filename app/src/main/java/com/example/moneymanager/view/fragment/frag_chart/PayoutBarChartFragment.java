package com.example.moneymanager.view.fragment.frag_chart;

import android.graphics.Color;
import android.view.View;

import com.example.moneymanager.model.bean.BarChartItemBean;
import com.example.moneymanager.model.dao.DBManager;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class PayoutBarChartFragment extends BaseBarChartFragment {
    int kind = 0;
    BarDataSet barDataSet;

    @Override
    public void onResume() {
        super.onResume();
        loadData(year, month, kind);
    }

    @Override
    protected void setAxisData(int year, int month) {
        List<IBarDataSet> setList = new ArrayList<>();
        List<BarChartItemBean> list = chartController.getMoneyOneDay(year, month, kind);
        if (list.size() == 0) {
            barChart.setVisibility(View.GONE);
            chartTv.setVisibility(View.VISIBLE);
        } else {
            barChart.setVisibility(View.VISIBLE);
            chartTv.setVisibility(View.GONE);
            //设置柱子的位置和高度
            List<BarEntry> barEntryList = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                BarEntry entry = new BarEntry(i, 0.0f);
                barEntryList.add(entry);
            }

            for (int i = 0; i < list.size(); i++) {
                BarChartItemBean itemBean = list.get(i);
                int day = itemBean.getDay();
                int xIndex = day - 1;
                BarEntry barEntry = barEntryList.get(xIndex);
                barEntry.setY(itemBean.getSumMoney());
            }
            barDataSet = new BarDataSet(barEntryList, "");
            //依次设置每次柱块的颜色,int... 类型
            barDataSet.setColors(Color.RED);
            //设置住块上文字的颜色
            barDataSet.setValueTextColor(Color.BLACK);
            //设置住块上文字的大小
            barDataSet.setValueTextSize(8f);
            ValueFormatter valueFormatter = new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    if (value == 0) {
                        return "";
                    }
                    return value + "";
                }
            };
            barDataSet.setValueFormatter(valueFormatter);
            setList.add(barDataSet);
            BarData barData = new BarData(setList);
            barData.setBarWidth(0.4f);
            barChart.setData(barData);
        }

    }

    @Override
    protected void setYAxis(int year, int month) {
        barChart.getAxisRight().setEnabled(false);
        YAxis yAxis = barChart.getAxisLeft();
        float maxMoney = chartController.getMaxMoneyInMonth(year, month, kind);
        float max = (float) Math.ceil(maxMoney);
        yAxis.setAxisMaximum(max);
        yAxis.setAxisMinimum(0f);
        yAxis.setEnabled(false);
        //不显示图例
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);
    }

    @Override
    public void setDate(int year, int month) {
        super.setDate(year, month);
        loadData(year,month,kind);
    }
}