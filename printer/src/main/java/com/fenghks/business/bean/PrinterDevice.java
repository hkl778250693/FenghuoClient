package com.fenghks.business.bean;

import android.hardware.usb.UsbDevice;

/**
 * 存储打印设备实体类
 * Created by fenghuo on 2017/9/8.
 */

public class PrinterDevice {
    private UsbDevice printer;  //USB打印机实例
    private String name;
    private int type;           //打印机类型 （0代表USB   1代表网络）
    private String ipAddress;   //网络打印机的IP地址

    public PrinterDevice() {
    }

    public PrinterDevice(UsbDevice printer, String name, int type, String ipAddress) {
        this.printer = printer;
        this.name = name;
        this.type = type;
        this.ipAddress = ipAddress;
    }

    public PrinterDevice(String name, int type, String ipAddress) {
        this.name = name;
        this.type = type;
        this.ipAddress = ipAddress;
    }

    public UsbDevice getPrinter() {
        return printer;
    }

    public void setPrinter(UsbDevice printer) {
        this.printer = printer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
