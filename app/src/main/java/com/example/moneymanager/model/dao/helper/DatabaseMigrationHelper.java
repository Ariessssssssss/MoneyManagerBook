package com.example.moneymanager.model.dao.helper;

import net.sqlcipher.database.SQLiteDatabase;
import android.util.Log;

import com.example.moneymanager.model.dao.DBOpenHelper;

/**
 * 数据库迁移助手类
 * 负责处理数据库版本升级和数据迁移
 */
public class DatabaseMigrationHelper {
    private static final String TAG = "DatabaseMigrationHelper";

    /**
     * 执行数据库迁移
     * @param db 数据库实例
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     * @param dbOpenHelper 数据库帮助类实例，用于创建新表
     */
    public static void migrateDatabase(SQLiteDatabase db, int oldVersion, int newVersion, DBOpenHelper dbOpenHelper) {
        try {
            // 开始事务，确保数据迁移的原子性
            db.beginTransaction();
            
            // 从版本1升级到版本2
            if (oldVersion == 1 && newVersion >= 2) {
                migrateV1ToV2(db, dbOpenHelper);
                oldVersion = 2;
            }
            
            // 从版本2升级到版本3（未来扩展）
            if (oldVersion == 2 && newVersion >= 3) {
                migrateV2ToV3(db, dbOpenHelper);
                oldVersion = 3;
            }
            
            // 可以继续添加更多版本的迁移逻辑
            
            // 标记事务成功
            db.setTransactionSuccessful();
            
        } catch (Exception e) {
            Log.e(TAG, "数据库迁移失败: " + e.getMessage(), e);
            
            // 如果迁移失败，确保数据库可用
            db.execSQL("DROP TABLE IF EXISTS usertb");
            db.execSQL("DROP TABLE IF EXISTS typetb");
            db.execSQL("DROP TABLE IF EXISTS detailstb");
            dbOpenHelper.onCreate(db);
        } finally {
            // 结束事务
            db.endTransaction();
        }
    }
    
    /**
     * 从版本1迁移到版本2
     */
    private static void migrateV1ToV2(SQLiteDatabase db, DBOpenHelper dbOpenHelper) {
        Log.i(TAG, "开始数据库升级：从版本1到版本2");
        
        // 1. 备份现有数据
        db.execSQL("CREATE TEMPORARY TABLE details_backup AS SELECT * FROM detailstb");
        db.execSQL("CREATE TEMPORARY TABLE types_backup AS SELECT * FROM typetb");
        db.execSQL("CREATE TEMPORARY TABLE users_backup AS SELECT * FROM usertb");
        
        // 2. 删除旧表
        db.execSQL("DROP TABLE IF EXISTS detailstb");
        db.execSQL("DROP TABLE IF EXISTS typetb");
        db.execSQL("DROP TABLE IF EXISTS usertb");
        
        // 3. 创建新表
        dbOpenHelper.onCreate(db);
        
        // 4. 恢复数据
        db.execSQL("INSERT INTO typetb SELECT * FROM types_backup");
        db.execSQL("INSERT INTO detailstb SELECT * FROM details_backup");
        db.execSQL("INSERT INTO usertb SELECT * FROM users_backup");
        
        // 5. 清理临时表
        db.execSQL("DROP TABLE IF EXISTS details_backup");
        db.execSQL("DROP TABLE IF EXISTS types_backup");
        db.execSQL("DROP TABLE IF EXISTS users_backup");
        
        Log.i(TAG, "数据库升级成功完成：版本1 -> 版本2");
    }
    
    /**
     * 从版本2迁移到版本3（未来扩展）
     */
    private static void migrateV2ToV3(SQLiteDatabase db, DBOpenHelper dbOpenHelper) {
        Log.i(TAG, "开始数据库升级：从版本2到版本3");
        
        // 在这里实现从版本2到版本3的迁移逻辑
        // 例如：添加新表、添加新列、修改数据类型等
        
        Log.i(TAG, "数据库升级成功完成：版本2 -> 版本3");
    }
}