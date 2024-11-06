package com.example.moneymanager.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * 预算数据访问对象类
 * 负责预算相关的数据库操作
 */
public class BudgetDAO {
    private static final String TAG = "BudgetDAO";
    private SQLiteDatabase db;

    public BudgetDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 设置预算
     * @param year 年份
     * @param month 月份
     * @param budget 预算金额
     * @return 是否设置成功
     */
    public boolean setBudget(int year, int month, float budget) {
        boolean success = false;
        try {
            db.beginTransaction();

            // 先检查是否已存在
            Cursor cursor = db.query("budgettb", null,
                    "year = ? AND month = ?",
                    new String[]{String.valueOf(year), String.valueOf(month)},
                    null, null, null);

            ContentValues values = new ContentValues();
            values.put("budget", budget);
            values.put("year", year);
            values.put("month", month);

            if (cursor != null && cursor.getCount() > 0) {
                // 更新
                int rows = db.update("budgettb", values,
                        "year = ? AND month = ?",
                        new String[]{String.valueOf(year), String.valueOf(month)});
                success = rows > 0;
            } else {
                // 插入
                long result = db.insert("budgettb", null, values);
                success = result != -1;
            }

            if (cursor != null) {
                cursor.close();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error setting budget: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    /**
     * 获取预算
     * @param year 年份
     * @param month 月份
     * @return 预算金额
     */
    public float getBudget(int year, int month) {
        float budget = 0.0f;
        Cursor cursor = null;
        try {
            String sql = "SELECT budget FROM budgettb WHERE year = ? AND month = ?";
            cursor = db.rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(month)});
            if (cursor.moveToFirst()) {
                budget = cursor.getFloat(cursor.getColumnIndexOrThrow("budget"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting budget: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return budget;
    }
}