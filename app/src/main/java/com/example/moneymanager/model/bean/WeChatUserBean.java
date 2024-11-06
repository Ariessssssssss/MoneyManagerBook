package com.example.moneymanager.model.bean;

import java.util.List;

public class WeChatUserBean {
    private String errmsg;

    private String errcode;
    private String openid;
    private String nickname;
    private Integer sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private List<?> privilege;

    public String getErrmsg() {
        return errmsg;
    }

    public String getErrcode() {
        return errcode;
    }

    public String getOpenid() {
        return openid;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getSex() {
        return sex;
    }

    public String getLanguage() {
        return language;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getCountry() {
        return country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public List<?> getPrivilege() {
        return privilege;
    }

    public String getUnionid() {
        return unionid;
    }

    private String unionid;

}
