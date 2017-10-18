package com.fenghks.business.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fenghuo on 2017/9/13.
 */

public class MySqliteDBOpenhelper extends SQLiteOpenHelper {
    private static final String TAG = "MySqliteDBOpenhelper";
    private static MySqliteDBOpenhelper INSTANCE;
    private final ICallBack mCallBack;


    private MySqliteDBOpenhelper(Context context, ICallBack callBack) {
        super(context, callBack.getDatabaseName(), null, callBack.getVersion());
        this.mCallBack = callBack;
    }

    //初始化
    public static void init(@NonNull Context context, @NonNull ICallBack callBack) {
        INSTANCE = new MySqliteDBOpenhelper(context, callBack);
    }

    /**
     * 插入单个数据
     *
     * @param tableName
     * @param entity
     * @param <T>
     */
    public static <T> void insert(String tableName, T entity) {
        SQLiteDatabase db = INSTANCE.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            INSTANCE.mCallBack.assignValuesByEntity(tableName, entity, values);
            db.insert(tableName, null, values);
            values.clear();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 插入多个数据
     *
     * @param tableName
     * @param entities
     * @param <T>
     */
    public static <T> void insert(String tableName, List<T> entities) {
        SQLiteDatabase db = INSTANCE.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (T entity : entities) {
                INSTANCE.mCallBack.assignValuesByEntity(tableName, entity, values);
                db.insert(tableName, null, values);
                values.clear();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 更新数据
     *
     * @param tableName
     * @param entity
     * @param whereClause
     * @param whereArgs
     * @param <T>
     */
    public static <T> void update(String tableName, T entity, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = INSTANCE.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            INSTANCE.mCallBack.assignValuesByEntity(tableName, entity, values);
            db.update(tableName, values, whereClause, whereArgs);
            values.clear();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 查询多条数据
     *
     * @param tableName
     * @param queryStr
     * @param whereArgs
     * @param <T>
     * @return
     */
    public static <T> List<T> queryList(String tableName, @NonNull String queryStr, @Nullable String[] whereArgs) {
        SQLiteDatabase db = INSTANCE.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryStr, whereArgs);
        Log.d(TAG, "查询到集合的长度为：" + cursor.getCount());
        try {
            List<T> lists = new ArrayList<>(cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    T entity = INSTANCE.mCallBack.newEntityByCursor(tableName, cursor);
                    if (entity != null) {
                        lists.add(entity);
                    }
                } while (cursor.moveToNext());
            }
            return lists;
        } finally {
            cursor.close();
            db.close();
        }
    }

    /**
     * 查询单个数据
     *
     * @param <T>
     * @param tableName
     * @param queryStr
     * @param whereArgs
     * @return
     */
    public static <T> T querySingle(String tableName, @NonNull String queryStr, @Nullable String[] whereArgs) {
        SQLiteDatabase db = INSTANCE.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryStr, whereArgs);
        try {
            T entity = null;
            if (cursor.moveToFirst()) {
                do {
                    entity = INSTANCE.mCallBack.newEntityByCursor(tableName, cursor);
                    if (entity == null) {
                        return null;
                    }
                } while (cursor.moveToNext());
            }
            return entity;
        } finally {
            cursor.close();
            db.close();
        }
    }

    /**
     * 删除表中所有的数据
     *
     * @param tableName
     */
    public static void deleteFrom(String tableName) {
        SQLiteDatabase db = INSTANCE.getWritableDatabase();
        db.beginTransaction();
        try {
            String sql = "DELETE FROM " + tableName;
            db.execSQL(sql);
            /*设置id从1开始（sqlite默认id从1开始），若没有这一句，id将会延续删除之前的id
                db.execSQL("update sqlite_sequence set seq=0 where name='表名'");*/
            db.execSQL("update sqlite_sequence set seq=0 where name='" + tableName + "'");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // delete的适用场合是涉及到删除的对象数量较少时。
    // 当删除多条数据时（例如：500条），通过循环的方式来一个一个的删除需要12s，而使用execSQL语句结合(delete from table id in("1", "2", "3"))的方式只需要50ms
    public static void delete(String tableName, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = INSTANCE.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(tableName, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 当操作数据较多时，直接使用sql语句或许效率更高
     * <p>
     * 执行sql语句（例如: String sql = "delete from tableName where mac in ('24:71:89:0A:DD:82', '24:71:89:0A:DD:83','24:71:89:0A:DD:84')"）
     * 注意：db.execSQL文档中有说明"the SQL statement to be executed. Multiple statements separated by semicolons are not supported."，
     * 也就是说通过分号分割的多个statement操作是不支持的。
     *
     * @param sql
     */
    public static void execSQL(String sql) {
        SQLiteDatabase db = INSTANCE.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> tablesSQL = mCallBack.createTablesSQL();
        for (String create_table : tablesSQL) {
            db.execSQL(create_table);
            Log.d(TAG, "create table " + "===" + create_table + "===" + " successful! ");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        mCallBack.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }

    public interface ICallBack {
        String getDatabaseName();

        int getVersion();

        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

        List<String> createTablesSQL();

        <T> void assignValuesByEntity(String tableName, T entity, ContentValues values);

        <T> T newEntityByCursor(String tableName, Cursor cursor);
    }
}
