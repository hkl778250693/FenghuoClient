package com.fenghks.business.bean;

/**
 * Created by Fei on 2017/4/23.
 */

public class Sender {
    private int senderid; //配送员id
    private int areaid; //区域ID
    private int groupid; //组ID
    private String name; //姓名
    private String phone; //电话
    private String headimg; //log图片
    private double lon; //经度
    private double lat; //纬度
    private String token; //商家编号
    private String registrationid; //门店地址

    public Sender(int senderid, int areaid, int groupid, String name, String phone, String headimg) {
        this.senderid = senderid;
        this.areaid = areaid;
        this.groupid = groupid;
        this.name = name;
        this.phone = phone;
        this.headimg = headimg;
    }

    public Sender(int senderid, int areaid, int groupid, String name, String phone, String headimg,
                  String token, String registrationid) {
        this.senderid = senderid;
        this.areaid = areaid;
        this.groupid = groupid;
        this.name = name;
        this.phone = phone;
        this.headimg = headimg;
        this.token = token;
        this.registrationid = registrationid;
    }

    public Sender(int senderid, int areaid, int groupid, String name, String phone, String headimg,
                  double lat,double lon,String token, String registrationid) {
        this.senderid = senderid;
        this.areaid = areaid;
        this.groupid = groupid;
        this.name = name;
        this.phone = phone;
        this.headimg = headimg;
        this.lat = lat;
        this.lon = lon;
        this.token = token;
        this.registrationid = registrationid;
    }

    public int getSenderid() {
        return senderid;
    }

    public void setSenderid(int senderid) {
        this.senderid = senderid;
    }

    public int getAreaid() {
        return areaid;
    }

    public void setAreaid(int areaid) {
        this.areaid = areaid;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public double[] getLoc() {
        return new double[]{this.lon,this.lat};
    }

    public void setLoc(double lon,double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRegistrationid() {
        return registrationid;
    }

    public void setRegistrationid(String registrationid) {
        this.registrationid = registrationid;
    }
}
