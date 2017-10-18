package com.fenghks.business;

import android.app.Application;

import com.fenghks.business.bean.Business;
import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.Printer;
import com.fenghks.business.bean.PrinterDevice;
import com.fenghks.business.bean.Sender;
import com.fenghks.business.print.USBPrinter;
import com.fenghks.business.utils.MyDBCallBack;
import com.fenghks.business.utils.MySqliteDBOpenhelper;
import com.fenghks.business.utils.SPUtil;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fei on 2017/1/18.
 */

public class FenghuoApplication extends Application {
    public static boolean needLogin = true;
    public static Business business = null;
    public static int shopID;
    public static List<Business> shops = new ArrayList<>();
    public static List<Sender> senders = new ArrayList<>();
    public static int currentShop = 0;
    public static String token = null;   //设备在后台服务器注册返回的token
    public static String city = null;
    public static List<Order> orders = new ArrayList<>();
    public static int lastOrderID = -1;  //上次获取的最新的ID，默认-1
    public static String xg_token = null;   //设备注册信鸽服务返回的token
    public static String app_xg_token = null;   //APP传过来的信鸽token
    public static List<PrinterDevice> printerDeviceList = new ArrayList<>();   //存储打印机的列表（包含USB和网络）
    public static List<String> myPrintIDList = new ArrayList<>();       //打印机ID集合,用于判读打印机是否存在打印机集合中
    public static String cloudDeviceMacAddress = null;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        MySqliteDBOpenhelper.init(this.getApplicationContext(), new MyDBCallBack());  //初始化本地数据库
        this.token = SPUtil.getString(this, "token");
    }
}
