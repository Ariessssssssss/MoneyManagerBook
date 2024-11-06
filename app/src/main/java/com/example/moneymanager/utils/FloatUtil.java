package com.example.moneymanager.utils;

import java.math.BigDecimal;

public class FloatUtil {

    public static float div(float v1,float v2){
        float v3 = (v1 / v2);
        BigDecimal b1 = new BigDecimal(v3);
        float value = b1.setScale(4, 4).floatValue();
        return value;
    }

    public static String ratioToPercent(float value){
        float v = value * 100;
        BigDecimal decimal = new BigDecimal(v);
        float val = decimal.setScale(2, 4).floatValue();
        String percent = val + "%";
        return percent;
    }
}
