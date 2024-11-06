package com.example.moneymanager.model.service;

import android.content.Context;

import com.example.moneymanager.model.bean.UserBean;
import com.example.moneymanager.model.dao.DBManager;
import com.example.moneymanager.model.dao.UserDAO;
import net.sqlcipher.database.SQLiteDatabase;

/**
 * 用户服务类
 * 提供用户相关的业务逻辑处理
 */
public class UserService {
    private UserDAO userDAO;

    public UserService(Context context) {
        SQLiteDatabase db = DBManager.getDatabase();
        this.userDAO = new UserDAO(db);
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 是否登录成功
     */
    public boolean login(String username, String password) {
        return userDAO.checkUser(username, password);
    }

    /**
     * 用户注册
     * @param userBean 用户信息
     * @return 是否注册成功
     */
    public boolean register(UserBean userBean) {
        // 检查用户名是否已存在
        if (userDAO.isUserExist(userBean.getUsername(),userBean.getPassword())) {
            return false;
        }

        return userDAO.insertUser(userBean);
    }

    /**
     * 更新用户信息
     * @param userBean 用户信息
     * @return 是否更新成功
     */
    public boolean updateUserInfo(UserBean userBean) {
        return userDAO.updateUser(userBean);
    }

    /**
     * 获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    public UserBean getUserInfo(String username) {
        return userDAO.getUserByUsername(username);
    }

    /**
     * 修改密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        // 验证旧密码
        if (!login(username, oldPassword)) {
            return false;
        }

        // 获取用户信息并更新密码
        UserBean userBean = getUserInfo(username);
        if (userBean == null) {
            return false;
        }

        userBean.setPassword(newPassword);
        return updateUserInfo(userBean);
    }
}