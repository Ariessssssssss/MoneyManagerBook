package com.example.moneymanager.view.fragment.frag_chart;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.ImageButton;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.ChartController;
import com.example.moneymanager.utils.AIAnalysisServiceUtil;
import com.example.moneymanager.view.fragment.frag_chart.BasePieChartFragment;
import com.github.mikephil.charting.charts.PieChart;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class YearlyChartFragment extends BasePieChartFragment {

    private PieChart yearPieChart;
    private TextView yearIncomeTv, yearExpenseTv;
    private TextView tvAnalysisReport;
    private ProgressBar progressAnalysis;
    private ImageButton btnRefreshAnalysis;
    private ChartController chartController;
    private Map<String, Float> expenseMap = new HashMap<>();
    private Map<String, Float> incomeMap = new HashMap<>();
    private float totalIncome, totalExpense;
    private int currentYear;
    private List<Float> monthlyExpenses = new ArrayList<>();
    private List<Float> monthlyIncomes = new ArrayList<>();

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
        
        // 初始化AI分析报告相关视图
        View analysisCard = view.findViewById(R.id.card_ai_analysis);
        tvAnalysisReport = analysisCard.findViewById(R.id.tv_analysis_report);
        progressAnalysis = analysisCard.findViewById(R.id.progress_analysis);
        btnRefreshAnalysis = analysisCard.findViewById(R.id.btn_refresh_analysis);

        btnRefreshAnalysis.setOnClickListener(v -> {
            generateAnalysisReport();
        });
    }

    @Override
    protected void loadData() {
        loadYearData();
        collectMonthlyData();
        generateAnalysisReport();
    }

    private void loadYearData() {
        // 获取当前年份
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);

        // 查询支出数据
        expenseMap.clear();
        totalExpense = 0f;
        Cursor cursor = chartController.getAccountListByYear(currentYear, 0); // 0表示支出
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
        incomeMap.clear();
        totalIncome = 0f;
        cursor = chartController.getAccountListByYear(currentYear, 1); // 1表示收入
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
                totalIncome += money;
                // 按类型累加金额
                if (incomeMap.containsKey(typename)) {
                    incomeMap.put(typename, incomeMap.get(typename) + money);
                } else {
                    incomeMap.put(typename, money);
                }
            }
            cursor.close();
        }

        // 设置收入支出文本
        yearExpenseTv.setText("支出: ¥" + String.format("%.2f", totalExpense));
        yearIncomeTv.setText("收入: ¥" + String.format("%.2f", totalIncome));

        // 生成饼图数据
        setCombinedPieChartData(yearPieChart, incomeMap, expenseMap);
    }

    private void collectMonthlyData() {
        // 收集月度数据用于年度趋势分析
        monthlyExpenses.clear();
        monthlyIncomes.clear();
        
        for (int month = 1; month <= 12; month++) {
            float monthExpense = chartController.getMonthlySum(currentYear, month, 0);
            float monthIncome = chartController.getMonthlySum(currentYear, month, 1);
            
            monthlyExpenses.add(monthExpense);
            monthlyIncomes.add(monthIncome);
        }
    }

    private void generateAnalysisReport() {
        // 显示加载中
        progressAnalysis.setVisibility(View.VISIBLE);
        tvAnalysisReport.setVisibility(View.GONE);

        // 请求AI分析报告
        AIAnalysisServiceUtil.getInstance(getContext()).generateYearlyAnalysisReport(
                expenseMap, incomeMap, totalExpense, totalIncome,
                monthlyExpenses, monthlyIncomes,
                new AIAnalysisServiceUtil.AIAnalysisCallback() {
                    @Override
                    public void onAnalysisReady(String analysisText) {
                        // 在UI线程更新界面
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                progressAnalysis.setVisibility(View.GONE);
                                tvAnalysisReport.setVisibility(View.VISIBLE);
                                tvAnalysisReport.setText(analysisText);
                            });
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // 在UI线程显示错误
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                progressAnalysis.setVisibility(View.GONE);
                                tvAnalysisReport.setVisibility(View.VISIBLE);
                                tvAnalysisReport.setText("生成分析报告失败，请稍后重试。\n错误：" + errorMessage);
                            });
                        }
                    }
                }
        );
    }

}