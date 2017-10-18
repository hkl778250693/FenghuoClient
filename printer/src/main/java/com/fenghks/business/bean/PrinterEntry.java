package com.fenghks.business.bean;

import android.provider.BaseColumns;

/**
 * Created by fenghuo on 2017/9/21.
 */

public class PrinterEntry implements BaseColumns{
    public static final String COLUMN_NAME_SHOP_ID = "shopId";
    public static final String COLUMN_NAME_IP = "ip";
    public static final String COLUMN_NAME_PORT = "port";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_BUSINESS_NUM = "businessNum";
    public static final String COLUMN_NAME_KITCHEN_NUM = "kitchenNum";
    public static final String COLUMN_NAME_CUSTOMER_NUM = "customerNum";
    public static final String COLUMN_NAME_SENDER_NUM = "senderNum";
    public static final String COLUMN_NAME_QRCODE = "qrcode";
    public static final String COLUMN_NAME_MANAGE_TOKEN = "manageToken";
    public static final String COLUMN_NAME_IS_PAUSE = "ispause";
    public static final String COLUMN_NAME_PRINTER_TYPE = "printerType";
    public static final String COLUMN_NAME_PRINTER_STATUS = "status";
}
