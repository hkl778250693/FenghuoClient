package com.fenghks.business;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import com.fenghks.business.bean.Business;
import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.Printer;
import com.fenghks.business.bean.Sender;
import com.fenghks.business.utils.MyDBCallBack;
import com.fenghks.business.utils.MySqliteDBOpenhelper;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.SPUtil;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fei on 2017/1/18.
 */

public class FenghuoApplication extends Application {
    public static boolean needLogin = true;
    public static Business business = null;
    public static List<Business> shops = new ArrayList<>();
    public static List<Sender> senders = new ArrayList<>();
    public static int currentShop = 0;
    public static int currentShopID = 0; //当前进入的店铺ID
    public static String token = null;
    public static String city = null;
    public static List<Order> orders = null;
    public static String xg_token = null;
    public static List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    public static List<Printer> printerList = new ArrayList<>();
    public static ParseUtils parseUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        MySqliteDBOpenhelper.init(this.getApplicationContext(), new MyDBCallBack());  //初始化本地数据库
        this.token  = SPUtil.getString(this,"token");
    }
}
