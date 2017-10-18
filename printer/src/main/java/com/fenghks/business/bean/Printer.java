package com.fenghks.business.bean;

import java.io.Serializable;

/**
 * Created by fenghuo on 2017/9/21.
 */

public class Printer implements Serializable {
    private int shopId;    //店铺ID
    private String ip;    //ip地址，usb打印机0
    private int port;     //端口，默认9100
    private String name;   //打印机别名
    private int businessNum;      //商家联打印数目，默认1
    private int kitchenNum;       //后厨联打印数目，默认0
    private int customerNum;      //客户联打印数目，默认0
    private int senderNum;        //配送联打印数目，默认0
    private int qrcode;           //是否打印二维码（1/0），默认1
    private int ispause;          //打印机是否停用，默认0不停用
    private int printerType;      //打印机类型，1网络、2 usb
    private int status;           //操作状态 （0：正常使用  1：已掉线 ）

    public Printer() {
    }

    /**
     * 打印机构造函数
     */
    public Printer(int shopId, String ip, int port, String printerName, int businessNum, int kitchenNum, int customerNum, int senderNum,
                   int qrcode, int ispause, int printerType, int status) {
        this.shopId = shopId;
        this.ip = ip;
        this.port = port;
        this.name = printerName;
        this.businessNum = businessNum;
        this.kitchenNum = kitchenNum;
        this.customerNum = customerNum;
        this.senderNum = senderNum;
        this.qrcode = qrcode;
        this.ispause = ispause;
        this.printerType = printerType;
        this.status = status;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBusinessNum() {
        return businessNum;
    }

    public void setBusinessNum(int businessNum) {
        this.businessNum = businessNum;
    }

    public int getKitchenNum() {
        return kitchenNum;
    }

    public void setKitchenNum(int kitchenNum) {
        this.kitchenNum = kitchenNum;
    }

    public int getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(int customerNum) {
        this.customerNum = customerNum;
    }

    public int getSenderNum() {
        return senderNum;
    }

    public void setSenderNum(int senderNum) {
        this.senderNum = senderNum;
    }

    public int getQrcode() {
        return qrcode;
    }

    public void setQrcode(int qrcode) {
        this.qrcode = qrcode;
    }

    public int getIspause() {
        return ispause;
    }

    public void setIspause(int ispause) {
        this.ispause = ispause;
    }

    public int getPrinterType() {
        return printerType;
    }

    public void setPrinterType(int printerType) {
        this.printerType = printerType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
