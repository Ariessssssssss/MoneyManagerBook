package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanager.database.DBManager;
import com.example.moneymanager.database.UserBean;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText nameEt,pwdEt,checkPwdEt;
    TextView cancelTv;
    ImageView hideIv,checkHideIv;
    Button registerBtn;
    UserBean userBean;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        pwdEt = findViewById(R.id.register_et_password);
        checkPwdEt = findViewById(R.id.register_et_checkPwd);
        hideIv = findViewById(R.id.register_iv_hide);
        checkHideIv = findViewById(R.id.register_iv_checkHide);
        registerBtn = findViewById(R.id.register_btn);
        cancelTv = findViewById(R.id.register_tv_cancel);
        cancelTv.setOnClickListener(this);
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

        }if (v.getId()==R.id.register_tv_cancel) {
            Intent intent;
            intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }



    private void register() {
        String username = nameEt.getText().toString();
        String password = pwdEt.getText().toString();
        String checkPassword = checkPwdEt.getText().toString();
        if(username.equals("")||password.equals("")||checkPassword.equals("")){
            Toast.makeText(RegisterActivity.this, "用户名密码不能为空！", Toast.LENGTH_SHORT).show();
        }else {
            if (password.equals(checkPassword)) {
                userBean = new UserBean();
                userBean.setUsername(username);
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
        }
    }

    boolean isShow = false;
    private void toggleShow() {
        if (isShow){ //显示 ===> 隐藏
            PasswordTransformationMethod passwordMethod = PasswordTransformationMethod.getInstance();
            pwdEt.setTransformationMethod(passwordMethod);;
            hideIv.setImageResource(R.mipmap.eye_close);
            isShow = false;
        }else {
            HideReturnsTransformationMethod hideMethod = HideReturnsTransformationMethod.getInstance();
            pwdEt.setTransformationMethod(hideMethod);
            hideIv.setImageResource(R.mipmap.eye_open);
            isShow = true;
        }
    }

    private void toggleShowCheck() {
        if (isShow){ //显示 ===> 隐藏
            PasswordTransformationMethod passwordMethod = PasswordTransformationMethod.getInstance();
            checkPwdEt.setTransformationMethod(passwordMethod);;
            checkHideIv.setImageResource(R.mipmap.eye_close);
            isShow = false;
        }else {
            HideReturnsTransformationMethod hideMethod = HideReturnsTransformationMethod.getInstance();
            checkPwdEt.setTransformationMethod(hideMethod);
            checkHideIv.setImageResource(R.mipmap.eye_open);
            isShow = true;
        }
    }
}