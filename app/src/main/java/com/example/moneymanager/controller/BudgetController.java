package com.example.moneymanager.controller;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 预算控制器
 * 负责处理预算相关的业务逻辑
 */
public class BudgetController {
    private Context context;
    private SharedPreferences preferences;

    public BudgetController(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences("money", Context.MODE_PRIVATE);
    }

    /**
     * 获取预算
     * @return 预算金额
     */
    public float getBudget() {
        return preferences.getFloat("budget", 0.0f);
    }

    /**
     * 设置预算
     * @param budget 预算金额
     * @return 是否设置成功
     */
    public boolean setBudget(float budget) {
        if (budget < 0) {
            return false;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("budget", budget);
        return editor.commit();
    }

    /**
     * 计算预算剩余金额
     * @param year 年份
     * @param month 月份
     * @return 剩余预算
     */
    public float getRemainingBudget(int year, int month) {
        float budget = getBudget();
        if (budget <= 0) {
            return 0.0f;
        }

        DetailsController detailsController = new DetailsController(context);
        float monthlyExpense = detailsController.getMonthlySum(year, month, 0);

        float remaining = budget - monthlyExpense;
        return Math.max(remaining, 0.0f);
    }

    /**
     * 计算预算使用百分比
     * @param year 年份
     * @param month 月份
     * @return 预算使用百分比 (0-100)
     */
    public float getBudgetUsagePercentage(int year, int month) {
        float budget = getBudget();
        if (budget <= 0) {
            return 0.0f;
        }

        DetailsController detailsController = new DetailsController(context);
        float monthlyExpense = detailsController.getMonthlySum(year, month, 0);

        float percentage = (monthlyExpense / budget) * 100;
        return Math.min(percentage, 100.0f);
    }

    /**
     * 检查是否超出预算
     * @param year 年份
     * @param month 月份
     * @return 是否超出预算
     */
    public boolean isBudgetExceeded(int year, int month) {
        float budget = getBudget();
        if (budget <= 0) {
            return false;
        }

        DetailsController detailsController = new DetailsController(context);
        float monthlyExpense = detailsController.getMonthlySum(year, month, 0);

        return monthlyExpense > budget;
    }

    /**
     * 计算日均可用预算
     * @param year 年份
     * @param month 月份
     * @param currentDay 当前日期
     * @return 日均可用预算
     */
    public float getDailyBudget(int year, int month, int currentDay) {
        float remaining = getRemainingBudget(year, month);

        // 获取当月总天数
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        // 计算剩余天数
        int remainingDays = daysInMonth - currentDay + 1;
        if (remainingDays <= 0) {
            return 0.0f;
        }

        return remaining / remainingDays;
    }
}