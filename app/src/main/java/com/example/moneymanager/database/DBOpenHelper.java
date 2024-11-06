package com.example.moneymanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.moneymanager.R;

public class DBOpenHelper extends SQLiteOpenHelper {

    public DBOpenHelper(@Nullable Context context) {
        super(context, "financial.db", null, 1);
    }

    //创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        //用户表
        String sqlUser = "create table usertb(id integer primary key autoincrement,username varchar(10),password varchar(15))";
        //创建表示类型的表
        String sqlType = "create table typetb (id integer primary key autoincrement,typename varchar(10),imageId integer,sImageId integer,kind integer)";
        //表示一条内容相关数据的表
        String sqlDetails = "create table detailstb (id integer primary key autoincrement,typename varchar(10),sImageId integer,record varchar(100)," +
                "money float,time varchar(60),year integer,month integer,day integer,kind integer)";

        db.execSQL(sqlUser);
        db.execSQL(sqlType);
        db.execSQL(sqlDetails);
        insertType(db);
    }

    private void insertType(SQLiteDatabase db) {
        String sql = "insert into typetb (typename,imageId,sImageId,kind) values (?,?,?,?)";
        db.execSQL(sql,new Object[]{"餐饮", R.mipmap.food,R.mipmap.food_red,0});
        db.execSQL(sql,new Object[]{"交通", R.mipmap.transport,R.mipmap.transport_red,0});
        db.execSQL(sql,new Object[]{"娱乐", R.mipmap.play,R.mipmap.play_red,0});
        db.execSQL(sql,new Object[]{"服饰", R.mipmap.cloths,R.mipmap.cloths_red,0});
        db.execSQL(sql,new Object[]{"日用品", R.mipmap.daily_necessity,R.mipmap.daily_necessity_red,0});
        db.execSQL(sql,new Object[]{"住房", R.mipmap.house,R.mipmap.house_red,0});
        db.execSQL(sql,new Object[]{"医疗", R.mipmap.medical,R.mipmap.medical_red,0});
        db.execSQL(sql,new Object[]{"教育", R.mipmap.study,R.mipmap.study_red,0});
        db.execSQL(sql,new Object[]{"投资", R.mipmap.investment,R.mipmap.investment_red,0});
        db.execSQL(sql,new Object[]{"其他", R.mipmap.other,R.mipmap.other_red,0});

        db.execSQL(sql,new Object[]{"工资", R.mipmap.wages,R.mipmap.wages_blue,1});
        db.execSQL(sql,new Object[]{"奖金", R.mipmap.bonuses,R.mipmap.bonuses_blue,1});
        db.execSQL(sql,new Object[]{"兼职", R.mipmap.part_time,R.mipmap.part_time_blue,1});
        db.execSQL(sql,new Object[]{"理财", R.mipmap.in_investment,R.mipmap.in_investment_blue,1});
        db.execSQL(sql,new Object[]{"其他", R.mipmap.in_other,R.mipmap.in_other_blue,1});
    }

    //数据库版本更新时发生改变，调用此方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
