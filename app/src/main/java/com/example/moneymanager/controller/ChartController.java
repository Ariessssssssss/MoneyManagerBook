// app/src/main/java/com/example/moneymanager/controller/ChartController.java
package com.example.moneymanager.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.moneymanager.model.bean.BarChartItemBean;
import com.example.moneymanager.model.bean.ChartItemBean;
import com.example.moneymanager.model.service.ChartService;

import net.sqlcipher.Cursor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图表控制器
 * 负责处理图表相关的业务逻辑，作为View和Model的桥梁
 */
public class ChartController {
    private ChartService chartService;
    private Context context;

    public ChartController(Context context) {
        this.context = context.getApplicationContext();
        this.chartService = new ChartService(context);
    }

    /**
     * 获取指定年月饼图数据列表
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 饼图数据列表
     */
    public List<ChartItemBean> getChartListFromDetailstb(int year, int month, int kind) {
        return chartService.getChartList(year, month, kind);
    }
    /**
     * 获取最近几天的账单数据
     * @param days 天数
     * @param kind 收入或支出类型 (0:支出, 1:收入)
     * @return 账单数据游标
     */
    public Cursor getAccountListByRecentDays(int days, int kind) {
        return chartService.getAccountListByRecentDays(days, kind);
    }

    /**
     * 获取指定年份的账单数据
     * @param year 年份
     * @param kind 收入或支出类型 (0:支出, 1:收入)
     * @return 账单数据游标
     */
    public Cursor getAccountListByYear(int year, int kind) {
        return chartService.getAccountListByYear(year, kind);
    }
    /**
     * 获取柱状图数据列表
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 柱状图数据列表
     */
    public List<BarChartItemBean> getBarChartItems(int year, int month, int kind) {
        return chartService.getBarChartData(year, month, kind);
    }

    /**
     * 获取预算图表数据
     * @param year 年份
     * @param month 月份
     * @param totalBudget 总预算
     * @return 图表数据列表
     */
    public List<ChartItemBean> getBudgetChartData(int year, int month, float totalBudget) {
        return chartService.getBudgetChartList(year, month, totalBudget);
    }

    /**
     * 获取某月最大支出/收入
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 最大金额
     */
    public float getMaxMoneyInMonth(int year, int month, int kind) {
        return chartService.getMaxMoneyOneMonth(year, month, kind);
    }
    /**
     * 获取某月每日支出/收入总数
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 图表数据列表
     */
    public List<BarChartItemBean> getMoneyOneDay(int year, int month, int kind) {
        return chartService.getMoneyOneDay(year, month, kind);
    }

    /**
     * 获取所有年份列表
     * @return 年份列表
     */
    public List<Integer> getAllYearList() {
        return chartService.getYearList();
    }

    /**
     * 获取月度图表数据
     * @param year 年份
     * @param month 月份
     * @return 包含收入支出数据和图表数据的Map
     */
    public Map<String, Object> getMonthlyChartData(int year, int month) {
        Map<String, Object> result = new HashMap<>();

        // 获取收入支出总额
        float totalIncome = chartService.getMonthlySum(year, month, 1);
        float totalExpense = chartService.getMonthlySum(year, month, 0);

        // 获取饼图数据
        List<ChartItemBean> expenseChartList = getChartListFromDetailstb(year, month, 0);

        result.put("totalIncome", totalIncome);
        result.put("totalExpense", totalExpense);
        result.put("expenseChartList", expenseChartList);

        return result;
    }

    /**
     * 获取预算图表数据
     * @param year 年份
     * @param month 月份
     * @param prefs SharedPreferences实例，用于获取预算值
     * @return 包含预算数据的Map
     */
    public Map<String, Object> getBudgetData(int year, int month, SharedPreferences prefs) {
        Map<String, Object> result = new HashMap<>();

        // 获取预算
        float totalBudget = prefs.getFloat("budget", 0.0f);

        // 获取支出
        float totalExpense = chartService.getMonthlySum(year, month, 0);

        // 计算剩余预算
        float remainingBudget = totalBudget - totalExpense;
        if (remainingBudget < 0) remainingBudget = 0;

        // 获取预算占比饼图数据
        List<ChartItemBean> budgetChartList = getBudgetChartData(year, month, totalBudget);

        result.put("totalBudget", totalBudget);
        result.put("totalExpense", totalExpense);
        result.put("remainingBudget", remainingBudget);
        result.put("budgetChartList", budgetChartList);

        return result;
    }

    /**
     * 分析月度财务情况
     * @param year 年份
     * @param month 月份
     * @return 财务分析结果
     */
    public ChartService.MonthlyAnalysis analyzeMonthlyData(int year, int month) {
        return chartService.analyzeMonthlyData(year, month);
    }
}