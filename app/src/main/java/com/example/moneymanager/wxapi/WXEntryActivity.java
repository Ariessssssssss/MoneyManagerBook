package com.example.moneymanager.wxapi;

import static android.text.TextUtils.isEmpty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.moneymanager.view.activity.LoginActivity;
import com.example.moneymanager.view.activity.MainActivity;
import com.example.moneymanager.model.bean.WeChatToken;
import com.example.moneymanager.model.bean.WeChatUserBean;
import com.example.moneymanager.utils.OkHttpUtil;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private IWXAPI mWxApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWxApi = WXAPIFactory.createWXAPI(this,OkHttpUtil.WX_APPID,true);
        mWxApi.handleIntent(this.getIntent(),this);
    }
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWxApi.handleIntent(intent,this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Intent intent = new Intent(this, LoginActivity.class);
        if (baseResp.getType() == 1){
            switch (baseResp.errCode){
                case BaseResp.ErrCode.ERR_OK:
                    //拉起微信成功
                    SendAuth.Resp authRsp = (SendAuth.Resp)baseResp;
                    Log.i("code: ",(authRsp.code).toString());
                    requestAccessToken(authRsp.code);
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    Log.e("授权", "出错！");
                    startActivity(intent);
                    //("授权出错");
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    //"用户取消登陆"
                    startActivity(intent);
                    break;
                default:
                    break;
            }
            finish();
        }

    }

    private void requestAccessToken(String code) {
        String url  = "https://api.weixin.qq.com/sns/oauth2/access_token?"
                +"appid="+OkHttpUtil.WX_APPID
                +"&secret="+OkHttpUtil.WX_Secret
                +"&code="+code
                +"&grant_type=authorization_code";
        OkHttpUtil.getInstance().doGet(url,new OkHttpUtil.CallBack(){

            @Override
            public void failed(Exception e) {
                Log.e("请求token", "获取token失败！");
            }

            @Override
            public void success(String json) {
                Gson gson = new Gson();
                WeChatToken token = gson.fromJson(json, WeChatToken.class);
                if (isEmpty(token.getErrmsg())){
                    refreshToken(token);
                }else {
                    Log.e("token返回",token.getErrcode()+token.getErrmsg());
                }
            }
        });
    }

    private void refreshToken(WeChatToken token) {
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?"
                +"appid="+OkHttpUtil.WX_APPID
                +"&grant_type=refresh_token"
                +"&refresh_token="+token.getRefresh_token();
        OkHttpUtil.getInstance().doGet(url,new OkHttpUtil.CallBack(){

            @Override
            public void failed(Exception e) {
                getUserInfo(token);
                Log.e("刷新token", "刷新token失败！");
            }

            @Override
            public void success(String json) {
                Gson gson = new Gson();
                WeChatToken new_token = gson.fromJson(json, WeChatToken.class);
                if (isEmpty(new_token.getErrmsg())){
                    getUserInfo(new_token);
                }else {
                    getUserInfo(token);
                    Log.e("token返回",token.getErrcode()+token.getErrmsg());
                }
            }
        });
    }

    private void getUserInfo(WeChatToken token) {
        String url  = "https://api.weixin.qq.com/sns/userinfo?"
                +"access_token="+token.getAccess_token()
                +"&openid="+token.getOpenid();
        OkHttpUtil.getInstance().doGet(url, new OkHttpUtil.CallBack() {
            @Override
            public void failed(Exception e) {
                Log.e("获取用户信息失败：",e.toString());
            }

            @Override
            public void success(String json) {
                Gson gson = new Gson();
                WeChatUserBean weChatUserBean = gson.fromJson(json, WeChatUserBean.class);
                if (isEmpty(weChatUserBean.getErrmsg())){
                    Intent intent;
                    intent = new Intent(WXEntryActivity.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    Log.e("获取用户信息失败：",weChatUserBean.getErrcode()+weChatUserBean.getErrmsg());
                }
            }
        });
    }
}