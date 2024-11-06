package com.example.moneymanager.model.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.moneymanager.model.dao.DatabaseOperations;
import com.example.moneymanager.model.dao.DatabaseOperationsImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataService {
    private static final String TAG = "DataService";
    private DatabaseOperations dbOperations;
    private Context context;

    public DataService(Context context) {
        this.context = context;
        this.dbOperations = new DatabaseOperationsImpl(context);
    }

    public boolean backupDatabase(Uri destinationUri) {
        boolean success = false;

        try {
            File dbFile = dbOperations.getDatabaseFile();
            if (!dbFile.exists()) {
                Log.e(TAG, "Database file not found");
                return false;
            }

            // 打开输出流
            OutputStream output = context.getContentResolver().openOutputStream(destinationUri);

            if (output != null) {
                // 导出数据库
                success = dbOperations.exportDatabase(dbFile, output);
                output.close();
            }

        } catch (IOException e) {
            Log.e(TAG, "Error backing up database", e);
        }

        return success;
    }

    public boolean restoreDatabase(Uri sourceUri) {
        boolean success = false;

        try {
            File dbFile = dbOperations.getDatabaseFile();

            // 打开输入流
            InputStream input = context.getContentResolver().openInputStream(sourceUri);

            if (input != null) {
                // 导入数据库
                success = dbOperations.importDatabase(input, dbFile);
                input.close();
            }

        } catch (IOException e) {
            Log.e(TAG, "Error restoring database", e);
        }

        return success;
    }
}
