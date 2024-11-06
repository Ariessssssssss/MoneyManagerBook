// app/src/main/java/com/example/moneymanager/controller/UserController.java
package com.example.moneymanager.controller;

import android.content.Context;

import com.example.moneymanager.model.bean.UserBean;
import com.example.moneymanager.model.service.UserService;
import com.example.moneymanager.view.interfaces.LoginView;

/**
 * 用户控制器
 * 负责处理用户账户相关的业务逻辑，作为View和Model的桥梁
 */
public class UserController {
    private UserService userService;
    private LoginView loginView;

    /**
     * 用于需要视图交互的场景
     * @param loginView 登录视图接口
     */
    public UserController(LoginView loginView) {
        this.loginView = loginView;
        Context context = (Context) loginView;
        this.userService = new UserService(context);
    }

    /**
     * 用于不需要视图交互的场景
     * @param context 上下文
     */
    public UserController(Context context) {
        this.userService = new UserService(context);
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     */
    public boolean login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            if (loginView != null) {
                loginView.showMessage("用户名和密码不能为空");
            }
            return false;
        }

        if (loginView != null) {
            loginView.showLoading();
        }

        boolean result = userService.login(username, password);

        if (loginView != null) {
            loginView.hideLoading();

            if (result) {
                loginView.saveLoginStatus(username, password);
                loginView.navigateToMain();
            } else {
                loginView.showMessage("用户名或密码错误");
            }
        }
        return result;
    }

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param email 电子邮箱
     */
    public boolean register(String username, String password, String confirmPassword, String email) {
        if (username.isEmpty() || password.isEmpty()) {
            if (loginView != null) {
                loginView.showMessage("用户名和密码不能为空");
            }
            return false;
        }

        if (!password.equals(confirmPassword)) {
            if (loginView != null) {
                loginView.showMessage("两次输入的密码不一致");
            }
            return false;
        }

        UserBean user = new UserBean();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        boolean result = userService.register(user);

        if (loginView != null) {
            if (result) {
                loginView.showMessage("注册成功");
                loginView.navigateToLogin();
            } else {
                loginView.showMessage("注册失败，用户名可能已存在");
            }
        }
        return result;
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param username 用户名
     * @param password 密码
     * @param email 电子邮箱
     * @return 是否更新成功
     */
    public boolean updateUserInfo(int id, String username, String password, String email) {
        UserBean user = new UserBean(id, username, password, email);
        return userService.updateUserInfo(user);
    }

    /**
     * 修改密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        return userService.changePassword(username, oldPassword, newPassword);
    }
}