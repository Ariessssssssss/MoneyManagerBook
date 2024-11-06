package com.example.moneymanager.model.service;

import android.content.Context;

import com.example.moneymanager.model.bean.BarChartItemBean;
import com.example.moneymanager.model.bean.ChartItemBean;
import com.example.moneymanager.model.dao.ChartDAO;
import com.example.moneymanager.model.dao.DBManager;
import com.example.moneymanager.model.dao.DetailsDAO;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.List;

/**
 * 图表服务类
 * 提供图表相关的业务逻辑处理
 */
public class ChartService {
    private ChartDAO chartDAO;
    private DetailsDAO detailsDAO; // 添加DetailsDAO引用

    public ChartService(Context context) {
        SQLiteDatabase db = DBManager.getDatabase();
        this.chartDAO = new ChartDAO(db);
        this.detailsDAO = new DetailsDAO(db); // 初始化DetailsDAO
    }

    /**
     * 获取图表列表
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 图表数据列表
     */
    public List<ChartItemBean> getChartList(int year, int month, int kind) {
        return chartDAO.getChartList(year, month, kind);
    }

    /**
     * 获取柱状图数据
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 柱状图数据列表
     */
    public List<BarChartItemBean> getBarChartData(int year, int month, int kind) {
        return chartDAO.getBarChartData(year, month, kind);
    }
    /**
     * 获取最近几天的账单数据
     * @param days 天数
     * @param kind 收入或支出类型 (0:支出, 1:收入)
     * @return 账单数据游标
     */
    public Cursor getAccountListByRecentDays(int days, int kind) {
        return chartDAO.getAccountListByRecentDays(days, kind);
    }

    /**
     * 获取指定年份的账单数据
     * @param year 年份
     * @param kind 收入或支出类型 (0:支出, 1:收入)
     * @return 账单数据游标
     */
    public Cursor getAccountListByYear(int year, int kind) {
        return chartDAO.getAccountListByYear(year, kind);
    }


    /**
     * 获取预算图表列表
     * @param year 年份
     * @param month 月份
     * @param totalBudget 总预算
     * @return 图表数据列表
     */
    public List<ChartItemBean> getBudgetChartList(int year, int month, float totalBudget) {
        return chartDAO.getBudgetChartList(year, month, totalBudget);
    }

    /**
     * 获取某月最大支出/收入
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 最大金额
     */
    public float getMaxMoneyOneMonth(int year, int month, int kind) {
        return chartDAO.getMaxMoneyOneMonth(year, month, kind);
    }
    /**
     * 获取某月每日支出/收入总数
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 图表数据列表
     */
    public List<BarChartItemBean> getMoneyOneDay(int year, int month, int kind) {
        return chartDAO.getMoneyOneDay(year, month, kind);
    }

    /**
     * 获取年份列表
     * @return 年份列表
     */
    public List<Integer> getYearList() {
        return chartDAO.getYearList();
    }

    /**
     * 获取指定月份的总金额
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 总金额
     */
    public float getMonthlySum(int year, int month, int kind) {
        // 从DetailsDAO获取月度总金额
        return detailsDAO.getSumMoneyOneMonth(year, month, kind);
    }

    /**
     * 分析月度财务情况
     * @param year 年份
     * @param month 月份
     * @return 财务分析结果
     */
    public MonthlyAnalysis analyzeMonthlyData(int year, int month) {
        float income = getMonthlySum(year, month, 1);
        float expense = getMonthlySum(year, month, 0);
        float balance = income - expense;
        boolean isPositiveBalance = balance >= 0;

        List<ChartItemBean> expenseItems = getChartList(year, month, 0);
        // 计算主要支出类别
        String mainExpenseCategory = "";
        float mainExpenseAmount = 0;

        if (expenseItems != null && !expenseItems.isEmpty()) {
            ChartItemBean maxItem = expenseItems.get(0); // 已按金额降序排列
            mainExpenseCategory = maxItem.getType();
            mainExpenseAmount = maxItem.getTotalMoney();
        }

        return new MonthlyAnalysis(income, expense, balance, isPositiveBalance,
                mainExpenseCategory, mainExpenseAmount);
    }

    /**
     * 月度财务分析结果类
     */
    public static class MonthlyAnalysis {
        private float income;
        private float expense;
        private float balance;
        private boolean isPositiveBalance;
        private String mainExpenseCategory;
        private float mainExpenseAmount;

        public MonthlyAnalysis(float income, float expense, float balance,
                               boolean isPositiveBalance, String mainExpenseCategory,
                               float mainExpenseAmount) {
            this.income = income;
            this.expense = expense;
            this.balance = balance;
            this.isPositiveBalance = isPositiveBalance;
            this.mainExpenseCategory = mainExpenseCategory;
            this.mainExpenseAmount = mainExpenseAmount;
        }

        public float getIncome() {
            return income;
        }

        public float getExpense() {
            return expense;
        }

        public float getBalance() {
            return balance;
        }

        public boolean isPositiveBalance() {
            return isPositiveBalance;
        }

        public String getMainExpenseCategory() {
            return mainExpenseCategory;
        }

        public float getMainExpenseAmount() {
            return mainExpenseAmount;
        }
    }
}