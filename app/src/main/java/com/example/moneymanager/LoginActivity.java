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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanager.database.DBManager;
import com.example.moneymanager.database.UserBean;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText usernameEt,passwordEt;
    TextView registerTv;
    ImageView hideIv;
    Button loginBtn;
    CheckBox checkBox;
    private List<UserBean> userBeans;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        preferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        loadData();

    }

    private void loadData() {
        String myname=preferences.getString("username","");
        String mypwd=preferences.getString("password","");
        Boolean check=preferences.getBoolean("checkBox",false);
        if (check == true){
            usernameEt.setText(myname);
            passwordEt.setText(mypwd);
            checkBox.setChecked(true);
        }else{
            usernameEt.setText("");
            passwordEt.setText("");
            checkBox.setChecked(false);
        }
    }

    private void initView() {
        usernameEt = findViewById(R.id.login_et_username);
        passwordEt = findViewById(R.id.login_et_password);
        registerTv = findViewById(R.id.login_tv_register);
        hideIv = findViewById(R.id.login_iv_hide);
        loginBtn = findViewById(R.id.login_btn);
        checkBox = findViewById(R.id.login_checkBox);
        registerTv.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        hideIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_tv_register) {
            Intent intent;
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }if (v.getId() == R.id.login_btn) {
            login();
        }if (v.getId() == R.id.login_iv_hide) {
            toggleShow();
        }

    }

    private void login() {
        String username = usernameEt.getText().toString();
        String password = passwordEt.getText().toString();
        if(username.equals("")||password.equals("")){
            Toast.makeText(LoginActivity.this, "用户名密码不能为空！", Toast.LENGTH_SHORT).show();
        }else {
            int size = DBManager.selectUserFromUsertb(username, password);
            if (size == 0) {
                Toast.makeText(LoginActivity.this,"账号或密码错误，请重新输入",Toast.LENGTH_SHORT).show();
                usernameEt.setText("");
                passwordEt.setText("");
            }else {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username",username);
                editor.putString("password",password);
                editor.putBoolean("checkBox",checkBox.isChecked());
                editor.commit();
                Toast.makeText(LoginActivity.this, "账号密码正确，欢迎登录", Toast.LENGTH_SHORT).show();
                Intent intent;
                intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        }

    }

    boolean isShow = false;
    private void toggleShow() {
        if (isShow){ //显示 ===> 隐藏
            PasswordTransformationMethod passwordMethod = PasswordTransformationMethod.getInstance();
            passwordEt.setTransformationMethod(passwordMethod);;
            hideIv.setImageResource(R.mipmap.eye_close);
            isShow = false;
        }else {
            HideReturnsTransformationMethod hideMethod = HideReturnsTransformationMethod.getInstance();
            passwordEt.setTransformationMethod(hideMethod);
            hideIv.setImageResource(R.mipmap.eye_open);
            isShow = true;
        }
    }

}