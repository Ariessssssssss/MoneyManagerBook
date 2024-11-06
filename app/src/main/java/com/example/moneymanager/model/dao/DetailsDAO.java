package com.example.moneymanager.model.dao;

import android.content.ContentValues;
import net.sqlcipher.Cursor;
import android.util.Log;

import com.example.moneymanager.model.bean.DetailsBean;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 明细数据访问对象类
 * 负责账目明细相关的数据库操作
 */
public class DetailsDAO {
    private static final String TAG = "DetailsDAO";
    private SQLiteDatabase db;

    public DetailsDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 插入记录到明细表
     * @param bean 明细数据对象
     * @return 插入结果
     */
    public boolean insertItem(DetailsBean bean) {
        boolean success = false;
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("typename", bean.getTypename());
            values.put("sImageId", bean.getsImageId());
            values.put("record", bean.getRecord());
            values.put("money", bean.getMoney());
            values.put("time", bean.getTime());
            values.put("year", bean.getYear());
            values.put("month", bean.getMonth());
            values.put("day", bean.getDay());
            values.put("kind", bean.getKind());

            long result = db.insert("detailstb", null, values);
            success = result != -1;

            db.setTransactionSuccessful();
            Log.i(TAG, "insertItem: ok!!!!");
        } catch (Exception e) {
            Log.e(TAG, "Error inserting item: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    /**
     * 更新记录到明细表
     * @param id 记录ID
     * @param bean 明细数据对象
     * @return 更新结果
     */
    public boolean updateItem(int id, DetailsBean bean) {
        boolean success = false;
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("typename", bean.getTypename());
            values.put("sImageId", bean.getsImageId());
            values.put("record", bean.getRecord());
            values.put("money", bean.getMoney());
            values.put("time", bean.getTime());
            values.put("year", bean.getYear());
            values.put("month", bean.getMonth());
            values.put("day", bean.getDay());
            values.put("kind", bean.getKind());

            int rowsAffected = db.update("detailstb", values, "id = ?",
                    new String[]{String.valueOf(id)});
            success = rowsAffected > 0;

            db.setTransactionSuccessful();
            Log.i(TAG, "updateItem: ok!!!!");
        } catch (Exception e) {
            Log.e(TAG, "Error updating item: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    /**
     * 删除明细表中的记录
     * @param id 记录ID
     * @return 删除结果
     */
    public boolean deleteItem(int id) {
        boolean success = false;
        try {
            db.beginTransaction();
            int rowsAffected = db.delete("detailstb", "id = ?",
                    new String[]{String.valueOf(id)});
            success = rowsAffected > 0;

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting item: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    /**
     * 获取指定日期的明细列表
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @return 明细列表
     */
    public List<DetailsBean> getDetailsListOneDay(int year, int month, int day) {
        List<DetailsBean> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select * from detailstb where year=? and month=? and day=? order by id desc";
            cursor = db.rawQuery(sql, new String[]{
                    String.valueOf(year),
                    String.valueOf(month),
                    String.valueOf(day)
            });
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                int sImageId = cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
                String record = cursor.getString(cursor.getColumnIndexOrThrow("record"));
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                int kind = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
                DetailsBean detailsBean = new DetailsBean(id, typename, sImageId, record, money, time, year, month, day, kind);
                list.add(detailsBean);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting daily details list: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 获取指定月份的明细列表
     * @param year 年份
     * @param month 月份
     * @return 明细列表
     */
    public List<DetailsBean> getDetailsListOneMonth(int year, int month) {
        List<DetailsBean> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select * from detailstb where year=? and month=? order by time ASC";
            cursor = db.rawQuery(sql, new String[]{
                    String.valueOf(year),
                    String.valueOf(month)
            });
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                int sImageId = cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
                String record = cursor.getString(cursor.getColumnIndexOrThrow("record"));
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                int day = cursor.getInt(cursor.getColumnIndexOrThrow("day"));
                int kind = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
                DetailsBean detailsBean = new DetailsBean(id, typename, sImageId, record, money, time, year, month, day, kind);
                list.add(detailsBean);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting monthly details list: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 根据备注搜索记录
     * @param record 备注关键字
     * @return 记录列表
     */
    public List<DetailsBean> getListByRecord(String record) {
        List<DetailsBean> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select * from detailstb where record like ?";
            cursor = db.rawQuery(sql, new String[]{"%" + record + "%"});
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                int sImageId = cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String remark = cursor.getString(cursor.getColumnIndexOrThrow("record"));
                int year = cursor.getInt(cursor.getColumnIndexOrThrow("year"));
                int month = cursor.getInt(cursor.getColumnIndexOrThrow("month"));
                int day = cursor.getInt(cursor.getColumnIndexOrThrow("day"));
                int kind = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
                DetailsBean detailsBean = new DetailsBean(id, typename, sImageId, remark, money, time, year, month, day, kind);
                list.add(detailsBean);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting details by record: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 获取所有年份列表
     * @return 年份列表
     */
    public List<Integer> getYearList() {
        List<Integer> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select distinct(year) from detailstb order by year asc";
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int year = cursor.getInt(cursor.getColumnIndexOrThrow("year"));
                list.add(year);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting year list: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 获取指定日期的总金额
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param kind 类型（0=支出，1=收入）
     * @return 总金额
     */
    public float getSumMoneyOneDay(int year, int month, int day, int kind) {
        float total = 0.0f;
        Cursor cursor = null;
        try {
            String sql = "select SUM(money) from detailstb where year = ? and month = ? and day = ? and kind = ? order by time asc";
            cursor = db.rawQuery(sql, new String[]{
                    String.valueOf(year),
                    String.valueOf(month),
                    String.valueOf(day),
                    String.valueOf(kind)
            });
            if (cursor.moveToFirst()) {
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("SUM(money)"));
                total = money;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting daily sum: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }

    /**
     * 获取指定月份的总金额
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 总金额
     */
    public float getSumMoneyOneMonth(int year, int month, int kind) {
        float total = 0.0f;
        Cursor cursor = null;
        try {
            String sql = "select SUM(money) from detailstb where year = ? and month = ? and kind = ? order by time asc";
            cursor = db.rawQuery(sql, new String[]{
                    String.valueOf(year),
                    String.valueOf(month),
                    String.valueOf(kind)
            });
            if (cursor.moveToFirst()) {
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("SUM(money)"));
                total = money;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting monthly sum: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }

    /**
     * 获取指定年份的总金额
     * @param year 年份
     * @param kind 类型（0=支出，1=收入）
     * @return 总金额
     */
    public float getSumMoneyOneYear(int year, int kind) {
        float total = 0.0f;
        Cursor cursor = null;
        try {
            String sql = "select SUM(money) from detailstb where year = ? and kind = ? order by time asc";
            cursor = db.rawQuery(sql, new String[]{
                    String.valueOf(year),
                    String.valueOf(kind)
            });
            if (cursor.moveToFirst()) {
                float money = cursor.getFloat(cursor.getColumnIndexOrThrow("SUM(money)"));
                total = money;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting yearly sum: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }

    /**
     * 获取指定月份的记录数量
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 记录数量
     */
    public int getCountItemOneMonth(int year, int month, int kind) {
        int total = 0;
        Cursor cursor = null;
        try {
            String sql = "select Count(money) from detailstb where year = ? and month = ? and kind = ?";
            cursor = db.rawQuery(sql, new String[]{
                    String.valueOf(year),
                    String.valueOf(month),
                    String.valueOf(kind)
            });
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("Count(money)"));
                total = count;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting monthly count: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }
    /**
     * 获取所有明细记录的游标
     * @return 数据库游标
     */
    public Cursor getAllDetailsCursor() {
        if (db == null || !db.isOpen()) {
            throw new IllegalStateException("Database not initialized");
        }

        // Query all records from the detailstb table
        return db.query("detailstb", null, null, null, null, null, "time DESC");
    }

    /**
     * 清空所有记录
     * @return 操作结果
     */
    public boolean deleteAllDetails() {
        boolean success = false;
        try {
            db.beginTransaction();
            String sql = "delete from detailstb";
            db.execSQL(sql);
            success = true;
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting all details: " + e.getMessage());
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }
}