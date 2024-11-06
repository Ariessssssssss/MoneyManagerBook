package com.example.moneymanager;

import android.app.Application;
import android.util.Log;

import com.example.moneymanager.model.dao.DBManager;
import com.example.moneymanager.utils.SecureStorageUtil;

public class UniteApp extends Application {
    @Override
    public void onCreate() {

        super.onCreate();
        // Initialize SecureStorageUtil with application context
        SecureStorageUtil.initialize(getApplicationContext());

        try {
            // Initialize database
            DBManager.initDB(getApplicationContext());
        } catch (Exception e) {
            Log.e("UniteApp", "Failed to initialize database", e);
            // Handle database initialization error
            // Consider showing a user-friendly message or attempting recovery
        }
    }
    public void onTerminate() {
        // Close database when app terminates
        DBManager.closeDB();
        super.onTerminate();
    }
}
