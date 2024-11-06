package com.example.moneymanager.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.UserController;
import com.example.moneymanager.model.bean.UserBean;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText nameEt,emailEt,pwdEt,checkPwdEt;
    TextView loginTv;
    ImageView hideIv,checkHideIv;
    Button registerBtn;
    UserBean userBean;
    SharedPreferences preferences;
    // 添加控制器
    private UserController userController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // 初始化控制器
        userController = new UserController(this);
        initView();
        preferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        loadData();
    }

    private void loadData() {
        preferences.getString("username","");
        preferences.getString("password","");
        preferences.getBoolean("checkBox",false);
    }

    private void initView() {
        nameEt = findViewById(R.id.register_et_username);
        emailEt = findViewById(R.id.register_et_email);
        pwdEt = findViewById(R.id.register_et_password);
        checkPwdEt = findViewById(R.id.register_et_checkPwd);
        hideIv = findViewById(R.id.register_iv_hide);
        checkHideIv = findViewById(R.id.register_iv_checkHide);
        registerBtn = findViewById(R.id.register_btn);
        loginTv = findViewById(R.id.register_tv_login);

        loginTv.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        hideIv.setOnClickListener(this);
        checkHideIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.register_iv_hide) {
            toggleShow();
        }if (v.getId()==R.id.register_iv_checkHide) {
            toggleShowCheck();
        }if (v.getId()==R.id.register_btn) {
            register();

        }if (v.getId()==R.id.register_tv_login) {
            Intent intent;
            intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }



    private void register() {
        String username = nameEt.getText().toString();
        String email = emailEt.getText().toString();
        String password = pwdEt.getText().toString();
        String checkPassword = checkPwdEt.getText().toString();
        /*if(username.equals("")||password.equals("")||checkPassword.equals("")){
            Toast.makeText(RegisterActivity.this, "用户名密码不能为空！", Toast.LENGTH_SHORT).show();
        } else if (email.equals("")) {
            Toast.makeText(RegisterActivity.this, "邮箱不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            if (password.equals(checkPassword)) {
                userBean = new UserBean();
                userBean.setUsername(username);
                userBean.setEmail(email);
                userBean.setPassword(password);
                DBManager.insertUserToUsertb(userBean);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username",username);
                editor.putString("password",password);
                editor.putBoolean("checkBox",true);
                editor.commit();
                Toast.makeText(RegisterActivity.this, "账号注册成功！", Toast.LENGTH_SHORT).show();
                Intent intent;
                intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(RegisterActivity.this,"两次密码不一致，请重试",Toast.LENGTH_SHORT).show();
            }
        }*/
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(checkPassword) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "输入内容不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(checkPassword)) {
            Toast.makeText(this, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
            return;
        }
        // 使用UserController注册
        boolean success = userController.register(username,password,checkPassword,email);
        if (success) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username",username);
            editor.putString("password",password);
            editor.putBoolean("checkBox",true);
            editor.commit();
            Intent intent;
            intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
        }
    }

    boolean isShow = false;
    private void toggleShow() {
        if (isShow){ //显示 ===> 隐藏
            PasswordTransformationMethod passwordMethod = PasswordTransformationMethod.getInstance();
            pwdEt.setTransformationMethod(passwordMethod);;
            hideIv.setImageResource(R.drawable.eye_close);
            isShow = false;
        }else {
            HideReturnsTransformationMethod hideMethod = HideReturnsTransformationMethod.getInstance();
            pwdEt.setTransformationMethod(hideMethod);
            hideIv.setImageResource(R.drawable.eye_open);
            isShow = true;
        }
    }

    private void toggleShowCheck() {
        if (isShow){ //显示 ===> 隐藏
            PasswordTransformationMethod passwordMethod = PasswordTransformationMethod.getInstance();
            checkPwdEt.setTransformationMethod(passwordMethod);;
            checkHideIv.setImageResource(R.drawable.eye_close);
            isShow = false;
        }else {
            HideReturnsTransformationMethod hideMethod = HideReturnsTransformationMethod.getInstance();
            checkPwdEt.setTransformationMethod(hideMethod);
            checkHideIv.setImageResource(R.drawable.eye_open);
            isShow = true;
        }
    }
}