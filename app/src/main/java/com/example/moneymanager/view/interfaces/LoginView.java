package com.example.moneymanager.view.interfaces;

public interface LoginView extends BaseView {
    void saveLoginStatus(String username, String password);
    void navigateToMain();
    void navigateToLogin();
}
