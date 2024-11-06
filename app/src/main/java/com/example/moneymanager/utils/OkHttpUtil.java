package com.example.moneymanager.utils;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtil {

    private static OkHttpUtil instance;
    private static final String TAG = "OkHttpUtil";
    //微信登录AppSecret
    public static final String WX_APPID = "wx6c6d8b95990d7eb9";
    //微信登录AppID
    public static final String WX_Secret = "98f3faf4dfd463f923994e9a58923f6c";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    private OkHttpUtil(){

    }

    public interface CallBack{
        public void failed(Exception e);

        public void success(String json);
    }

    public static OkHttpUtil getInstance() {
        if (instance == null){
            synchronized (OkHttpUtil.class) {
                if (instance == null) {
                    instance = new OkHttpUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 网络请求
     * @param url
     * @param callBack
     */
    public void doGet(String url,CallBack callBack){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed: " + e.getMessage());
                callBack.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Response failed: " + response.message());
                    callBack.failed(new IOException("Unexpected response " + response));
                    return;
                }
                String jsonData = response.body().string();
                callBack.success(jsonData);
            }
        });
    }
}
