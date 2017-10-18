package com.fenghks.business.bean;

import java.io.Serializable;

/**
 * Created by Fei on 2017/2/6.
 */

public class OrderItem implements Serializable{

    private int id; //ID
    private int orderid; //订单ID
    private String name; //名称
    private int amount; //数量
    private double price; //单价
    private double totalprice; //单价
    private String note; //备注
    private String rawdata; //原始数据
    private int cartid; //几号篮子
    private int itemtype; //详情类型，0菜品，1配送费和折扣，2赠品

    public OrderItem() {
    }

    public OrderItem(int id, int orderid, String name, int amount, double price,
                     double totalprice, String note, String rawdata) {
        this.id = id;
        this.orderid = orderid;
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.totalprice = totalprice;
        this.note = note;
        this.rawdata = rawdata;
        if(this.totalprice == 0.0){
            this.totalprice = this.price * this.amount;
        }
    }

    public OrderItem(int id, int orderid, String name, int amount, double price, double totalprice,
                     String note, String rawdata, int cartid, int itemtype) {
        this.id = id;
        this.orderid = orderid;
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.totalprice = totalprice;
        this.note = note;
        this.rawdata = rawdata;
        this.cartid = cartid;
        this.itemtype = itemtype;
        if(this.totalprice == 0.0){
            this.totalprice = this.price * this.amount;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRawdata() {
        return rawdata;
    }

    public void setRawdata(String rawdata) {
        this.rawdata = rawdata;
    }

    public int getCartid() {
        return cartid;
    }

    public void setCartid(int cartid) {
        this.cartid = cartid;
    }

    public int getItemtype() {
        return itemtype;
    }

    public void setItemtype(int itemtype) {
        this.itemtype = itemtype;
    }
}
