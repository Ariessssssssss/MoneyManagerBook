package com.example.moneymanager.view.fragment.frag_chart;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneymanager.model.dao.DBManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
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

        // 设置中心文字
        pieChart.setCenterText(title);
        pieChart.setCenterTextSize(14f);
        pieChart.setDrawCenterText(true);

        // 设置中心圆洞
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        // 设置图例
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false); // 禁用默认图例

        // 启用旋转和点击高亮
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
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
