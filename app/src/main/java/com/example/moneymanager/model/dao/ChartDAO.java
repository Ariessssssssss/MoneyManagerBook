package com.example.moneymanager.model.dao;

import android.util.Log;

import com.example.moneymanager.model.bean.BarChartItemBean;
import com.example.moneymanager.model.bean.ChartItemBean;
import com.example.moneymanager.utils.FloatUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 图表数据访问对象
 * 负责图表相关的数据库操作
 */
public class ChartDAO {
    private static final String TAG = "ChartDAO";
    private SQLiteDatabase db;

    public ChartDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 获取饼图数据列表
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 饼图数据列表
     */
    public List<ChartItemBean> getChartList(int year, int month, int kind) {
        List<ChartItemBean> list = new ArrayList<>();
        DetailsDAO detailsDAO = new DetailsDAO(db);
        float sumMoneyOneMonth = detailsDAO.getSumMoneyOneMonth(year, month, kind);

        Cursor cursor = null;
        try {
            String sql = "select typename, sImageId, SUM(money) as total from detailstb " +
                    "where year = ? and month = ? and kind = ? group by typename " +
                    "order by total desc";
            cursor = db.rawQuery(sql, new String[]{
                    String.valueOf(year),
                    String.valueOf(month),
                    String.valueOf(kind)
            });
            while (cursor.moveToNext()) {
                int sImageId = cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                float total = cursor.getFloat(cursor.getColumnIndexOrThrow("total"));
                float percentage = FloatUtil.div(total, sumMoneyOneMonth);
                ChartItemBean bean = new ChartItemBean(sImageId, typename, percentage, total);
                list.add(bean);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting chart list: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 获取月内最大金额
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 最大金额
     */
    public float getMaxMoneyOneMonth(int year, int month, int kind) {
        float value = 0;
        Cursor cursor = null;
        try {
            String sql = "select sum(money) from detailstb where year = ? and month = ? and kind = ? " +
                    "group by day order by sum(money) desc";
            cursor = db.rawQuery(sql, new String[]{
                    String.valueOf(year),
                    String.valueOf(month),
                    String.valueOf(kind)
            });
            if (cursor.moveToFirst()) {
                value = cursor.getFloat(cursor.getColumnIndexOrThrow("sum(money)"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting max money in month: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return value;
    }
    /**
     * 获取某月每日支出/收入总数
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 图表数据列表
     */
    public List<BarChartItemBean> getMoneyOneDay(int year, int month, int kind){
        List<BarChartItemBean> list = new ArrayList<>();
        String sql = "select day,sum(money) from detailstb where year = ? and month = ? and kind = ? group by day";
        Cursor cursor = db.rawQuery(sql, new String[]{year + "", month + "", kind + ""});
        while (cursor.moveToNext()) {
            int day = cursor.getInt(cursor.getColumnIndexOrThrow("day"));
            float sum = cursor.getFloat(cursor.getColumnIndexOrThrow("sum(money)"));
            BarChartItemBean bean = new BarChartItemBean(year, month, day, sum);
            list.add(bean);
        }
        cursor.close();
        return list;
    }

    /**
     * 获取柱状图数据
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 柱状图数据列表
     */
    public List<BarChartItemBean> getBarChartData(int year, int month, int kind) {
        List<BarChartItemBean> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select day, sum(money) from detailstb where year = ? and month = ? and kind = ? group by day";
            cursor = db.rawQuery(sql, new String[]{
                    String.valueOf(year),
                    String.valueOf(month),
                    String.valueOf(kind)
            });
            while (cursor.moveToNext()) {
                int day = cursor.getInt(cursor.getColumnIndexOrThrow("day"));
                float sum = cursor.getFloat(cursor.getColumnIndexOrThrow("sum(money)"));
                BarChartItemBean bean = new BarChartItemBean(year, month, day, sum);
                list.add(bean);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting bar chart data: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
    /**
     * 获取最近几天的账单数据
     * @param days 天数
     * @param kind 收入或支出类型 (0:支出, 1:收入)
     * @return 账单数据游标
     */
    public Cursor getAccountListByRecentDays(int days, int kind) {
        // 获取当前日期
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -days);

        // 格式化日期为yyyy-MM-dd格式
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(calendar.getTime());

        // 查询最近days天的数据
        String sql = "SELECT * FROM detailstb WHERE time >= ? AND kind = ? ORDER BY time DESC";
        return db.rawQuery(sql, new String[]{startDate, String.valueOf(kind)});
    }

    /**
     * 获取指定年份的账单数据
     * @param year 年份
     * @param kind 收入或支出类型 (0:支出, 1:收入)
     * @return 账单数据游标
     */
    public Cursor getAccountListByYear(int year, int kind) {
        String sql = "SELECT * FROM detailstb WHERE year = ? AND kind = ? ORDER BY month ASC, day ASC";
        return db.rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(kind)});
    }

    /**
     * 获取预算图表数据
     * @param year 年份
     * @param month 月份
     * @param totalBudget 总预算
     * @return 图表数据列表
     */
    public List<ChartItemBean> getBudgetChartList(int year, int month, float totalBudget) {
        List<ChartItemBean> list = new ArrayList<>();
        if (totalBudget <= 0) {
            return list;
        }

        Cursor cursor = null;
        try {
            // 按类型分组查询支出
            String sql = "SELECT typename, sImageId, SUM(money) as total FROM detailstb " +
                    "WHERE year = ? AND month = ? AND kind = 0 " +
                    "GROUP BY typename ORDER BY total DESC";

            cursor = db.rawQuery(sql, new String[]{
                    String.valueOf(year),
                    String.valueOf(month)
            });
            while (cursor.moveToNext()) {
                int sImageId = cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                float total = cursor.getFloat(cursor.getColumnIndexOrThrow("total"));
                float percentage = FloatUtil.div(total, totalBudget);

                ChartItemBean bean = new ChartItemBean(sImageId, typename, percentage, total);
                list.add(bean);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting budget chart list: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 获取年份列表
     * @return 年份列表
     */
    public List<Integer> getYearList() {
        List<Integer> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select distinct(year) from detailstb order by year asc";
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int year = cursor.getInt(cursor.getColumnIndexOrThrow("year"));
                list.add(year);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting year list: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
}