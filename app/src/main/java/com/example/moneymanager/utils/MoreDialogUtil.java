package com.example.moneymanager.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.moneymanager.DetailsActivity;
import com.example.moneymanager.HistoryActivity;
import com.example.moneymanager.R;
import com.example.moneymanager.SettingActivity;

public class MoreDialogUtil extends Dialog implements View.OnClickListener {
    Button settingBtn, historyBtn, detailsBtn;
    ImageView closeIv;

    public MoreDialogUtil(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_more);
        settingBtn = findViewById(R.id.dialog_more_btn_setting);
        historyBtn = findViewById(R.id.dialog_more_btn_history);
        detailsBtn = findViewById(R.id.dialog_more_btn_details);
        closeIv = findViewById(R.id.dialog_more_iv);
        settingBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        detailsBtn.setOnClickListener(this);
        closeIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v.getId()==R.id.dialog_more_btn_setting) {
            intent.setClass(getContext(), SettingActivity.class);
            getContext().startActivity(intent);
        }
        if (v.getId()==R.id.dialog_more_btn_history) {
            intent.setClass(getContext(), HistoryActivity.class);
            getContext().startActivity(intent);
        }
        if (v.getId()==R.id.dialog_more_btn_details) {
            intent.setClass(getContext(), DetailsActivity.class);
            getContext().startActivity(intent);
        }
        if (v.getId()==R.id.dialog_more_iv) {
            cancel();
        }
        cancel();
    }

    public void setDialogSize(){
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        Display defaultDisplay = window.getWindowManager().getDefaultDisplay();
        wlp.width = (int) (defaultDisplay.getWidth());
        wlp.gravity = Gravity.BOTTOM;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(wlp);
    }
}
