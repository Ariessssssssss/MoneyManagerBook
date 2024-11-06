package com.example.moneymanager.model.bean;

public class ChartItemBean {
    int sImageId;
    String type;
    float percentage;
    float totalMoney;

    public int getsImageId() {
        return sImageId;
    }

    public void setsImageId(int sImageId) {
        this.sImageId = sImageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public float getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(float totalMoney) {
        this.totalMoney = totalMoney;
    }

    public ChartItemBean() {
    }

    public ChartItemBean(int sImageId, String type, float percentage, float totalMoney) {
        this.sImageId = sImageId;
        this.type = type;
        this.percentage = percentage;
        this.totalMoney = totalMoney;
    }
}
