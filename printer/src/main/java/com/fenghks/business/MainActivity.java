package com.fenghks.business;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.fenghks.business.bean.PrinterDevice;
import com.fenghks.business.print.USBPrinter;
import com.fenghks.business.receiver.FrontRegisterService;
import com.fenghks.business.receiver.ObtainOrderListService;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.NetworkUtil;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.PollingUtils;
import com.fenghks.business.utils.SPUtil;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context mContext;
    private boolean flag = true;
    private List<PrinterDevice> printerDeviceList = new ArrayList<>();   //存储打印机的列表（包含USB和网络）


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        mContext = getApplicationContext();
        //注册信鸽服务
        CommonUtil.registerPush(mContext);
        //初始化USB打印机监听广播
        USBPrinter.initUSBPrinter(this);
        //检测本地是否缓存打印机列表
        if ("".equals(SPUtil.getString(mContext, AppConstants.PRINTER_LIST))) {
            SPUtil.putString(mContext, AppConstants.PRINTER_LIST, JSON.toJSONString(printerDeviceList));
            Log.d(TAG, "添加默认打印机列表到sp，列表长度为：" + printerDeviceList.size());
        }
        if (SPUtil.getInt(mContext, AppConstants.LAST_ORDER_ID) == -1) {
            SPUtil.putInt(mContext, AppConstants.LAST_ORDER_ID, -1);  //添加默认最新订单ID
        }
        if (!SPUtil.getBoolean(mContext, AppConstants.IS_INITIALIZE, false)) {
            openUDPBroadcast(); //开启与商家APP的UDP监听广播
        } else {
            Log.d(TAG, "设备已注册");
        }
        //开启订单定时请求服务  轮询服务
        PollingUtils.startPollingService(mContext, 2 * 60, ObtainOrderListService.class);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (flag) {
                SPUtil.putBoolean(mContext, AppConstants.ASK_FOR_AUTO_RUN, false);
                if (SPUtil.getBoolean(mContext, AppConstants.ASK_FOR_AUTO_RUN, true)) {
                    CommonUtil.askAutoStartPermission(mContext);
                    flag = false;
                }
            }
        }
    }


    /**
     * 检测店铺初始化状态以及激活状态
     */
    private void checkShopState() {
        Map<String, String> map = new HashMap<>();
        map.put("businessid", SPUtil.getInt(mContext, AppConstants.APP_SHOP_ID) + "");   //店铺id（不能传商家id）
        map.put("requestId", UUID.randomUUID().toString());
        x.http().post(CommonUtil.genGetParam(mContext, AppConstants.OBTAIN_DEVICE_STATE, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "当前店铺的状态为：" + result);
                JSONObject obj = ParseUtils.parseDataString(result);
                if (null != obj) {
                    if (obj.optInt("businessid") == SPUtil.getInt(mContext, AppConstants.APP_SHOP_ID)) {
                        if (obj.optInt("isbind") == 1) {   //设备（绑定）注册成功
                            SPUtil.putBoolean(mContext, "shop" + SPUtil.getInt(mContext, AppConstants.APP_SHOP_ID), AppConstants.IS_INITIALIZE, true);
                            Log.d(TAG, "设备已注册");
                        } else {
                            SPUtil.putBoolean(mContext, "shop" + SPUtil.getInt(mContext, AppConstants.APP_SHOP_ID), AppConstants.IS_INITIALIZE, false);
                            openUDPBroadcast(); //开启与商家APP的UDP监听广播
                            Log.d(TAG, "设备未注册");
                        }
                    }
                }else {
                    SPUtil.putBoolean(mContext, "shop" + SPUtil.getInt(mContext, AppConstants.APP_SHOP_ID), AppConstants.IS_INITIALIZE, false);
                    openUDPBroadcast(); //开启与商家APP的UDP监听广播
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 开启与商家APP的UDP监听广播
     */
    private void openUDPBroadcast() {
        FenghuoApplication.cloudDeviceMacAddress = NetworkUtil.getMacAddress();//获取云接单设备的MAC地址
        Log.d(TAG, "设备的MAC地址为：" + FenghuoApplication.cloudDeviceMacAddress);
        SPUtil.putString(this, AppConstants.DEVICE_MAC_ADDRESS, NetworkUtil.getMacAddress());
        if (null != SPUtil.getString(this, AppConstants.DEVICE_MAC_ADDRESS) && !"02:00:00:00:00:02".equals(FenghuoApplication.cloudDeviceMacAddress)) {
            Intent service = new Intent(this, FrontRegisterService.class);
            this.startService(service);
            Log.d(TAG, "已开启与商家APP的连接服务监听");
            Toast.makeText(this, "已开启与商家APP的连接服务监听！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "获取盒子MAC地址失败，无法与商家APP对接", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        USBPrinter.destroyPrinter();
    }
}
