package com.example.moneymanager.view.fragment.frag_chart;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneymanager.model.dao.DBManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasePieChartFragment extends Fragment {
    protected DBManager dbManager;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbManager = new DBManager();
        loadData();
    }

    // 子类必须实现此方法加载数据
    protected void loadData() {

    }

    // 设置饼图基本属性
    protected void setupPieChart(PieChart pieChart, String title) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(20, 20, 20, 20); // 增加边距以容纳外部标签

        // 设置中心圆洞
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(60f);
        pieChart.setTransparentCircleRadius(65f);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        // 关闭中心文本显示
        pieChart.setDrawCenterText(false);

        // 设置图例
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true); // 启用图例
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(8f);
        //legend.setEnabled(false); // 禁用默认图例

        // 启用旋转和点击高亮
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        // 设置悬停高亮样式
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);

        // 只有在高亮时显示数值
        pieChart.setDrawEntryLabels(false);
    }

    // 设置新的饼图数据方法 - 统一处理收入和支出
    protected void setCombinedPieChartData(PieChart pieChart, Map<String, Float> incomeMap, Map<String, Float> expenseMap) {
        List<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        // 计算总金额
        final float totalIncome;
        float incomeTmp = 0f;
        for (Float value : incomeMap.values()) {
            incomeTmp += value;
        }
        totalIncome = incomeTmp;
        final float totalExpense;
        float expenseTmp = 0f;
        for (Float value : expenseMap.values()) {
            expenseTmp += value;
        }
        totalExpense = expenseTmp;
        float total = totalIncome + totalExpense;

        // 合并所有收入为一个项
        if (totalIncome > 0) {
            entries.add(new PieEntry(totalIncome, "收入"));
            colors.add(Color.rgb(46, 139, 87)); // 使用主题绿色代表收入
        }

        // 合并所有支出为一个项
        if (totalExpense > 0) {
            entries.add(new PieEntry(totalExpense, "支出"));
            colors.add(Color.rgb(255, 107, 107)); // 使用支出红色
        }

        // 如果没有数据，添加一个空条目
        if (entries.isEmpty()) {
            entries.add(new PieEntry(1f, "暂无数据"));
            colors.add(Color.LTGRAY);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        // 设置鼠标悬停时数值的格式和位置
        dataSet.setValueTextSize(0f); // 默认不显示数值
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);

        // 设置点击高亮处理器
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pe = (PieEntry) e;
                if (pe.getLabel().equals("收入")) {
                    showIncomeCategoryDetails(pieChart, incomeMap, totalIncome);
                } else if (pe.getLabel().equals("支出")) {
                    showExpenseCategoryDetails(pieChart, expenseMap, totalExpense);
                }
            }

            @Override
            public void onNothingSelected() {
                // 恢复原来的环形图
                setCombinedPieChartData(pieChart, incomeMap, expenseMap);
            }
        });

        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1000);
    }

    // 显示收入类别详情
    private void showIncomeCategoryDetails(PieChart pieChart, Map<String, Float> incomeMap, float totalIncome) {
        List<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        // 添加收入类别数据
        int colorIndex = 0;
        for (Map.Entry<String, Float> entry : incomeMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            // 使用绿色系列颜色 - 修复颜色方法
            int greenVariation = 46 + colorIndex * 30;
            greenVariation = Math.min(greenVariation, 255); // 确保不超过255
            colors.add(Color.rgb(greenVariation, 139, 87));
            colorIndex++;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        // 显示数值
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                float percentage = (totalIncome > 0) ? (value / totalIncome * 100) : 0;
                return String.format("¥%.0f(%.1f%%)", value, percentage);
            }
        });

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    // 显示支出类别详情
    private void showExpenseCategoryDetails(PieChart pieChart, Map<String, Float> expenseMap, float totalExpense) {
        List<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        // 添加支出类别数据
        for (Map.Entry<String, Float> entry : expenseMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            // 使用不同颜色
            colors.add(ColorTemplate.MATERIAL_COLORS[colors.size() % ColorTemplate.MATERIAL_COLORS.length]);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        // 显示数值
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                float percentage = (totalExpense > 0) ? (value / totalExpense * 100) : 0;
                return String.format("¥%.0f(%.1f%%)", value, percentage);
            }
        });

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }
    // 设置饼图数据
    protected void setPieChartData(PieChart pieChart, Map<String, Float> dataMap) {
        List<PieEntry> entries = new ArrayList<>();
        // 计算总金额
        final float total;
        float sum = 0f;
        for (Float value : dataMap.values()) {
            sum += value;
        }
        total = sum;
        // 将数据添加到饼图条目中
        for (Map.Entry<String, Float> entry : dataMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        // 如果没有数据，添加一个空条目
        if (entries.isEmpty()) {
            entries.add(new PieEntry(1f, "暂无数据"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // 设置数值显示位置为外部
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1Length(0.4f); // 设置连接线第一段长度
        dataSet.setValueLinePart2Length(0.4f); // 设置连接线第二段长度
        dataSet.setValueLineColor(Color.GRAY); // 设置连接线颜色
        dataSet.setValueLineWidth(1f); // 设置连接线宽度
        dataSet.setValueLinePart1OffsetPercentage(80f); // 调整连接线起点位置
        dataSet.setValueLineVariableLength(true); // 允许连接线长度可变

        // 设置颜色
        ArrayList<Integer> colors = new ArrayList<>();
        for (PieEntry entry : entries) {
            if (entry.getLabel().contains("剩余预算")) {
                // 剩余预算使用绿色
                colors.add(Color.rgb(76, 175, 80));
            } else if (entry.getLabel().contains("未设置预算")) {
                // 未设置预算使用灰色
                colors.add(Color.LTGRAY);
            } else {
                // 其他支出类型使用默认颜色
                colors.add(ColorTemplate.MATERIAL_COLORS[colors.size() % ColorTemplate.MATERIAL_COLORS.length]);
            }
        }
        dataSet.setColors(colors);
        // 设置数值格式化器
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // 查找对应的 PieEntry
                for (PieEntry entry : entries) {
                    if (Math.abs(entry.getValue() - value) < 0.01f) {  // 使用一个小的误差范围来匹配
                        float percentage = (total > 0) ? (value / total * 100) : 0;
                        return String.format("%s ¥%.2f %.1f%%",
                                entry.getLabel(),    // 类型名称
                                value,               // 金额
                                percentage);         // 百分比
                    }
                }
                return "";
            }
        });

        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.setUsePercentValues(false);
        pieChart.invalidate();
        pieChart.animateY(1400);
    }

}
