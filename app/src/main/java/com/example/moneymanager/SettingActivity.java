package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanager.database.DBManager;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView backBtn;
    TextView clearTv,logoutTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        backBtn = findViewById(R.id.setting_iv_back);
        clearTv = findViewById(R.id.setting_tv_clear);
        logoutTv = findViewById(R.id.setting_tv_logout);
        backBtn.setOnClickListener(this);
        clearTv.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setting_iv_back) {
            finish();
        }if (v.getId() == R.id.setting_tv_clear) {
            showDeleteDialog();
        }if (v.getId() == R.id.setting_tv_logout) {
            showLoginOutDialog();
        }

    }

    private void showLoginOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要退出登录吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SettingActivity.this,"退出成功！",Toast.LENGTH_SHORT).show();
                        Intent intent;
                        intent = new Intent(SettingActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要删除所有记录吗？\n注意：删除后无法恢复！")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBManager.deleteAllDetails();
                        Toast.makeText(SettingActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();
                    }
                });
        builder.create().show();
    }
}