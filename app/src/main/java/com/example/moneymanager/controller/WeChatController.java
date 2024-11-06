package com.example.moneymanager.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.moneymanager.model.bean.WeChatTicket;
import com.example.moneymanager.model.bean.WeChatToken;
import com.example.moneymanager.model.bean.WeChatUserBean;
import com.example.moneymanager.utils.EncrpyUtil;
import com.example.moneymanager.utils.OkHttpUtil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * 微信控制器
 * 负责处理微信登录相关的业务逻辑
 */
public class WeChatController {
    private static final String TAG = "WeChatController";
    private static final String TIME_FORMAT = "yyyyMMddHHmmss";
    private Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface WeChatAuthCallback {
        void onSuccess(WeChatUserBean userInfo);
        void onFailure(String errorMessage);
    }

    public WeChatController(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 请求微信访问令牌
     * @param callback 回调接口
     */
    public void requestAccessToken(WeChatAuthCallback callback) {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
                + "&appid=" + OkHttpUtil.WX_APPID
                + "&secret=" + OkHttpUtil.WX_Secret;

        Log.d(TAG, "Requesting WeChat Access Token...");
        OkHttpUtil.getInstance().doGet(url, new OkHttpUtil.CallBack() {
            @Override
            public void failed(Exception e) {
                Log.e(TAG, "Failed to get access token: ", e);
                if (callback != null) {
                    callback.onFailure("网络请求失败，请检查网络");
                }
            }

            @Override
            public void success(String json) {
                Log.d(TAG, "Received Access Token Response: " + json);
                Gson gson = new Gson();
                WeChatToken result = gson.fromJson(json, WeChatToken.class);
                if (result != null && result.getAccess_token() != null) {
                    String accessToken = result.getAccess_token();
                    getTicket(accessToken, callback);
                } else {
                    if (callback != null) {
                        callback.onFailure("获取Token失败");
                    }
                }
            }
        });
    }

    /**
     * 获取微信票据
     * @param accessToken 访问令牌
     * @param callback 回调接口
     */
    private void getTicket(String accessToken, WeChatAuthCallback callback) {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?"
                + "access_token=" + accessToken
                + "&type=2";

        Log.d(TAG, "Requesting WeChat Ticket...");
        OkHttpUtil.getInstance().doGet(url, new OkHttpUtil.CallBack() {
            @Override
            public void failed(Exception e) {
                Log.e(TAG, "Failed to get WeChat ticket: ", e);
                if (callback != null) {
                    callback.onFailure("获取Ticket失败");
                }
            }

            @Override
            public void success(String json) {
                Gson gson = new Gson();
                WeChatTicket result = gson.fromJson(json, WeChatTicket.class);
                if (result != null && result.getTicket() != null) {
                    String ticket = result.getTicket();
                    generateSignature(ticket, callback);
                } else {
                    if (callback != null) {
                        callback.onFailure("获取Ticket失败");
                    }
                }
            }
        });
    }

    /**
     * 生成签名
     * @param ticket 票据
     * @param callback 回调接口
     */
    private void generateSignature(String ticket, WeChatAuthCallback callback) {
        String noncestr = generateNonceStr();
        String timeStamp = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(new Date());

        String stringToSign = String.format("appid=%s&noncestr=%s&sdk_ticket=%s&timestamp=%s",
                OkHttpUtil.WX_APPID, noncestr, ticket, timeStamp);
        String shaSignature = EncrpyUtil.getSHA(stringToSign);

        Log.d(TAG, "Generated Signature: " + shaSignature);
        // 这里应该继续处理OAuth认证，但由于使用了第三方SDK，这里仅作示例
        // callback.onSuccess(userInfo) 或 callback.onFailure(errorMessage)
    }

    /**
     * 生成随机字符串
     * @return 随机字符串
     */
    private String generateNonceStr() {
        Random random = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    /**
     * 获取用户信息
     * @param token 微信令牌
     * @param callback 回调接口
     */
    public void getUserInfo(WeChatToken token, WeChatAuthCallback callback) {
        String url = "https://api.weixin.qq.com/sns/userinfo?"
                + "access_token=" + token.getAccess_token()
                + "&openid=" + token.getOpenid();

        OkHttpUtil.getInstance().doGet(url, new OkHttpUtil.CallBack() {
            @Override
            public void failed(Exception e) {
                Log.e(TAG, "Failed to get user info: ", e);
                if (callback != null) {
                    callback.onFailure("获取用户信息失败");
                }
            }

            @Override
            public void success(String json) {
                Gson gson = new Gson();
                WeChatUserBean weChatUserBean = gson.fromJson(json, WeChatUserBean.class);

                if (weChatUserBean != null && weChatUserBean.getErrmsg() == null) {
                    if (callback != null) {
                        callback.onSuccess(weChatUserBean);
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure("获取用户信息失败");
                    }
                    Log.e(TAG, "Error in user info response: " + json);
                }
            }
        });
    }

    /**
     * 显示提示信息
     * @param message 提示信息
     */
    private void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }
}