package com.example.moneymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.moneymanager.utils.FloatUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
* 负责管理数据库
* 增删改查
* */
public class DBManager {
    private static SQLiteDatabase db;
    public static void initDB(Context context){
        DBOpenHelper helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public static void insertUserToUsertb(UserBean bean){
        ContentValues values = new ContentValues();
        values.put("username",bean.getUsername());
        values.put("password",bean.getPassword());
        db.insert("usertb",null,values);
        Log.i("aries","insertUserToUsertb: ok!!!!");
    }

    public static int selectUserFromUsertb(String username,String password){
        List<UserBean> list = new ArrayList<>();
        String sql = "select * from usertb where username = ? and password = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{username + "", password + ""});
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String pwd = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            UserBean userBean = new UserBean(id, name, pwd);
            list.add(userBean);
            return list.size();
        }
        cursor.close();
        return 0;
    }

    public static List<TypeBean>getTypeList(int kind){
        List<TypeBean> list = new ArrayList<>();
        String sql = "select * from typetb where kind = "+kind;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
            int imageId = cursor.getInt(cursor.getColumnIndexOrThrow("imageId"));
            int sImageId = cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
            int kind1 = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            TypeBean typeBean = new TypeBean(id,typename,imageId,sImageId,kind1);
            list.add(typeBean);
        }
        cursor.close();
        return list;
    }
    public static  void updateItemToDeatilstb(int id,DetailsBean bean){
        ContentValues values = new ContentValues();
        values.put("typename",bean.getTypename());
        values.put("sImageId",bean.getsImageId());
        values.put("record",bean.getRecord());
        values.put("money",bean.getMoney());
        values.put("time",bean.getTime());
        values.put("year",bean.getYear());
        values.put("month",bean.getMonth());
        values.put("day",bean.getDay());
        values.put("kind",bean.getKind());
        db.update("detailstb",values,"id = ?",new String[]{id +""});
        Log.i("aries","upadteItemToDetailstb: ok!!!!");
    }

    public static void insertItemToDetailstb(DetailsBean bean){
        ContentValues values = new ContentValues();
        values.put("typename",bean.getTypename());
        values.put("sImageId",bean.getsImageId());
        values.put("record",bean.getRecord());
        values.put("money",bean.getMoney());
        values.put("time",bean.getTime());
        values.put("year",bean.getYear());
        values.put("month",bean.getMonth());
        values.put("day",bean.getDay());
        values.put("kind",bean.getKind());
        db.insert("detailstb",null,values);
        Log.i("aries","insertItemToDetailstb: ok!!!!");
    }

    public static List<DetailsBean>getDetailsListOneDayFromDetailstb(int year,int month, int day){
        List<DetailsBean>list = new ArrayList<>();
        String sql = "select * from detailstb where year=? and month=? and day=? order by id DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{year + "", month + "", day + ""});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
            int sImageId = cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
            String record = cursor.getString(cursor.getColumnIndexOrThrow("record"));
            Float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            int kind = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
            DetailsBean detailsBean = new DetailsBean(id, typename, sImageId, record, money, time, year, month, day, kind);
            list.add(detailsBean);
        }
        cursor.close();
        return list;
    }

    public static List<DetailsBean>getDetailsListOneMonthFromDetailstb(int year,int month){
        List<DetailsBean>list = new ArrayList<>();
        String sql = "select * from detailstb where year=? and month=? order by time ASC";
        Cursor cursor = db.rawQuery(sql, new String[]{year + "", month + ""});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
            int sImageId = cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
            String record = cursor.getString(cursor.getColumnIndexOrThrow("record"));
            Float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            int day = cursor.getInt(cursor.getColumnIndexOrThrow("day"));
            int kind = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
            DetailsBean detailsBean = new DetailsBean(id, typename, sImageId, record, money, time, year, month, day, kind);
            list.add(detailsBean);
        }
        cursor.close();
        return list;
    }

    public static float getSumMoneyOneDay(int year, int month, int day, int kind){
        float total = 0.0f;
        String sql = "select SUM(money) from detailstb where year = ? and month = ? and day = ? and kind = ? order by time asc";
        Cursor cursor = db.rawQuery(sql, new String[]{year + "", month + "", day + "", kind + "",});
        if (cursor.moveToFirst()) {
            float money = cursor.getFloat(cursor.getColumnIndexOrThrow("SUM(money)"));
            total = money;
        }
        cursor.close();
        return total;
    }

    public static float getSumMoneyOneMonth(int year, int month, int kind){
        float total = 0.0f;
        String sql = "select SUM(money) from detailstb where year = ? and month = ? and kind = ? order by time asc";
        Cursor cursor = db.rawQuery(sql, new String[]{year + "", month + "", kind + "",});
        if (cursor.moveToFirst()) {
            float money = cursor.getFloat(cursor.getColumnIndexOrThrow("SUM(money)"));
            total = money;
        }
        cursor.close();
        return total;
    }

    public static float getSumMoneyOneYear(int year, int kind){
        float total = 0.0f;
        String sql = "select SUM(money) from detailstb where year = ? and kind = ? order by time asc";
        Cursor cursor = db.rawQuery(sql, new String[]{year + "", kind + "",});
        if (cursor.moveToFirst()) {
            float money = cursor.getFloat(cursor.getColumnIndexOrThrow("SUM(money)"));
            total = money;
        }
        cursor.close();
        return total;
    }

    public static int getCountItemOneMonth(int year, int month, int kind){
        int total = 0;
        String sql = "select Count(money) from detailstb where year = ? and month = ? and kind = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{year + "", month + "", kind + ""});
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(cursor.getColumnIndexOrThrow("Count(money)"));
            total = count;
        }
        cursor.close();
        return total;
    }

    public static List<DetailsBean>getListFromDetailstbByRecord(String record){
        List<DetailsBean>list = new ArrayList<>();
        String sql = "select * from detailstb where record like '%"+record+"%'";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
            int sImageId = cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
            Float money = cursor.getFloat(cursor.getColumnIndexOrThrow("money"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            String remark = cursor.getString(cursor.getColumnIndexOrThrow("record"));
            int year = cursor.getInt(cursor.getColumnIndexOrThrow("year"));
            int month = cursor.getInt(cursor.getColumnIndexOrThrow("month"));
            int day = cursor.getInt(cursor.getColumnIndexOrThrow("day"));
            int kind = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
            DetailsBean detailsBean = new DetailsBean(id, typename, sImageId, remark, money, time, year, month, day, kind);
            list.add(detailsBean);
        }
        cursor.close();
        return list;
    }

    public static List<Integer>getYearListFromDetailstb(){
        List<Integer>list = new ArrayList<>();
        String sql = "select distinct(year) from detailstb order by year asc";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int year = cursor.getInt(cursor.getColumnIndexOrThrow("year"));
            list.add(year);
        }
        cursor.close();
        return list;
    }

    public static int deleteItemFromDetailstbById(int id){
        int i = db.delete("detailstb", "id=?", new String[]{id + ""});
        return i;
    }

    public static void deleteAllDetails(){
        String sql = "delete from detailstb";
        db.execSQL(sql);

    }

    public static List<ChartItemBean> getChartListFromDetailstb(int year, int month, int kind){
        List<ChartItemBean>list = new ArrayList<>();
        float sumMoneyOneMonth = getSumMoneyOneMonth(year, month, kind);
        String sql = "select typename,sImageId,SUM(money) as total from detailstb where year = ? and month = ? and kind = ? group by typename "+
                "order by total desc";
        Cursor cursor = db.rawQuery(sql, new String[]{year+"",month+"",kind+""});
        while (cursor.moveToNext()) {
            int sImageId = cursor.getInt(cursor.getColumnIndexOrThrow("sImageId"));
            String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
            float total = cursor.getFloat(cursor.getColumnIndexOrThrow("total"));
            float percentage = FloatUtil.div(total,sumMoneyOneMonth);
            ChartItemBean bean = new ChartItemBean(sImageId, typename, percentage, total);
            list.add(bean);
        }
        cursor.close();
        return list;
    }

    public static float getMaxMoneyFromDetailstb0neMonth(int year, int month, int kind){
        String sql = "select sum(money) from detailstb where year = ? and month = ? and kind = ? group by day order by sum(money) desc";
        Cursor cursor = db.rawQuery(sql, new String[]{year + "", month + "", kind + ""});
        if (cursor.moveToFirst()) {
            float value = cursor.getFloat(cursor.getColumnIndexOrThrow("sum(money)"));
            return value;
        }
        cursor.close();
        return 0;

    }

    public static List<BarChartItemBean> getMoneyOneDay(int year, int month, int kind){
        List<BarChartItemBean> list = new ArrayList<>();
        String sql = "select day,sum(money) from detailstb where year = ? and month = ? and kind = ? group by day";
        Cursor cursor = db.rawQuery(sql, new String[]{year + "", month + "", kind + ""});
        while (cursor.moveToNext()) {
            int day = cursor.getInt(cursor.getColumnIndexOrThrow("day"));
            float sum = cursor.getFloat(cursor.getColumnIndexOrThrow("sum(money)"));
            BarChartItemBean bean = new BarChartItemBean(year, month, day, sum);
            list.add(bean);
        }
        cursor.close();
        return list;
    }

}
