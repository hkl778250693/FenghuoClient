package com.fenghks.business.print;

import android.content.Context;

import com.fenghks.business.R;
import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.OrderItem;
import com.fenghks.business.utils.CommonUtil;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Fei on 2017/4/26.
 */

public class PrintContent {
    /**
     * 头部
     */
    /*private String no;          /*//**#11美团外卖**
     private String shopName;    //店铺名
     private String orderTime;   //下单时间 2017-04-24 13:13:13
     private String bookTime;    //预定时间
     private String note;        //备注

     *//**
     * 菜品和费用
     *//*
    private String billType;    //用户支付/货到付款
    private String totalPrice;    //支付总价
    //private String originalfee;//原价

    *//**
     * 顾客信息
     *//*
    private String customerName;
    private String customerPhone;
    private String sendAddress;

    *//**
     * 二维码和取餐码
     *//*
    private String qrcode;
    private String getcode; //#12**123456*/

    /**
     * 生成打印格式json字符串
     *
     * @param printType 打印类型 0商家小票，1给后厨，2给顾客，3给配送,4二维码
     * @return 打印格式json字符串
     */
    public static String gen(Context context, Order order, int printType) {
        try {
            JSONObject obj = new JSONObject();
            JSONObject keys = new JSONObject();
            JSONObject goodObj = new JSONObject();

            JSONArray extras = new JSONArray();
            String[] nos = order.getOrdercode().split("#");
            if (nos.length > 1 && nos[nos.length - 1].matches("^[0-9]+$")) {
                keys.put("no", "--#" + nos[nos.length - 1] + order.getClientfrom() + "--");
            } else {
                keys.put("no", "--" + order.getClientfrom() + "--");
            }
            keys.put("shopName", order.getBusinessname());
            keys.put("orderTime", context.getString(R.string.order_time) +
                    new DateTime(order.getCreatetime()).toString("yyyy-MM-dd HH:mm:ss"));
            if (order.getOrderType() == 1) {
                String sendTime = new DateTime(order.getSendtime()).toString("MM-dd HH:mm");
                keys.put("note", context.getString(R.string.pre_send_time) + sendTime + "\n" + order.getNote());
            } else {
                keys.put("note", order.getNote());
            }
            keys.put("customerName", order.getCustomername());
            keys.put("customerPhone", order.getCustomerphone());
            keys.put("sendAddress", order.getSendaddress());
            switch (printType) {
                case 0:
                    keys.put("billType", context.getString(R.string.bill_unknown));
                    keys.put("totalPrice", order.getTotalprice() + context.getString(R.string.money_yuan));
                    break;
                case 1:
                    //keys.put("count",order.getTotalprice()+context.getString(R.string.money_yuan));
                    break;
                case 2:
                    keys.put("billType", context.getString(R.string.bill_unknown));
                    keys.put("totalPrice", order.getTotalprice() + context.getString(R.string.money_yuan));
                    break;
                case 3:
                    keys.put("billType", context.getString(R.string.bill_unknown));
                    keys.put("totalPrice", order.getTotalprice() + context.getString(R.string.money_yuan));
                    break;
                case 4:
                    keys.put("qrcode", CommonUtil.QR_BASE + order.getId());
                    if (nos.length > 1 && nos[nos.length - 1].matches("[0-9+]")) {
                        keys.put("getcode", "#" + nos[nos.length - 1] + " : " + order.getGetcode());
                    } else {
                        keys.put("getcode", order.getGetcode());
                    }
                    break;
            }
            obj.put("keys", keys);

            Map<Integer, List<OrderItem>> items = order.getItems();
            if (items != null) {
                List<OrderItem> tmp = null;
                OrderItem item = null;
                JSONArray goods = null;
                for (int j = 0; j < items.size(); j++) {
                    tmp = items.get(j);
                    goods = new JSONArray();
                    for (int i = 0; i < tmp.size(); i++) {
                        item = tmp.get(i);
                        JSONObject json = new JSONObject();
                        json.put("name", item.getName());
                        json.put("amount", item.getAmount());
                        json.put("price", item.getPrice());
                        goods.put(json);
                    }
                    goodObj.put(j + 1 + "号篮子", goods);
                }
                obj.put("goods", goodObj);
            }

            List<OrderItem> extraArray = order.getExtras();
            if (extraArray != null) {
                for (int i = 0; i < extraArray.size(); i++) {
                    OrderItem item = extraArray.get(i);
                    JSONObject json = new JSONObject();
                    json.put("name", item.getName());
                    //json.put("amount",item.getAmount());
                    json.put("price", item.getPrice());
                    extras.put(json);
                }
                obj.put("extras", extras);
            }
            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
