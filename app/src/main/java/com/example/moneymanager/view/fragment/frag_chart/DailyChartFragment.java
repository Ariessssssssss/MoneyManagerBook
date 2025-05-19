package com.example.moneymanager.view.fragment.frag_chart;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.ChartController;
import com.example.moneymanager.utils.AIAnalysisServiceUtil;
import com.example.moneymanager.view.fragment.frag_chart.BasePieChartFragment;
import com.github.mikephil.charting.charts.PieChart;

import java.util.HashMap;
import java.util.Map;

public class DailyChartFragment extends BasePieChartFragment {

    private PieChart recentPieChart;
    private TextView recentIncomeTv, recentExpenseTv;
    private ChartController chartController;
    private TextView tvAnalysisReport;
    private ProgressBar progressAnalysis;
    private ImageButton btnRefreshAnalysis;
    private Map<String, Float> mExpenseMap = new HashMap<>();
    private Map<String, Float> mIncomeMap = new HashMap<>();
    private float mTotalExpense = 0f;
    private float mTotalIncome = 0f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_chart, container, false);
        chartController = new ChartController(getContext());
        initView(view);
        return view;
    }

    private void initView(View view) {
        recentPieChart = view.findViewById(R.id.chart_pie_recent);
        recentIncomeTv = view.findViewById(R.id.chart_tv_recent_income);
        recentExpenseTv = view.findViewById(R.id.chart_tv_recent_expense);

        // 初始化饼图设置
        setupPieChart(recentPieChart, "近7日支出分类");
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
        loadRecentData();
    }

    private void loadRecentData() {
        // 获取近7天的数据
        mExpenseMap.clear();
        mIncomeMap.clear();
        mTotalExpense = 0f;
        mTotalIncome = 0f;
        Cursor cursor = chartController.getAccountListByRecentDays(7, 0); // 0表示支出
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
                mTotalExpense += money;

                // 按类型累加金额
                if (mExpenseMap.containsKey(typename)) {
                    mExpenseMap.put(typename, mExpenseMap.get(typename) + money);
                } else {
                    mExpenseMap.put(typename, money);
                }
            }
            cursor.close();
        }

        // 查询收入数据
        cursor = chartController.getAccountListByRecentDays(7, 1); // 1表示收入
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
                mTotalIncome += money;
                // 按类型累加金额
                if (mIncomeMap.containsKey(typename)) {
                    mIncomeMap.put(typename, mIncomeMap.get(typename) + money);
                } else {
                    mIncomeMap.put(typename, money);
                }
            }
            cursor.close();
        }
        // 设置收入支出文本
        recentExpenseTv.setText("支出: ¥" + String.format("%.2f", mTotalExpense));
        recentIncomeTv.setText("收入: ¥" + String.format("%.2f", mTotalIncome));

        // 生成饼图数据
        setCombinedPieChartData(recentPieChart, mIncomeMap, mExpenseMap);

        /*Map<String, Float> expenseMap = new HashMap<>();
        Map<String, Float> incomeMap = new HashMap<>();
        float totalExpense = 0f;
        Cursor cursor = chartController.getAccountListByRecentDays(7, 0); // 0表示支出
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
        cursor = chartController.getAccountListByRecentDays(7, 1); // 1表示收入
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
        }*/

        /*// 设置收入支出文本
        recentExpenseTv.setText("支出: ¥" + String.format("%.2f", totalExpense));
        recentIncomeTv.setText("收入: ¥" + String.format("%.2f", totalIncome));

        // 生成饼图数据
        setCombinedPieChartData(recentPieChart, incomeMap, expenseMap);*/
        generateAnalysisReport();
    }
    private void generateAnalysisReport() {
        // 显示加载中
        progressAnalysis.setVisibility(View.VISIBLE);
        tvAnalysisReport.setVisibility(View.GONE);

        // 请求AI分析报告
        AIAnalysisServiceUtil.getInstance(getContext()).generateDailyAnalysisReport(
                mExpenseMap, mIncomeMap, mTotalExpense, mTotalIncome,
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