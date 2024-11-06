package com.example.moneymanager.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.moneymanager.model.bean.UserBean;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问对象类
 * 负责用户相关的数据库操作
 */
public class UserDAO {
    private static final String TAG = "UserDAO";
    private SQLiteDatabase db;

    public UserDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 向用户表插入用户数据
     * @param bean 用户数据对象
     * @return 插入结果，true表示成功
     */
    public boolean insertUser(UserBean bean){
        boolean success = false;
        try{
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("username", bean.getUsername());
            values.put("password", bean.getPassword());
            values.put("email", bean.getEmail());

            long result = db.insert("usertb", null, values);
            success = result != -1;

            db.setTransactionSuccessful();
            Log.i(TAG, "insertUser: ok!!!!");
        } catch (Exception e){
            Log.e(TAG, "Error inserting user: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息对象，不存在则返回null
     */
    public UserBean getUserByUsername(String username) {
        UserBean userBean = null;
        Cursor cursor = null;
        try {
            String sql = "select * from usertb where username = ?";
            cursor = db.rawQuery(sql, new String[]{username});
            if (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String pwd = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                userBean = new UserBean(id, name, pwd, email);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by username: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userBean;
    }

    /**
     * 检查用户登录凭据是否有效
     * @param username 用户名
     * @param password 密码
     * @return 是否有效
     */
    public boolean checkUser(String username, String password) {
        return checkUserExists(username, password) ;
    }

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    public boolean isUserExist(String username,String password) {
        return checkUserExists(username,password);
    }
    /**
     * 查询用户是否存在
     * @param username 用户名
     * @param password 密码
     * @return 匹配的用户，如果不存在返回null
     */
/*
    public UserBean selectUser(String username, String password){
        UserBean userBean = null;
        Cursor cursor = null;
        try {
            String sql = "select * from usertb where username = ? and password = ?";
            cursor = db.rawQuery(sql, new String[]{username, password});
            if (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String pwd = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                userBean = new UserBean(id, name, pwd, email);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error selecting user: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userBean;
    }
*/

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    public boolean checkUserExists(String username,String password) {
        boolean exists = false;
        Cursor cursor = null;
        try {
            String sql = "select count(*) from usertb where username = ?and password = ?";
            cursor = db.rawQuery(sql, new String[]{username,password});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                exists = count > 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking if user exists: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }

    /**
     * 更新用户信息
     * @param bean 用户信息
     * @return 更新结果
     */
    public boolean updateUser(UserBean bean) {
        boolean success = false;
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("username", bean.getUsername());
            values.put("password", bean.getPassword());
            values.put("email", bean.getEmail());

            int rowsAffected = db.update("usertb", values, "id = ?",
                    new String[]{String.valueOf(bean.getId())});
            success = rowsAffected > 0;

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error updating user: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }
    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    public List<UserBean> getAllUsers() {
        List<UserBean> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select * from usertb";
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String pwd = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                UserBean userBean = new UserBean(id, name, pwd, email);
                list.add(userBean);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all users: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    public boolean deleteUser(int id) {
        boolean success = false;
        try {
            db.beginTransaction();
            int rowsAffected = db.delete("usertb", "id = ?", new String[]{String.valueOf(id)});
            success = rowsAffected > 0;
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting user: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }
}