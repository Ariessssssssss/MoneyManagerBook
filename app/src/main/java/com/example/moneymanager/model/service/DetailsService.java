package com.example.moneymanager.model.service;

import android.content.Context;

import com.example.moneymanager.model.bean.DetailsBean;
import com.example.moneymanager.model.dao.DBManager;
import com.example.moneymanager.model.dao.DetailsDAO;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.Cursor;

import java.util.List;

/**
 * 明细服务类
 * 提供明细记录相关的业务逻辑处理
 */
public class DetailsService {
    private DetailsDAO detailsDAO;

    public DetailsService(Context context) {
        SQLiteDatabase db = DBManager.getDatabase();
        this.detailsDAO = new DetailsDAO(db);
    }

    /**
     * 添加一条记录
     * @param bean 记录数据
     * @return 是否添加成功
     */
    public boolean addRecord(DetailsBean bean) {
        return detailsDAO.insertItem(bean);
    }

    /**
     * 更新一条记录
     * @param id 记录ID
     * @param bean 记录数据
     * @return 是否更新成功
     */
    public boolean updateRecord(int id, DetailsBean bean) {
        return detailsDAO.updateItem(id, bean);
    }

    /**
     * 删除一条记录
     * @param id 记录ID
     * @return 是否删除成功
     */
    public boolean deleteRecord(int id) {
        return detailsDAO.deleteItem(id);
    }

    /**
     * 获取指定日期的记录列表
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @return 记录列表
     */
    public List<DetailsBean> getDailyDetailsList(int year, int month, int day) {
        return detailsDAO.getDetailsListOneDay(year, month, day);
    }

    /**
     * 获取指定月份的记录列表
     * @param year 年份
     * @param month 月份
     * @return 记录列表
     */
    public List<DetailsBean> getMonthlyDetailsList(int year, int month) {
        return detailsDAO.getDetailsListOneMonth(year, month);
    }
    /**
     * 获取指定月份的记录列表
     * @return 记录列表
     */
    public List<Integer> getYearDetailsList() {
        return detailsDAO.getYearList();
    }
    /**
     * 根据备注搜索记录
     * @param record 备注关键字
     * @return 记录列表
     */
    public List<DetailsBean> searchRecords(String record) {
        return detailsDAO.getListByRecord(record);
    }

    /**
     * 获取指定日期的总金额
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param kind 类型（0=支出，1=收入）
     * @return 总金额
     */
    public float getDailySum(int year, int month, int day, int kind) {
        return detailsDAO.getSumMoneyOneDay(year, month, day, kind);
    }

    /**
     * 获取指定月份的总金额
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 总金额
     */
    public float getMonthlySum(int year, int month, int kind) {
        return detailsDAO.getSumMoneyOneMonth(year, month, kind);
    }

    /**
     * 获取指定年份的总金额
     * @param year 年份
     * @param kind 类型（0=支出，1=收入）
     * @return 总金额
     */
    public float getYearlySum(int year, int kind) {
        return detailsDAO.getSumMoneyOneYear(year, kind);
    }
    /**
     * 获取指定月份的记录数量
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 总金额
     */
    public int getMonthlyCountItem(int year,int month, int kind) {
        return detailsDAO.getCountItemOneMonth(year,month, kind);
    }
    /**
     * 获取所有明细记录的游标
     * @return 数据库游标
     */
    public Cursor getAllDetailsCursor() {
        return detailsDAO.getAllDetailsCursor();
    }

    /**
     * 清空所有记录
     * @return 是否清空成功
     */
    public boolean clearAllRecords() {
        return detailsDAO.deleteAllDetails();
    }

    /**
     * 分析月度消费模式
     * @param year 年份
     * @param month 月份
     * @return 消费分析结果
     */
    public ConsumptionAnalysis analyzeMonthlyConsumption(int year, int month) {
        float income = detailsDAO.getSumMoneyOneMonth(year, month, 1);
        float expense = detailsDAO.getSumMoneyOneMonth(year, month, 0);
        int incomeCount = detailsDAO.getCountItemOneMonth(year, month, 1);
        int expenseCount = detailsDAO.getCountItemOneMonth(year, month, 0);

        float avgIncomePerTransaction = incomeCount > 0 ? income / incomeCount : 0;
        float avgExpensePerTransaction = expenseCount > 0 ? expense / expenseCount : 0;

        return new ConsumptionAnalysis(income, expense, incomeCount, expenseCount,
                avgIncomePerTransaction, avgExpensePerTransaction);
    }

    /**
     * 消费分析结果类
     */
    public static class ConsumptionAnalysis {
        private float totalIncome;
        private float totalExpense;
        private int incomeCount;
        private int expenseCount;
        private float avgIncomePerTransaction;
        private float avgExpensePerTransaction;

        public ConsumptionAnalysis(float totalIncome, float totalExpense, int incomeCount,
                                   int expenseCount, float avgIncomePerTransaction,
                                   float avgExpensePerTransaction) {
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
            this.incomeCount = incomeCount;
            this.expenseCount = expenseCount;
            this.avgIncomePerTransaction = avgIncomePerTransaction;
            this.avgExpensePerTransaction = avgExpensePerTransaction;
        }

        public float getTotalIncome() {
            return totalIncome;
        }

        public float getTotalExpense() {
            return totalExpense;
        }

        public int getIncomeCount() {
            return incomeCount;
        }

        public int getExpenseCount() {
            return expenseCount;
        }

        public float getAvgIncomePerTransaction() {
            return avgIncomePerTransaction;
        }

        public float getAvgExpensePerTransaction() {
            return avgExpensePerTransaction;
        }

        public float getBalance() {
            return totalIncome - totalExpense;
        }
    }
}