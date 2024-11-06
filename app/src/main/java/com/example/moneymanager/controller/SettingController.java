package com.example.moneymanager.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.moneymanager.model.service.DataService;

/**
 * 设置控制器
 * 负责处理应用设置相关的业务逻辑
 */
public class SettingController {
    private Context context;
    private SharedPreferences preferences;
    private DataService dataService;

    public SettingController(Context context) {
        this.context = context.getApplicationContext();
        this.dataService = new DataService(context);
        this.preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }
    /**
     * 设置是否显示金额
     * @param show 是否显示金额
     * @return 是否设置成功
     */
    public boolean setShowAmount(boolean show) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showAmount", show);
        return editor.commit();
    }
    public boolean backupDatabase(Uri destinationUri) {
        return dataService.backupDatabase(destinationUri);
    }

    public boolean restoreDatabase(Uri sourceUri) {
        return dataService.restoreDatabase(sourceUri);
    }

}