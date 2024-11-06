package com.example.moneymanager.view.fragment.frag_chart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.ChartController;
import com.example.moneymanager.view.adapter.ChartItemAdapter;
import com.example.moneymanager.model.bean.ChartItemBean;
import com.example.moneymanager.model.dao.DBManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

abstract public class BaseBarChartFragment extends Fragment {
    ListView chartLv;
    int year;
    int month;
    List<ChartItemBean> mDatas;
    private ChartItemAdapter itemAdapter;
    ChartController chartController;
    BarChart barChart;
    TextView chartTv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_chart, container, false);
        chartLv = view.findViewById(R.id.frag_chart_lv);
        chartController = new ChartController(getContext());
        Bundle bundle = getArguments();
        year = bundle.getInt("year");
        month = bundle.getInt("month");
        mDatas = new ArrayList<>();
        itemAdapter = new ChartItemAdapter(getContext(), mDatas);
        chartLv.setAdapter(itemAdapter);
        addLVHeaderView();
        return view;
    }

    protected void addLVHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.item_chartfrag_top,null);
        chartLv.addHeaderView(headerView);
        barChart = headerView.findViewById(R.id.item_chartfrag_chart);
        chartTv = headerView.findViewById(R.id.item_chartfrag_top_tv);
        barChart.getDescription().setEnabled(false);
        //设置上下左右方向上的偏移量
        barChart.setExtraOffsets(20,20,20,20);
        //设置坐标轴
        setAxis(year,month);
        //设置数据
        setAxisData(year,month);
    }

    protected abstract void setAxisData(int year, int month);

    protected abstract void setYAxis(int year, int month);

    protected void setAxis(int year, int month) {
        //x轴
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.BLACK);
        xAxis.setLabelCount(31);
        xAxis.setTextSize(12f);
        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int val = (int) value;
                // 1号和15号的处理
                if (val == 0) {
                    return month + "-1";
                } else if (val == 14) {
                    return month + "-15";
                }
                // 判断是否是闰年
                boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                // 根据月份判断最后一天
                switch (month) {
                    case 2:
                        if (isLeapYear) {
                            if (val == 28) {
                                return month + "-29"; // 闰年2月的最后一天
                            }
                        } else {
                            if (val == 27) {
                                return month + "-28"; // 非闰年2月的最后一天
                            }
                        }
                        break;
                    case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                        if (val == 30) {
                            return month + "-31"; // 31天的月份
                        }
                        break;
                    case 4: case 6: case 9: case 11:
                        if (val == 29) {
                            return month + "-30"; // 30天的月份
                        }
                        break;
                }
                return "";
            }
        };
        //自定义格式
        xAxis.setValueFormatter(valueFormatter);
        xAxis.setYOffset(8f);
        setYAxis(year, month);
    }



    public void setDate(int year, int month){
        this.year = year;
        this.month = month;
        barChart.clear();
        barChart.invalidate();
        setAxis(year,month);
        setAxisData(year, month);
    };

    public void loadData(int year, int month, int kind) {
        List<ChartItemBean> list = chartController.getChartListFromDetailstb(year, month, kind);
        mDatas.clear();
        mDatas.addAll(list);
        itemAdapter.notifyDataSetChanged();
    }
}