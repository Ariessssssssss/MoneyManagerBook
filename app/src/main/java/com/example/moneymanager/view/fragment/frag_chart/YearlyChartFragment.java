package com.example.moneymanager.view.fragment.frag_chart;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.ChartController;
import com.example.moneymanager.view.fragment.frag_chart.BasePieChartFragment;
import com.github.mikephil.charting.charts.PieChart;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class YearlyChartFragment extends BasePieChartFragment {

    private PieChart yearPieChart;
    private TextView yearIncomeTv, yearExpenseTv;
    private ChartController chartController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yearly_chart, container, false);
        chartController = new ChartController(getContext());
        initView(view);
        return view;
    }

    private void initView(View view) {
        yearPieChart = view.findViewById(R.id.chart_pie_year);
        yearIncomeTv = view.findViewById(R.id.chart_tv_year_income);
        yearExpenseTv = view.findViewById(R.id.chart_tv_year_expense);

        // 初始化饼图设置
        setupPieChart(yearPieChart, "本年支出分类");
    }

    @Override
    protected void loadData() {
        loadYearData();
    }

    private void loadYearData() {
        // 获取当前年份
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        // 查询支出数据
        Map<String, Float> expenseMap = new HashMap<>();
        float totalExpense = 0f;
        Cursor cursor = chartController.getAccountListByYear(year, 0); // 0表示支出
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
                totalExpense += money;

                // 按类型累加金额
                if (expenseMap.containsKey(typename)) {
                    expenseMap.put(typename, expenseMap.get(typename) + money);
                } else {
                    expenseMap.put(typename, money);
                }
            }
            cursor.close();
        }

        // 查询收入数据
        float totalIncome = 0f;
        cursor = chartController.getAccountListByYear(year, 1); // 1表示收入
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
                totalIncome += money;
            }
            cursor.close();
        }

        // 设置收入支出文本
        yearExpenseTv.setText("支出: ¥" + String.format("%.2f", totalExpense));
        yearIncomeTv.setText("收入: ¥" + String.format("%.2f", totalIncome));

        // 生成饼图数据
        setPieChartData(yearPieChart, expenseMap);
    }

}