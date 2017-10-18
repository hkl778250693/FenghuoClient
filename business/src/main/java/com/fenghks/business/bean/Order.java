package com.fenghks.business.bean;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Fei on 2017/2/4.
 */

public class Order implements Serializable{
    private int id; //订单id
    private int areaid; //区域id
    private int businessid; //商家id
    private String businessname; //商家名称
    private int senderid; //配送员id
    private String firecode; //疯火单号
    private String ordercode; //原订单号
    private String clientfrom; //订单来源 1饿了么 2美团 3百度 4口碑 5其它
    private String customername; //客户姓名
    private String customerphone; //客户电话
    private String sendaddress; //送餐地址
    private double totalprice; //订单总价格
    private double originalfee; //原配送费用
    private double sendfee; //配送费用
    private double lon; //送餐坐标经度
    private double lat; //送餐坐标纬度
    private int state; //配送状态0未配送 1 已配单 2配送中 3 已送达 4 异常 5 退单
    private int distance; //订单距离
    private int timeconsuming; //预计耗时
    private int urgenum = 0; //催单次数
    private int ismerge; //是否并单
    private String createtime; //订单创建时间
    private String sendtime; //派送时间
    private String starttime; //订单下单时间
    private String gettime; //取餐时间
    private String arrivedtime; //送餐到达时间
    private String validatecode; //送餐验证码
    private String getcode; //六位取餐码
    private int timelimit; //送餐时间限制
    private String qrcode; //二维码链接
    private int isdelete = 0; //是否删除
    private String note; //备注
    private String exceptionresult; //异常处理结果
    private String tag; //
    private int mileage; //里程段
    private int createtype; //订单类型：打印、手动下单
    private int isdoexception = 0; //异常订单是否处理
    private int issenderover = 0; //是否配送员完结
    private int orderType = 0; //订单类型，0即时送达，1预订单

    //private List<OrderItem> items;
    private Map<Integer,List<OrderItem>> items;
    private List<OrderItem> extras;

    public Order() {
    }

    public Order(int id, int areaid, int businessid, String firecode, String ordercode, String clientfrom,
                 String customername, String customerphone, String sendaddress, double totalprice,
                 int state, String createtime, String sendtime, String validatecode, String getcode) {
        this.id = id;
        this.areaid = areaid;
        this.businessid = businessid;
        this.firecode = firecode;
        this.ordercode = ordercode;
        this.clientfrom = clientfrom;
        this.customername = customername;
        this.customerphone = customerphone;
        this.sendaddress = sendaddress;
        this.totalprice = totalprice;
        this.state = state;
        this.createtime = createtime;
        this.sendtime = sendtime;
        this.validatecode = validatecode;
        this.getcode = getcode;
        DateTime createTime = new DateTime(createtime);
        DateTime sendTime = new DateTime(sendtime);
        this.orderType = createTime.plusMinutes(30).isAfter(sendTime) ? 0 : 1 ;
        if(this.note == null || "null".equals(this.note)) this.note = "";
    }

    public Order(int id, int areaid, int businessid, int senderid, String firecode, String ordercode,
                 String clientfrom, String customername, String customerphone, String sendaddress,
                 double totalprice, double sendfee, double lon, double lat, int state, int distance,
                 String createtime, String sendtime, String validatecode, String getcode, int mileage, int createtype) {
        this.id = id;
        this.areaid = areaid;
        this.businessid = businessid;
        this.senderid = senderid;
        this.firecode = firecode;
        this.ordercode = ordercode;
        this.clientfrom = clientfrom;
        this.customername = customername;
        this.customerphone = customerphone;
        this.sendaddress = sendaddress;
        this.totalprice = totalprice;
        this.sendfee = sendfee;
        this.lon = lon;
        this.lat = lat;
        this.state = state;
        this.distance = distance;
        this.createtime = createtime;
        this.sendtime = sendtime;
        this.validatecode = validatecode;
        this.getcode = getcode;
        this.mileage = mileage;
        this.createtype = createtype;
        DateTime createTime = new DateTime(createtime);
        DateTime sendTime = new DateTime(sendtime);
        this.orderType = createTime.plusMinutes(30).isAfter(sendTime) ? 0 : 1 ;
        if(this.note == null || "null".equals(this.note)) this.note = "";
    }

    public Order(int id, int areaid, int businessid, int senderid, String firecode, String ordercode,
                 String clientfrom, String customername, String customerphone, String sendaddress,
                 double totalprice, double originalfee, double sendfee, double lon, double lat,
                 int state, int distance, int timeconsuming, int urgenum, int ismerge,
                 String createtime, String sendtime, String starttime, String gettime, String arrivedtime,
                 String validatecode, String getcode, int timelimit, String qrcode, int isdelete,
                 String note, String exceptionresult, String tag, int mileage, int createtype,
                 int isdoexception, int issenderover, int orderType) {
        this.id = id;
        this.areaid = areaid;
        this.businessid = businessid;
        this.senderid = senderid;
        this.firecode = firecode;
        this.ordercode = ordercode;
        this.clientfrom = clientfrom;
        this.customername = customername;
        this.customerphone = customerphone;
        this.sendaddress = sendaddress;
        this.totalprice = totalprice;
        this.originalfee = originalfee;
        this.sendfee = sendfee;
        this.lon = lon;
        this.lat = lat;
        this.state = state;
        this.distance = distance;
        this.timeconsuming = timeconsuming;
        this.urgenum = urgenum;
        this.ismerge = ismerge;
        this.createtime = createtime.replace("Z","");
        this.sendtime = sendtime.replace("Z","");
        this.starttime = starttime.replace("Z","");
        this.gettime = gettime.replace("Z","");
        this.arrivedtime = arrivedtime.replace("Z","");
        this.validatecode = validatecode;
        this.getcode = getcode;
        this.timelimit = timelimit;
        this.qrcode = qrcode;
        this.isdelete = isdelete;
        this.note = note;
        this.exceptionresult = exceptionresult;
        this.tag = tag;
        this.mileage = mileage;
        this.createtype = createtype;
        this.isdoexception = isdoexception;
        this.issenderover = issenderover;
        this.orderType = orderType;
        if(this.note == null || "null".equals(this.note)) this.note = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirecode() {
        return firecode;
    }

    public void setFirecode(String firecode) {
        this.firecode = firecode;
    }

    public String getOrdercode() {
        return ordercode;
    }

    public void setOrdercode(String ordercode) {
        this.ordercode = ordercode;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime.replace("Z","");
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime.replace("Z","");
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime.replace("Z","");
    }

    public String getGettime() {
        return gettime;
    }

    public void setGettime(String gettime) {
        this.gettime = gettime.replace("Z","");
    }

    public String getArrivedtime() {
        return arrivedtime;
    }

    public void setArrivedtime(String arrivedtime) {
        this.arrivedtime = arrivedtime.replace("Z","");
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getClientfrom() {
        return clientfrom;
    }

    public void setClientfrom(String clientfrom) {
        this.clientfrom = clientfrom;
    }

    public int getAreaid() {
        return areaid;
    }

    public void setAreaid(int areaid) {
        this.areaid = areaid;
    }

    public int getBusinessid() {
        return businessid;
    }

    public void setBusinessid(int businessid) {
        this.businessid = businessid;
    }

    public int getSenderid() {
        return senderid;
    }

    public void setSenderid(int senderid) {
        this.senderid = senderid;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getCustomerphone() {
        return customerphone;
    }

    public void setCustomerphone(String customerphone) {
        this.customerphone = customerphone;
    }

    public String getSendaddress() {
        return sendaddress;
    }

    public void setSendaddress(String sendaddress) {
        this.sendaddress = sendaddress;
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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTimeconsuming() {
        return timeconsuming;
    }

    public void setTimeconsuming(int timeconsuming) {
        this.timeconsuming = timeconsuming;
    }

    public String getValidatecode() {
        return validatecode;
    }

    public void setValidatecode(String validatecode) {
        this.validatecode = validatecode;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public double getSendfee() {
        return sendfee;
    }

    public void setSendfee(double sendfee) {
        this.sendfee = sendfee;
    }

    public double getOriginalfee() {
        return originalfee;
    }

    public void setOriginalfee(double originalfee) {
        this.originalfee = originalfee;
    }

    public int getUrgenum() {
        return urgenum;
    }

    public void setUrgenum(int urgenum) {
        this.urgenum = urgenum;
    }

    public int getIsmerge() {
        return ismerge;
    }

    public void setIsmerge(int ismerge) {
        this.ismerge = ismerge;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getGetcode() {
        return getcode;
    }

    public void setGetcode(String getcode) {
        this.getcode = getcode;
    }

    public int getTimelimit() {
        return timelimit;
    }

    public void setTimelimit(int timelimit) {
        this.timelimit = timelimit;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public int getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(int isdelete) {
        this.isdelete = isdelete;
    }

    public String getExceptionresult() {
        return exceptionresult;
    }

    public void setExceptionresult(String exceptionresult) {
        this.exceptionresult = exceptionresult;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getCreatetype() {
        return createtype;
    }

    public void setCreatetype(int createtype) {
        this.createtype = createtype;
    }

    public int getIsdoexception() {
        return isdoexception;
    }

    public void setIsdoexception(int isdoexception) {
        this.isdoexception = isdoexception;
    }

    public int getIssenderover() {
        return issenderover;
    }

    public void setIssenderover(int issenderover) {
        this.issenderover = issenderover;
    }

    public String getBusinessname() {
        return businessname;
    }

    public void setBusinessname(String businessname) {
        this.businessname = businessname;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public Map<Integer,List<OrderItem>> getItems() {
        //if(items == null) items = new ArrayList<>();
        return items;
    }

    public void setItems(Map<Integer,List<OrderItem>> items) {
        this.items = items;
    }

    public List<OrderItem> getExtras() {
        return extras;
    }

    public void setExtras(List<OrderItem> extras) {
        this.extras = extras;
    }
}
