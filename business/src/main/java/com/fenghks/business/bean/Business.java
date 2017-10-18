package com.fenghks.business.bean;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;

/**
 * Created by Fei on 2017/2/6.
 */

public class Business implements Serializable{

    private int id; //商家id
    private int areaid; //区域ID
    private int groupid; //组ID
    private int parentid; //总店ID（如果是总店ID为0）
    private String code; //商家编号
    private String name; //商家名称
    private String phone; //商家电话
    private String address; //门店地址
    private String logimg; //log图片
    private double lon; //经度
    private double lat; //纬度
    private String note; //备注
    private String loginuser; //登录帐号
    private String loginpwd; //登录密码
    private int isaccount; //是否独立核算
    private double surplus; //帐户余额
    private double orderincome; //订单总收入
    private double sendincome; //配送费总收入
    private int readytime; //平均备餐时间
    private DateTime startworktime; //上班时间
    private DateTime endworktime; //下班时间
    private int senderamount; //驻点配送员人数
    private int useamount; //运行人数（权重）
    private int isauto; //是否自动下单
    private int ischeck; //状态 0 未审核 1 已审核

    //美团
    private String ePoiId;
    //饿了么
    private int elemeid;

    public Business(int id, int areaid, int groupid, int parentid, String code, String name,
                    String phone, String address, String logimg, double lon, double lat,
                    double surplus, int readytime, String startworktime, String endworktime) {
        this.id = id;
        this.areaid = areaid;
        this.groupid = groupid;
        this.parentid = parentid;
        this.code = code;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.logimg = logimg;
        this.lon = lon;
        this.lat = lat;
        this.surplus = surplus;
        this.readytime = readytime;

        DateTimeFormatter format = DateTimeFormat.forPattern("HHmm");
        this.startworktime = DateTime.parse(startworktime,format);
        this.endworktime = DateTime.parse(endworktime,format);
        DateTime now = new DateTime();
        if(now.getHourOfDay() < 6 && this.startworktime.getHourOfDay() >= 6){
            this.startworktime = this.startworktime.minusHours(24);
        }
        if(now.getHourOfDay() >= 6 && this.endworktime.getHourOfDay() < 6){
            this.endworktime = this.endworktime.plusHours(24);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogimg() {
        return logimg;
    }

    public void setLogimg(String logimg) {
        this.logimg = logimg;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLoginuser() {
        return loginuser;
    }

    public void setLoginuser(String loginuser) {
        this.loginuser = loginuser;
    }

    public String getLoginpwd() {
        return loginpwd;
    }

    public void setLoginpwd(String loginpwd) {
        this.loginpwd = loginpwd;
    }

    public int getIsaccount() {
        return isaccount;
    }

    public void setIsaccount(int isaccount) {
        this.isaccount = isaccount;
    }

    public double getSurplus() {
        return surplus;
    }

    public void setSurplus(double surplus) {
        this.surplus = surplus;
    }

    public double getOrderincome() {
        return orderincome;
    }

    public void setOrderincome(double orderincome) {
        this.orderincome = orderincome;
    }

    public double getSendincome() {
        return sendincome;
    }

    public void setSendincome(double sendincome) {
        this.sendincome = sendincome;
    }

    public int getReadytime() {
        return readytime;
    }

    public void setReadytime(int readytime) {
        this.readytime = readytime;
    }

    public int getSenderamount() {
        return senderamount;
    }

    public void setSenderamount(int senderamount) {
        this.senderamount = senderamount;
    }

    public int getUseamount() {
        return useamount;
    }

    public void setUseamount(int useamount) {
        this.useamount = useamount;
    }

    public int getIsauto() {
        return isauto;
    }

    public void setIsauto(int isauto) {
        this.isauto = isauto;
    }

    public int getIscheck() {
        return ischeck;
    }

    public void setIscheck(int ischeck) {
        this.ischeck = ischeck;
    }

    public DateTime getStartworktime() {
        return startworktime;
    }

    public DateTime getEndworktime() {
        return endworktime;
    }

    public void setWorktime(String startworktime, String endworktime) {
        DateTimeFormatter format = DateTimeFormat.forPattern("HHmm");
        this.startworktime = DateTime.parse(startworktime,format);
        this.endworktime = DateTime.parse(endworktime,format);
        DateTime now = new DateTime();
        if(now.getHourOfDay() <= 6 && this.endworktime.isBefore(this.startworktime)){
            this.startworktime = this.startworktime.minusHours(24);
        }
        if(now.getHourOfDay() > 6 && this.endworktime.isBefore(this.startworktime)){
            this.endworktime = this.endworktime.plusHours(24);
        }
    }

    public String getePoiId() {
        return ePoiId;
    }

    public void setePoiId(String ePoiId) {
        this.ePoiId = ePoiId;
    }

    public int getElemeid() {
        return elemeid;
    }

    public void setElemeid(int elemeid) {
        this.elemeid = elemeid;
    }
}
