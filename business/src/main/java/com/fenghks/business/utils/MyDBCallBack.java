package com.fenghks.business.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.OrderItem;
import com.fenghks.business.bean.Printer;
import com.fenghks.business.bean.PrinterEntry;
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

    /**
     * 打印机设置字段表
     */
    public static final String CREATE_TABLE_PRINTER =
            "CREATE TABLE " + TABLE_NAME_PRINTER + " (id integer primary key autoincrement," + PrinterEntry.COLUMN_NAME_IP + " varchar(64)," + PrinterEntry.COLUMN_NAME_SHOP_ID + " integer," +
                    PrinterEntry.COLUMN_NAME_PORT + " integer default 9100," + PrinterEntry.COLUMN_NAME_NAME + " varchar(128)," + PrinterEntry.COLUMN_NAME_BUSINESS_NUM + " integer default 1," +
                    PrinterEntry.COLUMN_NAME_KITCHEN_NUM + " integer default 0," + PrinterEntry.COLUMN_NAME_CUSTOMER_NUM + " integer default 0," + PrinterEntry.COLUMN_NAME_SENDER_NUM + " integer default 0," +
                    PrinterEntry.COLUMN_NAME_QRCODE + " integer default 0," + PrinterEntry.COLUMN_NAME_MANAGE_TOKEN + " varchar(256)," + PrinterEntry.COLUMN_NAME_IS_PAUSE + " integer default 0," +
                    PrinterEntry.COLUMN_NAME_PRINTER_TYPE + " integer," + PrinterEntry.COLUMN_NAME_PRINTER_STATUS + " integer)";

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
        return Arrays.asList(CREATE_TABLE_ORDER, CREATE_TABLE_ORDER_DETAIL, CREATE_TABLE_MENU_ITEM, CREATE_TABLE_XG_MESSAGE, CREATE_TABLE_PRINTER);
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
            case TABLE_NAME_PRINTER:
                if (entity instanceof Printer) {
                    Printer printer = (Printer) entity;
                    values.put(PrinterEntry.COLUMN_NAME_SHOP_ID, printer.getShopId());
                    values.put(PrinterEntry.COLUMN_NAME_IP, printer.getIp());
                    values.put(PrinterEntry.COLUMN_NAME_PORT, printer.getPort());
                    values.put(PrinterEntry.COLUMN_NAME_NAME, printer.getName());
                    values.put(PrinterEntry.COLUMN_NAME_BUSINESS_NUM, printer.getBusinessNum());
                    values.put(PrinterEntry.COLUMN_NAME_KITCHEN_NUM, printer.getKitchenNum());
                    values.put(PrinterEntry.COLUMN_NAME_CUSTOMER_NUM, printer.getCustomerNum());
                    values.put(PrinterEntry.COLUMN_NAME_SENDER_NUM, printer.getSenderNum());
                    values.put(PrinterEntry.COLUMN_NAME_QRCODE, printer.getQrcode());
                    values.put(PrinterEntry.COLUMN_NAME_IS_PAUSE, printer.getIspause());
                    values.put(PrinterEntry.COLUMN_NAME_PRINTER_TYPE, printer.getPrinterType());
                    values.put(PrinterEntry.COLUMN_NAME_PRINTER_STATUS, printer.getStatus());
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
            case TABLE_NAME_PRINTER:
                return new Printer(cursor.getInt(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_IP)),
                        cursor.getInt(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_PORT)),
                        cursor.getString(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_NAME)),
                        cursor.getInt(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_BUSINESS_NUM)),
                        cursor.getInt(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_KITCHEN_NUM)),
                        cursor.getInt(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_CUSTOMER_NUM)),
                        cursor.getInt(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_SENDER_NUM)),
                        cursor.getInt(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_QRCODE)),
                        cursor.getInt(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_IS_PAUSE)),
                        cursor.getInt(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_PRINTER_TYPE)),
                        cursor.getInt(cursor.getColumnIndex(PrinterEntry.COLUMN_NAME_PRINTER_STATUS)));
        }
        return null;
    }
}
