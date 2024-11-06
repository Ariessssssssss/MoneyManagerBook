package com.example.moneymanager;

import android.app.Application;

import com.example.moneymanager.database.DBManager;

public class UniteApp extends Application {
    @Override
    public void onCreate() {

        super.onCreate();
        DBManager.initDB(getApplicationContext());
    }
}
