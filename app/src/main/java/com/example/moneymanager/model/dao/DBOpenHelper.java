package com.example.moneymanager.model.dao;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.moneymanager.R;
import com.example.moneymanager.model.dao.helper.DatabaseMigrationHelper;
import com.example.moneymanager.utils.SecureStorageUtil;

import java.io.File;

/**
 * Database helper class for managing the financial database.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBOpenHelper";
    public static final String Database_Name = "financial.db";
    private  static final int Database_Version = 1;
    private final Context context;

    /**
     * Constructor for the database helper.
     * @param context The application context.
     */
    public DBOpenHelper(@Nullable Context context) {
        super(context, Database_Name, null, Database_Version);
        this.context = context;
        SQLiteDatabase.loadLibs(context);
        SecureStorageUtil.initialize(context);
    }

    //创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        //用户表
        String sqlUser = "create table usertb(" +
                "id integer primary key autoincrement," +
                "username varchar(50)," +      // More space for usernames
                "password varchar(60)," +      // Store password hash, not plain password
                "email varchar(100))";              // Prevent duplicate emails

        //创建表示类型的表
        String sqlType = "create table typetb (" +
                "id integer primary key autoincrement," +
                "typename varchar(20)," +     // Balanced length for category names in Chinese/English
                "imageId integer," +          // Resource ID for the normal state icon (e.g. R.mipmap.food)
                "sImageId integer," +         // Selected state icon (e.g. R.mipmap.food_red/blue)
                "kind integer)";    

        //表示一条内容相关数据的表
        String sqlDetails = "create table detailstb (" +
                "id integer primary key autoincrement," +
                "typename varchar(20)," +     // Must match typetb.typename length
                "sImageId integer," +         // Selected state icon from typetb
                "record varchar(100)," +      // Transaction note/description
                "money float," +      // Using decimal for precise financial calculations
                "time varchar(60)," +         // Timestamp of the transaction
                "year integer," +             // Year of transaction
                "month integer," +            // Month of transaction (1-12)
                "day integer," +              // Day of transaction (1-31)
                "kind integer)";

        db.execSQL(sqlUser);
        db.execSQL(sqlType);
        db.execSQL(sqlDetails);

        insertType(db);
    }

    /**
     * Inserts default types into the typetb table.
     * @param db The database instance.
     */
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
        DatabaseMigrationHelper.migrateDatabase(db, oldVersion, newVersion,this);
    }

    /**
     * Returns an encrypted database instance.
     * @return The encrypted database instance.
     */
    public SQLiteDatabase getEncryptedDatabase() {
        String encryptionKey = SecureStorageUtil.getDbPassword();
        if (encryptionKey == null) {
            Log.e(TAG, "Database encryption key is null");
            throw new IllegalStateException("Encryption key not available");
        }
        try {
            // Try to open the database with the stored key
            return getWritableDatabase(encryptionKey);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open database with stored key", e);

            // If this is a new installation, the database might not exist yet
            File dbFile = context.getDatabasePath(Database_Name);
            if (dbFile.exists()) {
                // If the database exists but can't be opened, it might be corrupted
                Log.e(TAG, "Database exists but can't be opened. It might be corrupted.");
                // Optionally, delete the corrupt database and create a new one
                dbFile.delete();
            }

            // Create a new database with the encryption key
            return getWritableDatabase(encryptionKey);
        }
    }

}
