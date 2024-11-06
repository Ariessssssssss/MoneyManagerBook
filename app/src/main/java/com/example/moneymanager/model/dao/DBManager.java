package com.example.moneymanager.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import android.util.Log;

import com.example.moneymanager.model.bean.BarChartItemBean;
import com.example.moneymanager.model.bean.ChartItemBean;
import com.example.moneymanager.model.bean.DetailsBean;
import com.example.moneymanager.model.bean.TypeBean;
import com.example.moneymanager.model.bean.UserBean;
import com.example.moneymanager.model.dao.DBOpenHelper;
import com.example.moneymanager.utils.FloatUtil;

import java.util.ArrayList;
import java.util.List;

/*
* 负责管理数据库
* 增删改查
* */
public class DBManager {
    private static final String TAG = "DBManager";
    private static SQLiteDatabase db;
    private static boolean isInitialized = false;
    private static DBOpenHelper helper; // 新增，保存helper引用
    public static void initDB(Context context){
        if (isInitialized && db != null && db.isOpen()) {
            return;
        }

        try {
            DBOpenHelper helper = new DBOpenHelper(context);
            db = helper.getEncryptedDatabase();
            isInitialized = true;
        } catch (Exception e) {
            Log.e("DBManager", "Failed to initialize database", e);
            isInitialized = false;
        }
    }
    public static SQLiteDatabase getDatabase() {
        if (db == null || !db.isOpen()) {
            throw new IllegalStateException("Database not initialized. Call initDB first.");
        }
        return db;
    }

    public static void closeDB() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (helper != null) {
            helper.close();
        }
        isInitialized = false;
    }
}
