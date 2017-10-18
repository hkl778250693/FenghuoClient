package com.fenghks.business.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.OrderItem;
import com.fenghks.business.bean.XGNotification;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fenghuo on 2017/9/13.
 */

public class MyDBCallBack implements MySqliteDBOpenhelper.ICallBack {
    private static final int DB_VERSION = 2;  //版本号
    private static final String DB_NAME = "fenghuo.db";  //数据库名称

    //表名称
    public static final String TABLE_NAME_ORDERS = "orders";
    public static final String TABLE_NAME_ORDER_DETAIL = "orderdetail";
    public static final String TABLE_NAME_MENU_ITEM = "menuitem";
    public static final String TABLE_NAME_NOTIFICATION = "notification";
    public static final String TABLE_NAME_PRINTER = "printer";

    /**
     * 订单表
     */
    private static final String CREATE_TABLE_ORDER =
            "CREATE TABLE " + TABLE_NAME_ORDERS + " (id integer primary key,areaid integer," +
                    "businessid integer,senderid integer,firecode varchar(50),ordercode varchar(50)," +
                    "clientfrom varchar(50),customername varchar(200),customerphone varchar(200)," +
                    "sendaddress varchar(200),totalprice decimal(18,2),originalfee decimal(18,2)," +
                    "sendfee decimal(18,2),lon decimal(18,6),lat decimal(18,6),state integer," +
                    "distance integer,timeconsuming integer,urgenum integer,ismerge integer,sendtime datetime," +
                    "orderstarttime datetime,arrivedtime datetime,gettime datetime,validatecode varchar(50)," +
                    "timelimit integer,qrcode varchar(50),isdelete integer,createtime datetime," +
                    "note varchar(500),exceptionresult text,tag varchar(50),mileage integer," +
                    "createtype integer,isdoexception integer,getcode varchar(50),issenderover integer)";

    /**
     * 订单详情表
     */
    private static final String CREATE_TABLE_ORDER_DETAIL =
            "CREATE TABLE " + TABLE_NAME_ORDER_DETAIL + " (id integer primary key autoincrement,itemid integer not null," +
                    "orderid integer,name varchar(128),amount integer,price decimal(18,2),totalprice decimal(18,2)," +
                    "note varchar(500),rawdata text,cartid int default 0,itemtype int default 0)";

    /**
     * 菜单表
     */
    private static final String CREATE_TABLE_MENU_ITEM =
            "CREATE TABLE " + TABLE_NAME_MENU_ITEM + " (id integer primary key autoincrement,name varchar(128)," +
                    "price decimal(18,2),note varchar(500),rawdata text,isdelete integer)";

    /**
     * 信鸽推送消息表
     */
    private static final String CREATE_TABLE_XG_MESSAGE =
            "CREATE TABLE " + TABLE_NAME_NOTIFICATION + " (id integer primary key autoincrement,msg_id varchar(64)," +
                    "title varchar(128),activity varchar(256),notificationActionType varchar(512)," +
                    "content text,update_time varchar(16))";


    public MyDBCallBack() {

    }

    @Override
    public String getDatabaseName() {
        return DB_NAME;
    }

    @Override
    public int getVersion() {
        return DB_VERSION;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String update_1_to_2 = "alter table orderdetail add itemid integer not null,cartid int default 0,itemtype int default 0;";
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL(update_1_to_2);
        }
    }

    @Override
    public List<String> createTablesSQL() {
        return Arrays.asList(CREATE_TABLE_ORDER, CREATE_TABLE_ORDER_DETAIL, CREATE_TABLE_MENU_ITEM, CREATE_TABLE_XG_MESSAGE);
    }

    @Override
    public <T> void assignValuesByEntity(String tableName, T entity, ContentValues values) {
        switch (tableName) {
            case TABLE_NAME_ORDERS:
                if (entity instanceof Order) {  //订单表
                    Order order = (Order) entity;

                }
                break;
            case TABLE_NAME_ORDER_DETAIL:  //订单详情条目
                if (entity instanceof OrderItem) {
                    OrderItem item = (OrderItem) entity;
                }
                break;
            case TABLE_NAME_MENU_ITEM:    //菜单表

                break;
            case TABLE_NAME_NOTIFICATION:
                if (entity instanceof XGNotification) {
                    XGNotification notification = (XGNotification) entity;
                }
                break;
        }
    }

    @Override
    public Object newEntityByCursor(String tableName, Cursor cursor) {
        switch (tableName) {
            case TABLE_NAME_ORDERS:
            case TABLE_NAME_ORDER_DETAIL:
            case TABLE_NAME_MENU_ITEM:
            case TABLE_NAME_NOTIFICATION:
        }
        return null;
    }
}
