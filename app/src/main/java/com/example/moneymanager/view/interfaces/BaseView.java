package com.example.moneymanager.view.interfaces;

import com.example.moneymanager.model.bean.TypeBean;

import java.util.List;

public interface BaseView {
    void showMessage(String message);
    void showLoading();
    void hideLoading();
}