package com.example.moneymanager.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.DetailsController;
import com.example.moneymanager.controller.SettingController;
import com.example.moneymanager.utils.ExportDataUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView backBtn;
    TextView clearTv,logoutTv,fileTv, backupTv, restoreTv;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int CREATE_FILE_REQUEST_CODE = 101;
    private static final int BACKUP_FILE_REQUEST_CODE = 102;
    private static final int RESTORE_FILE_REQUEST_CODE = 103;
    private String exportFormat = "csv";
    // 添加控制器
    private DetailsController detailsController;
    private SettingController settingController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // 初始化控制器
        detailsController = new DetailsController(this);
        settingController = new SettingController(this);

        backBtn = findViewById(R.id.setting_iv_back);
        clearTv = findViewById(R.id.setting_tv_clear);
        logoutTv = findViewById(R.id.setting_tv_logout);
        fileTv = findViewById(R.id.setting_tv_file);
        backupTv = findViewById(R.id.setting_tv_backup);
        restoreTv = findViewById(R.id.setting_tv_restore);

        backBtn.setOnClickListener(this);
        clearTv.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
        fileTv.setOnClickListener(this);
        backupTv.setOnClickListener(this);
        restoreTv.setOnClickListener(this);

    }

    /*// 修改修改密码的方法
    private void changePassword() {
        // 获取输入值
        String oldPassword = oldPasswordEt.getText().toString();
        String newPassword = newPasswordEt.getText().toString();
        String confirmPassword = confirmPasswordEt.getText().toString();

        // 验证输入
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取当前用户名
        String username = getSharedPreferences("userinfo", MODE_PRIVATE).getString("username", "");

        // 使用UserController修改密码
        boolean success = userController.changePassword(username, oldPassword, newPassword);
        if (success) {
            Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
            // 更新储存的密码
            SharedPreferences.Editor editor = getSharedPreferences("userinfo", MODE_PRIVATE).edit();
            editor.putString("password", newPassword);
            editor.apply();
        } else {
            Toast.makeText(this, "原密码错误", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setting_iv_back) {
            finish();
        }if (v.getId() == R.id.setting_tv_clear) {
            showDeleteDialog();
        }if (v.getId() == R.id.setting_tv_logout) {
            showLoginOutDialog();
        }if (v.getId() == R.id.setting_tv_file) {
            showFileDialog();
        } else if (v.getId() == R.id.setting_tv_backup) {
            showBackupDialog();
        } else if (v.getId() == R.id.setting_tv_restore) {
            showRestoreDialog();
        }

    }

    private void showBackupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.backup_confirm_title)
                .setMessage(R.string.backup_confirm_message)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    chooseBackupLocation();
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    private void chooseBackupLocation() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");

        // 使用时间戳创建默认的文件名
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String filename = "moneymanager_backup_" + timestamp + ".db";
        intent.putExtra(Intent.EXTRA_TITLE, filename);

        startActivityForResult(intent, BACKUP_FILE_REQUEST_CODE);
    }

    private void showRestoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.restore_confirm_title)
                .setMessage(R.string.restore_confirm_message)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    chooseRestoreFile();
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    private void chooseRestoreFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        startActivityForResult(intent, RESTORE_FILE_REQUEST_CODE);
    }

    private void showFileDialog() {
        String[] options = {"导出为CSV", "导出为PDF"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择导出格式")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exportFormat = (which == 0) ? "csv" : "pdf";
                        checkStoragePermissionAndExport();
                    }
                })
                .setNegativeButton("取消", null);
        builder.create().show();
    }

    private void checkStoragePermissionAndExport() {
        // 对于 Android 10 (API 29) 及以上版本，使用 SAF (Storage Access Framework)
        // 对于较低版本，需要请求 WRITE_EXTERNAL_STORAGE 权限
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 显示权限请求说明对话框
                new AlertDialog.Builder(this)
                        .setTitle("需要存储权限")
                        .setMessage("导出数据需要访问您的存储空间，请授予权限")
                        .setPositiveButton("确定", (dialog, which) -> {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    STORAGE_PERMISSION_CODE);
                        })
                        .setNegativeButton("取消", null)
                        .create().show();
            } else {
                chooseExportLocation();
            }
        } else {
            // Android 10+ 使用 SAF，不需要特殊权限
            chooseExportLocation();
        }
    }

    private void chooseExportLocation() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Set mime type based on export format
        if (exportFormat.equals("csv")) {
            intent.setType("text/csv");
        } else {
            intent.setType("application/pdf");
        }

        // Default filename with timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String filename = "moneymanager_export_" + timestamp + "." + exportFormat;
        intent.putExtra(Intent.EXTRA_TITLE, filename);

        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseExportLocation();
            } else {
                Toast.makeText(this, "需要存储权限来导出数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            if (requestCode == CREATE_FILE_REQUEST_CODE) {
                try {
                    boolean success = false;
                    try {
                        if (exportFormat.equals("csv")) {
                            success = ExportDataUtil.exportToCSV(this, uri);
                        } else {
                            success = ExportDataUtil.exportToPDF(this, uri);
                        }
                    } catch (Exception e){
                        DocumentFile docFile = DocumentFile.fromSingleUri(this, uri);
                        if (docFile != null) {
                            docFile.delete();
                        }

                        Toast.makeText(this, "导出错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("ExportError", "Export failed", e);
                        return;
                    }
                    if (success) {
                        Toast.makeText(this, "导出成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // Delete the file if export indicates failure
                        DocumentFile docFile = DocumentFile.fromSingleUri(this, uri);
                        if (docFile != null) {
                            docFile.delete();
                        }
                        Toast.makeText(this, "导出失败，请检查权限和存储空间", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "导出错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("ExportError", "Export failed", e);
                }
            } else if (requestCode == BACKUP_FILE_REQUEST_CODE) {
                handleBackupResult(uri);
            } else if (requestCode == RESTORE_FILE_REQUEST_CODE) {
                handleRestoreResult(uri);
            }
        }
    }

    private void handleBackupResult(Uri uri) {
        try {
            boolean success = settingController.backupDatabase(uri);
            if (success) {
                Toast.makeText(this, R.string.backup_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.backup_failed, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "备份错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("BackupError", "Backup failed", e);
        }
    }

    private void handleRestoreResult(Uri uri) {
        try {
            // 显示最终确认对话框
            new AlertDialog.Builder(this)
                    .setTitle(R.string.restore_final_confirm_title)
                    .setMessage(R.string.restore_final_confirm_message)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        try {
                            boolean success = settingController.restoreDatabase(uri);
                            if (success) {
                                Toast.makeText(this, R.string.restore_success, Toast.LENGTH_SHORT).show();
                                // 重启应用
                                restartApp();
                            } else {
                                Toast.makeText(this, R.string.restore_failed, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "恢复错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("RestoreError", "Restore failed", e);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create().show();
        } catch (Exception e) {
            Toast.makeText(this, "选择文件错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("RestoreError", "File selection failed", e);
        }
    }

    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoginOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要退出登录吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 清除登录信息
                        SharedPreferences preferences = getSharedPreferences("userinfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("checkBox", false);
                        editor.commit();
                        Toast.makeText(SettingActivity.this,"退出成功！",Toast.LENGTH_SHORT).show();
                        Intent intent;
                        intent = new Intent(SettingActivity.this, LoginActivity.class);
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
                        // 使用RecordController清空记录
                        boolean success = detailsController.clearAllRecords();
                        if (success) {
                            Toast.makeText(SettingActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.create().show();
    }
}