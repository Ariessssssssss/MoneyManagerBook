package com.example.moneymanager.model.dao.helper;

import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * 数据库事务辅助类
 * 提供安全的事务处理
 */
public class DBTransaction {
    private static final String TAG = "DBTransaction";

    /**
     * 在事务中执行数据库操作
     * @param db 数据库
     * @param operation 数据库操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    public static <T> T executeInTransaction(SQLiteDatabase db, TransactionOperation<T> operation) {
        T result = null;
        try {
            db.beginTransaction();
            result = operation.execute();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Transaction error: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    /**
     * 事务操作接口
     * @param <T> 返回类型
     */
    public interface TransactionOperation<T> {
        T execute();
    }
}