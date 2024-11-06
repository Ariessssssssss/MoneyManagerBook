package com.example.moneymanager.view.fragment.frag_chart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.ChartController;
import com.example.moneymanager.model.bean.ChartItemBean;
import com.github.mikephil.charting.charts.PieChart;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthlyChartFragment extends BasePieChartFragment {

    private PieChart monthPieChart, budgetPieChart;
    private TextView monthIncomeTv, monthExpenseTv;
    private SharedPreferences preferences;
    private ChartController chartController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化控制器
        chartController = new ChartController(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_chart, container, false);
        preferences = requireActivity().getSharedPreferences("money", Context.MODE_PRIVATE);
        initView(view);
        return view;
    }

    private void initView(View view) {
        monthPieChart = view.findViewById(R.id.chart_pie_month);
        budgetPieChart = view.findViewById(R.id.chart_pie_budget);
        monthIncomeTv = view.findViewById(R.id.chart_tv_month_income);
        monthExpenseTv = view.findViewById(R.id.chart_tv_month_expense);

        // 初始化饼图设置
        setupPieChart(monthPieChart, "本月支出分类");
        setupPieChart(budgetPieChart, "预算占比");
    }

    @Override
    protected void loadData() {
        loadMonthData();
        loadBudgetData();
    }

    private void loadMonthData() {
        // 获取当前月份
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        // 使用ChartController获取数据
        Map<String, Object> chartData = chartController.getMonthlyChartData(year, month);

        float totalIncome = (float) chartData.get("totalIncome");
        float totalExpense = (float) chartData.get("totalExpense");
        List<ChartItemBean> expenseChartList = (List<ChartItemBean>) chartData.get("expenseChartList");
        /*// 查询支出数据
        Map<String, Float> expenseMap = new HashMap<>();
        float totalExpense = 0f;
        Cursor cursor = dbManager.getAccountListByYearMonth(year, month, 0); // 0表示支出
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
        }*/

        /*// 查询收入数据
        float totalIncome = 0f;
        cursor = dbManager.getAccountListByYearMonth(year, month, 1); // 1表示收入
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
                totalIncome += money;
            }
            cursor.close();
        }*/

        // 设置收入支出文本
        monthExpenseTv.setText("支出: ¥" + String.format("%.2f", totalExpense));
        monthIncomeTv.setText("收入: ¥" + String.format("%.2f", totalIncome));

        // 生成饼图数据
        Map<String, Float> expenseMap = new HashMap<>();
        for (ChartItemBean item : expenseChartList) {
            expenseMap.put(item.getType(), item.getTotalMoney());
        }
        setPieChartData(monthPieChart, expenseMap);
    }

    private void loadBudgetData() {
        // 获取当前年月
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        /*// 从SharedPreferences获取预算金额
        float totalBudget = preferences.getFloat("budget", 0.0f);

        // 获取预算数据
        List<ChartItemBean> budgetList = DBManager.getBudgetChartListFromDetailstb(year, month, totalBudget);

        Map<String, Float> budgetMap = new HashMap<>();
        float totalExpense = 0.0f;

        // 计算已使用的预算
        for (ChartItemBean item : budgetList) {
            budgetMap.put(item.getType(), item.getTotalMoney());
            totalExpense += item.getTotalMoney();
        }

        // 如果有设置预算，添加剩余预算
        if (totalBudget > 0) {
            float remainingBudget = totalBudget - totalExpense;
            if (remainingBudget > 0) {
                budgetMap.put("剩余预算", remainingBudget);
            }
        }

        // 如果没有数据，显示提示信息
        if (budgetMap.isEmpty()) {
            if (totalBudget > 0) {
                budgetMap.put("剩余预算", totalBudget);
            } else {
                budgetMap.put("未设置预算", 100f);
            }
        }*/
        // 使用ChartController获取预算数据
        Map<String, Object> budgetData = chartController.getBudgetData(year, month, preferences);

        List<ChartItemBean> budgetChartList = (List<ChartItemBean>) budgetData.get("budgetChartList");

        Map<String, Float> budgetMap = new HashMap<>();
        for (ChartItemBean item : budgetChartList) {
            budgetMap.put(item.getType(), item.getTotalMoney());
        }

        // 如果没有数据，显示提示信息
        if (budgetMap.isEmpty()) {
            float totalBudget = (float) budgetData.get("totalBudget");
            if (totalBudget > 0) {
                budgetMap.put("剩余预算", totalBudget);
            } else {
                budgetMap.put("未设置预算", 100f);
            }
        }

        setPieChartData(budgetPieChart, budgetMap);
    }
}