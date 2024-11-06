package com.example.moneymanager.model.dao;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseOperationsImpl implements DatabaseOperations{
    private static final String TAG = "DatabaseOperationsImpl";
    private Context context;

    public DatabaseOperationsImpl(Context context) {
        this.context = context;
    }

    @Override
    public File getDatabaseFile() {
        return context.getDatabasePath(DBOpenHelper.Database_Name);
    }

    @Override
    public void closeDatabase() {
        DBManager.closeDB();
    }

    @Override
    public void openDatabase() {
        DBManager.initDB(context);
    }

    @Override
    public boolean exportDatabase(File sourceFile, OutputStream outputStream) {
        try {
            // 关闭数据库以确保所有事务已提交
            closeDatabase();

            FileInputStream fis = new FileInputStream(sourceFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            fis.close();

            // 重新打开数据库
            openDatabase();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error exporting database", e);
            // 确保数据库重新打开
            openDatabase();
            return false;
        }
    }

    @Override
    public boolean importDatabase(InputStream inputStream, File destFile) {
        try {
            // 首先关闭数据库
            closeDatabase();

            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();

            // 重新打开数据库
            openDatabase();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error importing database", e);
            // 确保数据库重新打开
            openDatabase();
            return false;
        }
    }
}
