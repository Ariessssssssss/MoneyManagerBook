package com.example.moneymanager.view.activity;

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

import com.example.moneymanager.R;
import com.example.moneymanager.controller.UserController;
import com.example.moneymanager.controller.WeChatController;
import com.example.moneymanager.model.bean.UserBean;
import com.example.moneymanager.utils.OkHttpUtil;
import com.example.moneymanager.view.dialog.WechatDialogUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText usernameEt,passwordEt;
    TextView registerTv;
    ImageView hideIv;
    Button loginBtn,wechatBtn;
    CheckBox checkBox;
    SharedPreferences preferences;
    // 声明一个static的IWXAPI
    private static IWXAPI mWxApi;
    // 添加控制器
    private UserController userController;
    private WeChatController weChatController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 初始化控制器
        userController = new UserController(this);
        weChatController = new WeChatController(this);
        initView();
        preferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        loadData();
        //初始化微信SDK
        mWxApi = WXAPIFactory.createWXAPI(this, OkHttpUtil.WX_APPID, true);
        mWxApi.registerApp(OkHttpUtil.WX_APPID);

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
        wechatBtn = findViewById(R.id.login_btn_wechat);
        checkBox = findViewById(R.id.login_checkBox);
        registerTv.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        wechatBtn.setOnClickListener(this);
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
        }if (v.getId() == R.id.login_btn_wechat) {
            if (mWxApi!= null && mWxApi.isWXAppInstalled()) {
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_login";
                mWxApi.sendReq(req);
            } else {
                Toast.makeText(LoginActivity.this, "您还没有安装微信", Toast.LENGTH_SHORT).show();
                showWechatDialog();
            }
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
            // 使用UserController进行登录
            boolean success = userController.login(username, password);
            if (!success) {
/*
                Toast.makeText(LoginActivity.this,"账号或密码错误，请重新输入",Toast.LENGTH_SHORT).show();
*/
                usernameEt.setText("");
                passwordEt.setText("");
            } else {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username",username);
                editor.putString("password",password);
                editor.putBoolean("checkBox",checkBox.isChecked());
                editor.commit();
/*
                Toast.makeText(LoginActivity.this, "账号密码正确，欢迎登录", Toast.LENGTH_SHORT).show();
*/
                Intent intent;
                intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

    }

    private void showWechatDialog() {
        WechatDialogUtil wechatDialogUtil = new WechatDialogUtil(this);
        wechatDialogUtil.show();
        wechatDialogUtil.setDialogSize();
    }
    boolean isShow = false;
    private void toggleShow() {
        isShow = !isShow;
        passwordEt.setTransformationMethod(isShow
                ? HideReturnsTransformationMethod.getInstance()
                : PasswordTransformationMethod.getInstance());
        hideIv.setImageResource(isShow ? R.drawable.eye_open : R.drawable.eye_close);
    }

}