package com.example.moneymanager.model.service;

import android.content.Context;

import com.example.moneymanager.model.dao.BudgetDAO;
import com.example.moneymanager.model.dao.DBManager;
import net.sqlcipher.database.SQLiteDatabase;

/**
 * 预算服务类
 * 提供预算相关的业务逻辑处理
 */
public class BudgetService {
    private BudgetDAO budgetDAO;

    public BudgetService(Context context) {
        SQLiteDatabase db = DBManager.getDatabase();
        this.budgetDAO = new BudgetDAO(db);
    }

    /**
     * 设置月度预算
     * @param year 年份
     * @param month 月份
     * @param budget 预算金额
     * @return 是否设置成功
     */
    public boolean setMonthlyBudget(int year, int month, float budget) {
        if (budget < 0) {
            return false;
        }
        return budgetDAO.setBudget(year, month, budget);
    }

    /**
     * 获取月度预算
     * @param year 年份
     * @param month 月份
     * @return 预算金额
     */
    public float getMonthlyBudget(int year, int month) {
        return budgetDAO.getBudget(year, month);
    }

    /**
     * 计算预算使用情况
     * @param year 年份
     * @param month 月份
     * @param expense 当前支出
     * @return 预算使用情况
     */
    public BudgetStatus analyzeBudgetStatus(int year, int month, float expense) {
        float budget = getMonthlyBudget(year, month);
        if (budget <= 0) {
            return new BudgetStatus(0, 0, 0);
        }

        float remaining = budget - expense;
        float usagePercentage = expense / budget * 100;

        return new BudgetStatus(budget, remaining, usagePercentage);
    }

    /**
     * 预算状态类
     */
    public static class BudgetStatus {
        private float budget;
        private float remaining;
        private float usagePercentage;

        public BudgetStatus(float budget, float remaining, float usagePercentage) {
            this.budget = budget;
            this.remaining = remaining;
            this.usagePercentage = usagePercentage;
        }

        public float getBudget() {
            return budget;
        }

        public float getRemaining() {
            return remaining;
        }

        public float getUsagePercentage() {
            return usagePercentage;
        }

        public boolean isOverBudget() {
            return remaining < 0;
        }
    }
}