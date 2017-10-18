package com.fenghks.business.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.bean.Order;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.PollingUtils;
import com.fenghks.business.utils.SPUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 开启一个闹钟轮询服务，每隔五分钟向服务器请求一次订单信息
 * Created by fenghuo on 2017/8/31.
 */
public class ObtainOrderListService extends Service {
    private static final String TAG = "ObtainOrderListService";
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = ObtainOrderListService.this;
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher_round)
                //设置通知标题
                .setContentTitle("订单服务通知")
                //设置通知内容
                .setContentText("订单轮询服务，每五分钟请求一次！")
                //设置通知时间，默认为系统发出通知的时间，通常不用设置
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.FLAG_SHOW_LIGHTS);
        //通过builder.build()方法生成Notification对象,并发送通知,id=2
        notifyManager.notify(AppConstants.SERVICE_ORDER_ID, builder.build());
        Log.d(TAG, "开启订单轮询服务！");
        //获取今日订单ID 列表
        obtainTodayOrderIDs();
        //注册信鸽服务
        CommonUtil.registerPush(this.getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取今日订单ID 列表
     */
    private void obtainTodayOrderIDs() {
        Map<String, String> map = new HashMap<>();
        map.put("businessid", SPUtil.getInt(mContext, AppConstants.APP_SHOP_ID) + "");   //店铺id（不能传商家id）
        x.http().post(CommonUtil.genGetParam(mContext, AppConstants.OBTAIN_DEVICE_STATE, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "获取到今日订单ID列表为：" + result);
                JSONObject obj = ParseUtils.parseDataString(result);
                if (null != obj) {
                    if (obj.optInt("businessid") == SPUtil.getInt(mContext, AppConstants.APP_SHOP_ID)) {
                        if (!"[]".equals(obj.optString("ids"))) {  //今日有订单返回
                            List<Integer> newOrderIDs = JSON.parseArray(obj.optString("ids"), Integer.class);
                            Log.d(TAG, "获取到今日订单ID列表长度为：" + newOrderIDs.size());
                            List<Integer> oldOrderIds = JSON.parseArray(SPUtil.getString(mContext, AppConstants.PRINT_ORDER_ID_LIST), Integer.class);
                            Log.d(TAG, "获取到已打印的订单ID列表长度为：" + oldOrderIds.size());
                            //将获取的订单ID与本地已打印的订单ID对比，筛选出没有打印的订单
                            List<Integer> diffOrderIds = CommonUtil.getDiffrentElement(newOrderIDs, oldOrderIds);
                            if (diffOrderIds.size() > 0) {
                                //若存在没有打印的订单，则根据订单ID查询订单详情并打印
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < diffOrderIds.size(); i++) {
                                    sb.append(diffOrderIds.get(i)).append(",");
                                }
                                getOrderListByIDs(sb.substring(0,sb.length()-1));
                            }
                        }
                    }
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
     * 根据订单（可以是单个或多个ID）ID查询订单详情
     */
    public void getOrderListByIDs(String orderIds) {
        Map<String, String> map = new HashMap<>();
        map.put("businessid", SPUtil.getInt(this, AppConstants.APP_SHOP_ID) + "");     //店铺id（不能传商家id）
        map.put("orderIds", orderIds);     //订单号或列表，多个订单id用英文逗号,分隔
        x.http().post(CommonUtil.genGetParam(this, AppConstants.ORDER_LIST_BY_IDS, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Succeed！返回的结果为：" + result);
                JSONArray dataArray = ParseUtils.parseDataArray(result);
                if (null != dataArray) {
                    if (dataArray.length() == 0) {
                        Log.d(TAG, "获取到的订单为空！");
                        return;
                    }
                    for (int i = 0; i < dataArray.length(); i++) {
                        Order order = ParseUtils.parseOrder(dataArray.optJSONObject(i));
                        if (order.getId() > SPUtil.getInt(mContext, AppConstants.LAST_ORDER_ID)) {
                            SPUtil.putInt(mContext, AppConstants.LAST_ORDER_ID, order.getId());   //返回的订单ID大于上次保存的订单ID则保留
                            Log.d(TAG, "获取到的订单ID为：" + SPUtil.getInt(mContext, AppConstants.LAST_ORDER_ID));
                            //接收到一条订单，立即打印
                            Intent printService = new Intent(mContext, PrintOrderIntentService.class);
                            printService.putExtra("order", order);  //传递订单参数
                            startService(printService);
                        }
                    }
                } else {
                    Log.d(TAG, "获取订单为空！");
                    //获取到无效的订单，上报
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CommonUtil.networkErr(ex, isOnCallback);
                Log.d(TAG, "获取订单失败！错误信息为：" + ex);
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
                Log.d(TAG, "获取订单取消！");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "获取订单完成！");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
