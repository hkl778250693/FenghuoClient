package com.fenghks.business;

import android.app.Application;


import com.fenghks.business.bean.Business;
import com.fenghks.business.bean.Sender;
import com.fenghks.business.utils.MyDBCallBack;
import com.fenghks.business.utils.MySqliteDBOpenhelper;
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
    public static String token = null;   //设备在后台服务器注册返回的token
    public static String city = null;
    public static String xg_token = null;   //设备注册信鸽服务返回的token

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        MySqliteDBOpenhelper.init(this.getApplicationContext(), new MyDBCallBack());  //初始化本地数据库
        this.token = SPUtil.getString(this, "token");
    }
}
