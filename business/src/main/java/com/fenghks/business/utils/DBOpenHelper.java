package com.fenghks.business.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public static final int DB_VERSION = 2;
	public static final String DB_NAME = "fenghuo.db";

	public DBOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public static final String CREATE_TABLE_ORDER =
			"CREATE TABLE orders (id integer primary key,areaid integer," +
			"businessid integer,senderid integer,firecode varchar(50),ordercode varchar(50)," +
			"clientfrom varchar(50),customername varchar(200),customerphone varchar(200)," +
			"sendaddress varchar(200),totalprice decimal(18,2),originalfee decimal(18,2)," +
			"sendfee decimal(18,2),lon decimal(18,6),lat decimal(18,6),state integer," +
			"distance integer,timeconsuming integer,urgenum integer,ismerge integer,sendtime datetime," +
			"orderstarttime datetime,arrivedtime datetime,gettime datetime,validatecode varchar(50)," +
			"timelimit integer,qrcode varchar(50),isdelete integer,createtime datetime," +
			"note varchar(500),exceptionresult text,tag varchar(50),mileage integer," +
			"createtype integer,isdoexception integer,getcode varchar(50),issenderover integer)";

	public static final String CREATE_TABLE_ORDER_DETAIL =
		"CREATE TABLE orderdetail (id integer primary key autoincrement,itemid integer not null," +
		"orderid integer,name varchar(128),amount integer,price decimal(18,2),totalprice decimal(18,2)," +
		"note varchar(500),rawdata text,cartid int default 0,itemtype int default 0)";

	public static final String CREATE_TABLE_MENU_ITEM =
			"CREATE TABLE menuitem (id integer primary key autoincrement,name varchar(128)," +
			"price decimal(18,2),note varchar(500),rawdata text,isdelete integer)";

	public static final String CREATE_TABLE_XG_MESSAGE =
			"CREATE TABLE notification (id integer primary key autoincrement,msg_id varchar(64)," +
			"title varchar(128),activity varchar(256),notificationActionType varchar(512)," +
					"content text,update_time varchar(16))";

	public static final String CREATE_TABLE_PRINTER =
			"CREATE TABLE printer (id integer primary key autoincrement,ip varchar(64)," +
					"port integer default 9100,name varchar(128),businessNum integer default 1," +
					"kitchenNum integer default 0,customerNum integer default 0,senderNum integer default 0," +
					"qrcode integer default 0,ispause integer default 0,printertype integer)";

	@Override
	public void onCreate(SQLiteDatabase db) {
		//订单存储表
		db.execSQL(CREATE_TABLE_ORDER);

		//订单详情条目表
		db.execSQL(CREATE_TABLE_ORDER_DETAIL);

		//菜单表
		db.execSQL(CREATE_TABLE_MENU_ITEM);

		//信鸽消息存储
		db.execSQL(CREATE_TABLE_XG_MESSAGE);

		//打印机字段存储
		db.execSQL(CREATE_TABLE_PRINTER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String update_1_to_2 = "alter table orderdetail add itemid integer not null,cartid int default 0,itemtype int default 0;";
		if(oldVersion == 1 && newVersion == 2){
			db.execSQL(update_1_to_2);
		}
	}

}
