package com.fenghks.business.orders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.OrderItem;
import com.fenghks.business.bean.XGNotification;
import com.fenghks.business.utils.DBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fei on 2017/4/17.
 */

public class OrderHelper {

    private DBOpenHelper dbOpenHelper;
    private static OrderHelper instance = null;

    public OrderHelper(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context);
    }

    public synchronized static OrderHelper getInstance(Context ctx) {
        if (null == instance) {
            instance = new OrderHelper(ctx);
        }
        return instance;
    }

    public long saveOrder(Order order) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", order.getId());
        values.put("businessid", order.getBusinessid());
        values.put("businessname", order.getBusinessname());
        values.put("senderid", order.getSenderid());
        values.put("firecode", order.getFirecode());
        values.put("ordercode", order.getOrdercode());
        values.put("customername", order.getCustomername());
        values.put("customerphone", order.getCustomerphone());
        values.put("sendaddress", order.getSendaddress());
        values.put("totalprice", order.getTotalprice());
        values.put("originalfee", order.getOriginalfee());
        values.put("sendfee", order.getSendfee());
        values.put("lon", order.getLon());
        values.put("lat", order.getLat());
        values.put("state", order.getState());
        values.put("distance", order.getDistance());
        values.put("timeconsuming", order.getTimeconsuming());
        values.put("urgenum", order.getUrgenum());
        values.put("ismerge", order.getIsmerge());
        values.put("createtime", order.getCreatetime());
        values.put("sendtime", order.getSendtime());
        values.put("starttime", order.getStarttime());
        values.put("gettime", order.getGettime());
        values.put("arrivedtime", order.getArrivedtime());
        values.put("validatecode", order.getValidatecode());
        values.put("getcode", order.getGetcode());
        values.put("timelimit", order.getTimelimit());
        values.put("qrcode", order.getQrcode());
        values.put("isdelete", order.getIsdelete());
        values.put("note", order.getNote());
        values.put("exceptionresult", order.getExceptionresult());
        values.put("tag", order.getTag());
        values.put("mileage", order.getMileage());
        values.put("createtype", order.getCreatetime());
        values.put("isdoexception", order.getIsdoexception());
        values.put("issenderover", order.getIssenderover());
        return db.insert("orders", null, values);
    }

    public long saveOrderItem(OrderItem item) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("itemid", item.getId());
        values.put("name", item.getName());
        values.put("amount", item.getAmount());
        values.put("price", item.getPrice());
        values.put("totalprice", item.getPrice());
        values.put("note", item.getNote());
        values.put("orderid", item.getOrderid());
        values.put("rawdata", item.getRawdata());
        values.put("cartid", item.getCartid());
        values.put("itemtype", item.getItemtype());
        return db.insert("orderdetail", null, values);
    }

    public void updateOrder(Order order) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("senderid", order.getSenderid());
        values.put("customername", order.getCustomername());
        values.put("customerphone", order.getCustomerphone());
        values.put("sendaddress", order.getSendaddress());
        values.put("sendfee", order.getSendfee());
        values.put("lon", order.getLon());
        values.put("lat", order.getLat());
        values.put("state", order.getState());
        values.put("distance", order.getDistance());
        values.put("timeconsuming", order.getTimeconsuming());
        values.put("urgenum", order.getUrgenum());
        values.put("starttime", order.getStarttime());
        values.put("gettime", order.getGettime());
        values.put("arrivedtime", order.getArrivedtime());
        values.put("timelimit", order.getTimelimit());
        values.put("isdelete", order.getIsdelete());
        values.put("exceptionresult", order.getExceptionresult());
        values.put("mileage", order.getMileage());
        values.put("isdoexception", order.getIsdoexception());
        values.put("issenderover", order.getIssenderover());
        db.update("orders", values, "id=?", new String[] { order.getId()+"" });
    }

    public XGNotification find(Integer id) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db
                .query("notification",
                        new String[] { "id,msg_id,title,content,activity,notificationActionType,update_time" },
                        "id=?", new String[] { id.toString() }, null, null,
                        null, "1");
        try {
            if (cursor.moveToFirst()) {
                return new XGNotification(cursor.getInt(cursor
                        .getColumnIndex("id")), cursor.getLong(cursor
                        .getColumnIndex("msg_id")), cursor.getString(cursor
                        .getColumnIndex("title")), cursor.getString(cursor
                        .getColumnIndex("content")), cursor.getString(cursor
                        .getColumnIndex("activity")), cursor.getInt(cursor
                        .getColumnIndex("notificationActionType")), cursor.getString(cursor
                        .getColumnIndex("update_time")));
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public List<XGNotification> getScrollData(int currentPage, int lineSize,
                                              String msg_id) {
        String firstResult = String.valueOf((currentPage - 1) * lineSize);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            if (msg_id == null || "".equals(msg_id)) {
                cursor = db
                        .query("notification",
                                new String[] { "id,msg_id,title,content,activity,notificationActionType,update_time" },
                                null, null, null, null, "update_time DESC",
                                firstResult + "," + lineSize);
            } else {
                cursor = db
                        .query("notification",
                                new String[] { "id,msg_id,title,content,activity,notificationActionType,update_time" },
                                "msg_id like ?", new String[] { msg_id + "%" },
                                null, null, "update_time DESC", firstResult
                                        + "," + lineSize);
            }
            List<XGNotification> notifications = new ArrayList<XGNotification>();
            while (cursor.moveToNext()) {
                notifications.add(new XGNotification(cursor.getInt(cursor
                        .getColumnIndex("id")), cursor.getLong(cursor
                        .getColumnIndex("msg_id")), cursor.getString(cursor
                        .getColumnIndex("title")), cursor.getString(cursor
                        .getColumnIndex("content")), cursor.getString(cursor
                        .getColumnIndex("activity")), cursor.getInt(cursor
                        .getColumnIndex("notificationActionType")), cursor.getString(cursor
                        .getColumnIndex("update_time"))));
            }
            return notifications;
        } finally {
            cursor.close();
        }
    }

    public int delete(int id) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        return db.delete("orders", "orderid=?", new String[] { ""+id});
    }

    public int deleteAll() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        return db.delete("orders", "", null);
    }

    public int getCount() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from orders", null);
        try {
            cursor.moveToFirst();
            return cursor.getInt(0);
        } finally {
            cursor.close();
        }
    }

}
