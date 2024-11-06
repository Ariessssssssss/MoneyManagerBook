package com.example.moneymanager.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moneymanager.utils.EncrpyUtil;
import com.example.moneymanager.utils.OkHttpUtil;
import com.example.moneymanager.view.activity.MainActivity;
import com.example.moneymanager.R;
import com.example.moneymanager.model.bean.WeChatTicket;
import com.example.moneymanager.model.bean.WeChatToken;
import com.example.moneymanager.model.bean.WeChatUserBean;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.diffdev.DiffDevOAuthFactory;
import com.tencent.mm.opensdk.diffdev.IDiffDevOAuth;
import com.tencent.mm.opensdk.diffdev.OAuthErrCode;
import com.tencent.mm.opensdk.diffdev.OAuthListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class WechatDialogUtil extends Dialog implements View.OnClickListener, OAuthListener {
    ImageView closeIv,qrIv;
    private IDiffDevOAuth oAuth;
    private static final String timeFormat = "yyyyMMddHHmmss";
    private static final String TAG = "WechatDialogUtil";
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    public WechatDialogUtil(@NonNull Context context) {
        super(context);
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wechat);
        oAuth = DiffDevOAuthFactory.getDiffDevOAuth();

        closeIv = findViewById(R.id.dialog_wechat_close);
        qrIv = findViewById(R.id.dialog_wechat_QR);

        closeIv.setOnClickListener(this);
        qrIv.setOnClickListener(this);

        requestAccessToken();
    }

    private void requestAccessToken() {
        String url  = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
                +"&appid="+ OkHttpUtil.WX_APPID
                +"&secret="+OkHttpUtil.WX_Secret;
        Log.d(TAG, "Requesting WeChat Access Token...");
        OkHttpUtil.getInstance().doGet(url, new OkHttpUtil.CallBack() {
            @Override
            public void failed(Exception e) {
                showToast("网络请求失败，请检查网络");
                Log.e(TAG, "Failed to get access token: ", e);
            }

            @Override
            public void success(String json) {
                Log.d(TAG, "Received Access Token Response: " + json);
                Gson gson = new Gson();
                WeChatToken result = gson.fromJson(json, WeChatToken.class);
                String accessToken = result.getAccess_token();
                getTicket(accessToken);
            }
        });
    }

    private void getTicket(String accessToken) {
        String url  = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?"
                +"access_token="+accessToken
                +"&type=2";
        //https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=2
        Log.d(TAG, "Requesting WeChat Ticket...");
        OkHttpUtil.getInstance().doGet(url, new OkHttpUtil.CallBack() {
            @Override
            public void failed(Exception e) {
                showToast("网络请求失败，请检查网络");
                Log.e(TAG, "Failed to get WeChat ticket: ", e);            }

            @Override
            public void success(String json) {
                Gson gson = new Gson();
                WeChatTicket result = gson.fromJson(json, WeChatTicket.class);
                String ticket = result.getTicket();
                generateSignature(ticket);

                StringBuilder str = new StringBuilder();
                Random random = new Random();
                for (int i = 0; i < 8; i++) {
                    str.append(random.nextInt(10));
                }
                String noncestr = str.toString();
                String timeStamp = new SimpleDateFormat(timeFormat).format(new Date());
                //appid、noncestr、sdk_ticket、timestamp
                String string1 = String.format("appid=%s&noncestr=%s&sdk_ticket=%s&timestamp=%s",OkHttpUtil.WX_APPID,noncestr,ticket,timeStamp);
                String sha = EncrpyUtil.getSHA(string1);
                startOAuth(noncestr,timeStamp,sha);
            }
        });
    }

    private void generateSignature(String ticket) {
        String noncestr = generateNonceStr();
        String timeStamp = new SimpleDateFormat(timeFormat, Locale.getDefault()).format(new Date());

        String stringToSign = String.format("appid=%s&noncestr=%s&sdk_ticket=%s&timestamp=%s",
                OkHttpUtil.WX_APPID, noncestr, ticket, timeStamp);
        String shaSignature = EncrpyUtil.getSHA(stringToSign);

        Log.d(TAG, "Generated Signature: " + shaSignature);
        startOAuth(noncestr, timeStamp, shaSignature);
    }

    private void startOAuth(String noncestr, String timeStamp, String sha) {
        Log.d(TAG, "Starting WeChat OAuth...");
        boolean result = oAuth.auth(OkHttpUtil.WX_APPID, "snsapi_userinfo", noncestr, timeStamp, sha, this);
        if (!result) {
            showToast("OAuth 认证失败，请重试");
        }
    }
    private String generateNonceStr() {
        Random random = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
    @Override
    public void onClick(View v) {
        //Intent intent = new Intent();
        if (v.getId() == R.id.dialog_wechat_close){
            cancel();
        }
        cancel();
    }
    public void setDialogSize(){
        Window window = getWindow();
        if (window == null) return;

        WindowManager.LayoutParams wlp = window.getAttributes();
        Display defaultDisplay = window.getWindowManager().getDefaultDisplay();
        wlp.width = (int) (defaultDisplay.getWidth());
        wlp.gravity = Gravity.CENTER;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(wlp);
    }
    @Override
    public void onAuthGotQrcode(String s, byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mainHandler.post(() -> qrIv.setImageBitmap(bitmap));
            Log.d(TAG, "QR Code received and displayed.");
        } else {
            showToast("获取二维码失败");
            Log.e(TAG, "Failed to decode QR code.");
        }
    }
    @Override
    public void onQrcodeScanned() {
        Log.d(TAG, "WeChat QR Code scanned.");
    }
    @Override
    public void onAuthFinish(OAuthErrCode oAuthErrCode, String authCode) {
        if (oAuthErrCode == OAuthErrCode.WechatAuth_Err_OK) {
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
                    + "appid=" + OkHttpUtil.WX_APPID
                    + "&secret=" + OkHttpUtil.WX_Secret
                    + "&code=" + authCode
                    + "&grant_type=authorization_code";

            Log.d(TAG, "Fetching access token with auth code...");
            OkHttpUtil.getInstance().doGet(url, new OkHttpUtil.CallBack() {
                @Override
                public void failed(Exception e) {
                    showToast("网络请求失败，请检查网络");
                    Log.e(TAG, "Failed to exchange auth code for access token", e);
                }

                @Override
                public void success(String json) {
                    Gson gson = new Gson();
                    WeChatToken token = gson.fromJson(json, WeChatToken.class);

                    if (token != null && token.getOpenid() != null) {
                        // Now fetch user info like in WXEntryActivity
                        getUserInfo(token);
                    } else {
                        showToast("登录失败，请重试");
                        Log.e(TAG, "Failed to parse access token response: " + json);
                    }
                    /*Log.d(TAG, "Successfully authenticated, starting MainActivity.");
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    getContext().startActivity(intent);
                    dismiss();*/
                }
            });
        } else {
            showToast("授权失败，请重试");
            Log.e(TAG, "WeChat OAuth failed: " + oAuthErrCode.name());
        }
    }
    private void getUserInfo(WeChatToken token) {
        String url = "https://api.weixin.qq.com/sns/userinfo?"
                + "access_token=" + token.getAccess_token()
                + "&openid=" + token.getOpenid();

        OkHttpUtil.getInstance().doGet(url, new OkHttpUtil.CallBack() {
            @Override
            public void failed(Exception e) {
                showToast("获取用户信息失败");
                Log.e(TAG, "Failed to get user info: ", e);
            }

            @Override
            public void success(String json) {
                Gson gson = new Gson();
                WeChatUserBean weChatUserBean = gson.fromJson(json, WeChatUserBean.class);

                if (weChatUserBean != null && weChatUserBean.getErrmsg() == null) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    // You might want to pass user info to MainActivity
                    getContext().startActivity(intent);
                    dismiss();
                } else {
                    showToast("获取用户信息失败");
                    Log.e(TAG, "Error in user info response: " + json);
                }
            }
        });
    }
    private void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }
}
