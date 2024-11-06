package com.example.moneymanager.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.moneymanager.model.bean.TypeBean;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 类型数据访问对象类
 * 负责消费/收入类型相关的数据库操作
 */
public class TypeDAO {
    private static final String TAG = "TypeDAO";
    private SQLiteDatabase db;

    public TypeDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 获取类型列表
     * @param kind 类型（0=支出，1=收入）
     * @return 类型列表
     */
    public List<TypeBean> getTypeList(int kind) {
        List<TypeBean> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select * from typetb where kind = " + kind;
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                int imageId = cursor.getInt(cursor.getColumnIndexOrThrow("imageId"));
                int sImageId = cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
                int kind1 = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                TypeBean typeBean = new TypeBean(id, typename, imageId, sImageId, kind1);
                list.add(typeBean);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting type list: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 添加新类型
     * @param bean 类型数据
     * @return 添加结果
     */
    public boolean addType(TypeBean bean) {
        boolean success = false;
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("typename", bean.getTypename());
            values.put("imageId", bean.getImageId());
            values.put("sImageId", bean.getsImageId());
            values.put("kind", bean.getKind());

            long result = db.insert("typetb", null, values);
            success = result != -1;

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error adding type: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    /**
     * 更新类型
     * @param bean 类型数据
     * @return 更新结果
     */
    public boolean updateType(TypeBean bean) {
        boolean success = false;
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("typename", bean.getTypename());
            values.put("imageId", bean.getImageId());
            values.put("sImageId", bean.getsImageId());
            values.put("kind", bean.getKind());

            int rowsAffected = db.update("typetb", values, "id = ?",
                    new String[]{String.valueOf(bean.getId())});
            success = rowsAffected > 0;

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error updating type: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    /**
     * 删除类型
     * @param id 类型ID
     * @return 删除结果
     */
    public boolean deleteType(int id) {
        boolean success = false;
        try {
            db.beginTransaction();
            int rowsAffected = db.delete("typetb", "id = ?",
                    new String[]{String.valueOf(id)});
            success = rowsAffected > 0;

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting type: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }
}