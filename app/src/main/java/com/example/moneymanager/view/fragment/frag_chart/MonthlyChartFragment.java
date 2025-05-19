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
import android.widget.ProgressBar;
import android.widget.ImageButton;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.ChartController;
import com.example.moneymanager.model.bean.ChartItemBean;
import com.example.moneymanager.utils.AIAnalysisServiceUtil;
import com.github.mikephil.charting.charts.PieChart;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class MonthlyChartFragment extends BasePieChartFragment {

    private PieChart monthPieChart, budgetPieChart;
    private TextView monthIncomeTv, monthExpenseTv;
    private TextView tvAnalysisReport;
    private ProgressBar progressAnalysis;
    private ImageButton btnRefreshAnalysis;
    private SharedPreferences preferences;
    private ChartController chartController;
    private Map<String, Float> expenseMap = new HashMap<>();
    private Map<String, Float> incomeMap = new HashMap<>();
    private float totalIncome, totalExpense;

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
        setupPieChart(monthPieChart, "本月收支分类");
        setupPieChart(budgetPieChart, "预算占比");
        
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
        loadMonthData();
        loadBudgetData();
        generateAnalysisReport();
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
        // 查询收入分类数据
        List<ChartItemBean> incomeChartList = chartController.getChartListFromDetailstb(year, month, 1);

        // 设置收入支出文本
        monthExpenseTv.setText("支出: ¥" + String.format("%.2f", totalExpense));
        monthIncomeTv.setText("收入: ¥" + String.format("%.2f", totalIncome));

        // 存储数据以便生成分析报告
        expenseMap.clear();
        incomeMap.clear();
        
        // 生成收入饼图数据
        for (ChartItemBean item : incomeChartList) {
            incomeMap.put(item.getType(), item.getTotalMoney());
        }

        // 生成饼图数据
        for (ChartItemBean item : expenseChartList) {
            expenseMap.put(item.getType(), item.getTotalMoney());
        }
        
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        
        // 使用新的合并图表方法
        setCombinedPieChartData(monthPieChart, incomeMap, expenseMap);
    }

    private void loadBudgetData() {
        // 获取当前年月
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
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

    private void generateAnalysisReport() {
        // 显示加载中
        progressAnalysis.setVisibility(View.VISIBLE);
        tvAnalysisReport.setVisibility(View.GONE);

        // 请求AI分析报告
        AIAnalysisServiceUtil.getInstance(getContext()).generateMonthlyAnalysisReport(
                expenseMap, incomeMap, totalExpense, totalIncome,
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