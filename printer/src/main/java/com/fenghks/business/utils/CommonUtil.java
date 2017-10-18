package com.fenghks.business.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.MainActivity;
import com.fenghks.business.R;
import com.fenghks.business.bean.Printer;
import com.fenghks.business.bean.PrinterEntry;
import com.fenghks.business.receiver.ObtainOrderListService;
import com.fenghks.business.receiver.PrintOrderIntentService;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommonUtil {
    private static final String TAG = "CommonUtil";
    public static final String REQUEST_POST = "post";
    public static final int ROWS = 10;

    //private final static String SP_NAME="USER_INFO";

    //相机图片路径
    public static String CAMERA_PATH = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
    //附件路径
    public static String GLOBAL_PATH = Environment.getExternalStorageDirectory() + "/fenghuo";
    //图片路径
    public static String IMAGE_PATH = GLOBAL_PATH + "/images";
    //相册路径
    public static final String DCIM = GLOBAL_PATH + "/SaveImages/";

    public static final int SHARE_ACTION = 101;

    public static final String QR_BASE = "http://weixin.qq.com/r/gUNLU2PEez4ErScw9xaT?ordid=";


    //添加推送设备
    public static final String ADD_PUSH_DEVICE_URL = "/v1/business/addPushDevice";
    //删除推送设备
    public static final String REMOVE_PUSH_DEVICE_URL = "/v1/business/removePushDevice";
    //注册设备
    public static final String REGISTER_URL = "/v1/business/register";
    //上传图片
    public static final String UPLOAD_PIC_URL = "/v1/business/upload";
    //获取版本更新
    public static final String GET_UPDATE_URL = "/v1/business/checkAPPVersion";
    //获取推送
    //public static final String GET_MESSAGE_URL = "/v1/business/n";


    public static String app_version;
    public static String app_url;

    public static RequestParams genGetParam(Context context, String url, Map<String, String> map) {
        RequestParams params = new RequestParams(AppConstants.BASE_URL + url);
        if (null != map) {
            if (!"".equals(SPUtil.getString(context, AppConstants.DEVICE_TOKEN))) {
                params.addParameter("token", SPUtil.getString(context, AppConstants.DEVICE_TOKEN));
            }
            /*if(0 < FenghuoApplication.business.getId()){
                params.addParameter("businessid",FenghuoApplication.business.getId());
			}*/
            params.addParameter("deviceType", "1");
            params.addParameter("type", "cloudpushdevice");
            for (String key : map.keySet()) {
                params.addParameter(key, map.get(key));
            }
        }
        return params;
    }

    public static String genGetUrl(String baseUrl, Map<String, String> map) {
        String url = baseUrl + "?";
        for (String key : map.keySet()) {
            url += key + "=" + map.get(key) + "&";
        }
        url = url.substring(0, url.length() - 1);
        System.out.println(url);
        return url;
    }

    /**
     * 是否第一次进入
     **/
    public static void setIsFirst(Context context, boolean isFirst) {
        SPUtil.putBoolean(context, "isFirst", isFirst);
    }

    /**
     * 是否第一次进入
     *
     * @return
     */
    public static boolean getIsFirst(Context context) {
        return SPUtil.getBoolean(context, "isFirst", true);
    }

    public static void handleErr(Context context, String json) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
            switch (obj.optInt("status")) {
                case AppConstants.ERR_INVALID_TOKEN:
                    //autoLogin(context,AppConstants.FROM_ACTIVITY);
                    break;
            }
            Toast.makeText(context, obj.optString("msg"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册设备
     *
     * @param context
     */
    public static void registerDevice(final Context context) {
        String appNumber = getDeviceId(context);
        SPUtil.putString(context, "appNumber", appNumber);
        // 传参数
        Map<String, String> map = new HashMap<String, String>();
        map.put("businessid", "" + FenghuoApplication.business.getId());
        map.put("pushToken", "");//Android传空
        map.put("appType", "1");//Android传1，ios传2
        map.put("appNumber", appNumber);//Android传设备号（处理过的id号）,ios传生成的ID号
        x.http().post(CommonUtil.genGetParam(context, CommonUtil.REGISTER_URL, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                setIsFirst(context, false);
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

    public static String getPrintSettings(Context context) {
        String print_setting = SPUtil.getString(context, AppConstants.SP_SETTINGS, "pref_print_business", "0")
                + "," + SPUtil.getString(context, AppConstants.SP_SETTINGS, "pref_print_kitchen", "0")
                + "," + SPUtil.getString(context, AppConstants.SP_SETTINGS, "pref_print_customer", "0")
                + "," + SPUtil.getString(context, AppConstants.SP_SETTINGS, "pref_print_sender", "0")
                + "," + (SPUtil.getBoolean(context, AppConstants.SP_SETTINGS, "pref_print_qr", false) ? "1" : "0");
        return print_setting;
    }

    /**
     * 注册云打印设备（盒子）
     */
    public static boolean registerCloudPrintDevice(String params, final Context context) {
        final boolean[] result = {false};
        String[] paramArray = params.split(",");
        if (paramArray.length < 2) {
            return result[0];
        }
        Map<String, String> map = new HashMap<>();
        map.put("appCode", "fenghuo");
        map.put("appType", "cloudpushdevice");     //app类型
        map.put("businessid", paramArray[0]);     //店铺id（不能传商家id）
        Log.d(TAG, "businessid为：" + paramArray[0]);
        map.put("manageToken", paramArray[1]);    //商家APP注册信鸽返回的xg_token
        Log.d(TAG, "manageToken为：" + paramArray[1]);
        map.put("device_token", SPUtil.getString(context, AppConstants.DEVICE_XG_TOKEN));   //盒子注册信鸽返回的xg_token
        Log.d(TAG, "device_token为：" + SPUtil.getString(context, AppConstants.DEVICE_XG_TOKEN));
        map.put("macaddress", SPUtil.getString(context, AppConstants.DEVICE_MAC_ADDRESS));       //设备的mac地址
        FenghuoApplication.shopID = Integer.parseInt(paramArray[0]);
        FenghuoApplication.app_xg_token = paramArray[1];
        SPUtil.putInt(context, AppConstants.APP_SHOP_ID, Integer.parseInt(paramArray[0]));
        SPUtil.putString(context, AppConstants.APP_XG_TOKEN, paramArray[1]);
        x.http().post(CommonUtil.genGetParam(context, AppConstants.REGISTER_BOX, map), new Callback.CommonCallback<String>() {
            boolean succeed = false;
            boolean hasError = false;

            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Succeed！返回的结果为：" + result);
                JSONObject obj = ParseUtils.parseDataString(result);
                if (null != obj) {
                    Log.d(TAG, "盒子注册后返回的token为：" + obj.optString("token", ""));
                    FenghuoApplication.token = obj.optString("token", "");
                    SPUtil.putString(context, AppConstants.DEVICE_TOKEN, obj.optString("token", ""));
                    SPUtil.putBoolean(context, AppConstants.IS_INITIALIZE, true);
                    succeed = true;
                } else {
                    Log.d(TAG, "没有返回token");
                    succeed = false;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CommonUtil.networkErr(ex, isOnCallback);
                hasError = true;
                SPUtil.putBoolean(context, AppConstants.IS_INITIALIZE, false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                succeed = false;
                SPUtil.putBoolean(context, AppConstants.IS_INITIALIZE, false);
            }

            @Override
            public void onFinished() {
                if (!hasError && succeed) {
                    succeed = true;
                } else {
                    succeed = false;
                }
                result[0] = succeed;
            }
        });
        return result[0];
    }

    /**
     * 反注册推送服务
     */
    public static void unregisterPushService(final Context context) {
        if (FenghuoApplication.shops == null || FenghuoApplication.shops.size() == 0
                || StringUtil.isEmpty(SPUtil.getString(context, AppConstants.DEVICE_XG_TOKEN))) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("device_token", SPUtil.getString(context, AppConstants.DEVICE_XG_TOKEN));
        //map.put("deviceType","1");
        String businessids = "";
        for (int i = 0; i < FenghuoApplication.shops.size(); i++) {
            businessids += FenghuoApplication.shops.get(i).getId() + ",";
        }
        map.put("businessid", businessids.substring(0, businessids.length() - 1));
        x.http().post(CommonUtil.genGetParam(context, CommonUtil.REMOVE_PUSH_DEVICE_URL, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                JSONArray obj = ParseUtils.parseDataArray(result);
                if (obj != null) {
                    System.out.println("cancel push service successed!");
                } else {
                    //CommonUtil.handleErr(context, result);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CommonUtil.networkErr(ex, isOnCallback);
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
     * 注册信鸽推送服务，获取token，并保存本地
     */
    public static void registerPush(final Context context) {
        // 开启logcat输出，方便debug，发布时请关闭
        //XGPushConfig.enableDebug(this, true);
        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
        // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
        // 具体可参考详细的开发指南
        // 传递的参数为ApplicationContext
        //XGPushManager.registerPush(mContext);
        XGPushManager.registerPush(context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object obj, int flag) {
                Log.d(TAG, "XGPush注册成功,Token值为：" + obj);
                FenghuoApplication.xg_token = obj.toString();
                SPUtil.putString(context, AppConstants.DEVICE_XG_TOKEN, obj.toString());
            }

            @Override
            public void onFail(Object obj, int errCode, String msg) {
                Log.d(TAG, "TPush注册失败,错误码为：" + errCode + ",错误信息：" + msg);
            }
        });
    }

    public static String getDeviceId(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    /**
     * 判断程序是否在前台运行
     *
     * @param context
     * @return
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static void networkErr(Throwable ex, boolean isOnCallback) {
        if (ex instanceof HttpException) { // 网络错误
            System.out.println(ex.getMessage());
            //Toast.makeText(x.app(), R.string.network_error, Toast.LENGTH_LONG).show();
        } else { // 其他错误
            //Toast.makeText(x.app(), R.string.other_error, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 查询保存到本地的打印机列表
     */
    public static List<Printer> getLocalPrinters(Context context) {
        List<Printer> printerList = new ArrayList<>();
        printerList.clear();
        printerList = MySqliteDBOpenhelper.queryList(MyDBCallBack.TABLE_NAME_PRINTER, "select * from " +
                MyDBCallBack.TABLE_NAME_PRINTER + " where " + PrinterEntry.COLUMN_NAME_SHOP_ID + "=?" + " and " + PrinterEntry.COLUMN_NAME_PRINTER_STATUS + "=?", new String[]{String.valueOf(SPUtil.getInt(context, AppConstants.APP_SHOP_ID)), String.valueOf(AppConstants.STATUS_NORMAL)});
        if (printerList == null) {
            return null;
        } else {
            Log.d(TAG, "打印机列表长度为：" + printerList.size());
        }
        return printerList;
    }

    /**
     * 查询打印机设置信息
     *
     * @return
     */
    public static <T> T getSingleEntity(String name) {
        return MySqliteDBOpenhelper.querySingle(MyDBCallBack.TABLE_NAME_PRINTER, "select * from " +
                MyDBCallBack.TABLE_NAME_PRINTER + " where " + PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{name});
    }

    /**
     * 以下方法是关于云接单设备 消息上报相关
     */
    //上报订单获取结果
    public static void pushOrderResult(Context context, int orderId, int result) {
        Map<String, String> map = new HashMap<>();
        map.put("manageToken", "nulltoken");
        map.put("businessid", SPUtil.getInt(context, AppConstants.APP_SHOP_ID) + "");   //店铺id（不能传商家id）
        map.put("msgtype", "order");  //消息类型
        JSONObject obj = new JSONObject();
        try {
            obj.put("orderid", orderId);   //订单ID
            obj.put("result", result);   //1：成功   0：失败
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("msg", obj.toString());   //消息体，json对象
        x.http().post(CommonUtil.genGetParam(context, AppConstants.MSG_REPORT, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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


    //上报收到激活消息
    public static void pushGotActivateInfo(Context context, String managetoken, String requestId, int isactivate, int result) {
        Map<String, String> map = new HashMap<>();
        map.put("manageToken", managetoken);
        map.put("businessid", SPUtil.getInt(context, AppConstants.APP_SHOP_ID) + "");   //店铺id（不能传商家id）
        map.put("msgtype", "activate");  //消息类型
        JSONObject obj = new JSONObject();
        try {
            obj.put("requestId", requestId);
            obj.put("isactive", isactivate);
            obj.put("result", result);   //1：成功   0：失败
            obj.put("note", "收到激活成功消息！");   //描述信息
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("msg", obj.toString());   //消息体，json对象
        x.http().post(CommonUtil.genGetParam(context, AppConstants.MSG_REPORT, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "上报结果为：" + result);
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

    //上报打印机列表
    public static void pushPrinters(Context context, String managetoken, List<Printer> printerList, String requestId, int result) {
        Map<String, String> map = new HashMap<>();
        map.put("manageToken", managetoken);
        Log.d(TAG, "上报的商家APP token为：" + managetoken);
        map.put("businessid", SPUtil.getInt(context, AppConstants.APP_SHOP_ID) + "");   //店铺id（不能传商家id）
        map.put("msgtype", "getprinters");  //消息类型
        map.put("msg", jsonArrayPackagePrinter(printerList, requestId, result).toString());   //消息体，json对象
        x.http().post(CommonUtil.genGetParam(context, AppConstants.MSG_REPORT, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "上报结果为：" + result);
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
     * 将打印机列表封装成json对象
     */
    public static JSONObject jsonArrayPackagePrinter(List<Printer> lists, String requestId, int result) {
        JSONObject obj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            obj.put("requestId", requestId);
            obj.put("result", result);   //1：成功   0：失败
            for (int i = 0; i < lists.size(); i++) {
                Printer printer = lists.get(i);
                JSONObject object = new JSONObject();
                object.put("ip", printer.getIp());
                object.put("port", printer.getPort());
                object.put("name", printer.getName());
                object.put("businessNum", printer.getBusinessNum());
                object.put("kitchenNum", printer.getKitchenNum());
                object.put("customerNum", printer.getCustomerNum());
                object.put("senderNum", printer.getSenderNum());
                object.put("qrcode", printer.getQrcode());
                object.put("ispause", printer.getIspause());
                object.put("printertype", printer.getPrinterType());
                jsonArray.put(object);
            }
            obj.put("printers", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    //上报打印机设置结果  或者添加结果
    public static void pushSettingOrAddResult(Context context, String managetoken, String name, int type, Printer printer, String requestId, int result, String note) {
        Map<String, String> map = new HashMap<>();
        map.put("manageToken", managetoken);
        map.put("businessid", SPUtil.getInt(context, AppConstants.APP_SHOP_ID) + "");   //店铺id（不能传商家id）
        if (type == 0) {  //0代表设置打印机 消息类型
            map.put("msgtype", "setprinter");
        } else if (type == 1) {  //1代表添加打印机 消息类型
            map.put("msgtype", "addprinter");
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("requestId", requestId);
            obj.put("result", result);   //1：成功   0：失败
            obj.put("name", name);
            obj.put("note", note);   //描述信息
            JSONObject object = new JSONObject();
            object.put("ip", printer.getIp());
            object.put("port", printer.getPort());
            object.put("name", printer.getName());
            object.put("businessNum", printer.getBusinessNum());
            object.put("kitchenNum", printer.getKitchenNum());
            object.put("customerNum", printer.getCustomerNum());
            object.put("senderNum", printer.getSenderNum());
            object.put("qrcode", printer.getQrcode());
            object.put("ispause", printer.getIspause());
            object.put("printertype", printer.getPrinterType());

            obj.put("printer", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("msg", obj.toString());   //消息体，json对象
        x.http().post(CommonUtil.genGetParam(context, AppConstants.MSG_REPORT, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "上报结果为：" + result);
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

    //上报打印机删除结果
    public static void pushDeleteResult(Context context, String managetoken, String name, String requestId, int result, String note) {
        Map<String, String> map = new HashMap<>();
        map.put("manageToken", managetoken);
        map.put("businessid", SPUtil.getInt(context, AppConstants.APP_SHOP_ID) + "");   //店铺id（不能传商家id）
        map.put("msgtype", "delprinter");  //消息类型
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("requestId", requestId);
            obj.put("result", result);   //1：成功   0：失败
            obj.put("note", note);   //描述信息
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("msg", obj.toString());   //消息体，json对象
        x.http().post(CommonUtil.genGetParam(context, AppConstants.MSG_REPORT, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "上报结果为：" + result);
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


    //上报打印订单失败信息
    public static void pushPrintFailure(Context context, String managetoken, int orderId, String orderSn, String client, String note) {
        Map<String, String> map = new HashMap<>();
        map.put("manageToken", managetoken);
        map.put("businessid", SPUtil.getInt(context, AppConstants.APP_SHOP_ID) + "");   //店铺id（不能传商家id）
        map.put("msgtype", "orderException");  //消息类型
        JSONObject obj = new JSONObject();
        try {
            obj.put("orderId", orderId);
            obj.put("DaySn", orderSn);  //平台的订单流水号
            obj.put("clientfrom", client);   //订单来源
            obj.put("note", note);   //描述信息
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("msg", obj.toString());   //消息体，json对象
        x.http().post(CommonUtil.genGetParam(context, AppConstants.MSG_REPORT, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "上报结果为：" + result);
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

    // 保存打印机设置
    public static boolean savePrinterSetting(Printer printer, String name) {
        return MySqliteDBOpenhelper.update(MyDBCallBack.TABLE_NAME_PRINTER, printer, PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{name});
    }

    //添加网络打印机
    public static boolean addNetWorkPrinter(Printer printer) {
        return MySqliteDBOpenhelper.insert(MyDBCallBack.TABLE_NAME_PRINTER, printer);
    }

    //删除网络打印机
    public static boolean deleteNetPrinter(String name) {
        return MySqliteDBOpenhelper.delete(MyDBCallBack.TABLE_NAME_PRINTER, PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{name});
    }

    //获取某个打印机的配置信息
    public static String getPrinterSetting(String name) {
        String result = "1,0,0,0,1";
        StringBuilder sb = new StringBuilder();
        /** 通过名称查询打印机设置信息 */
        Printer printer = MySqliteDBOpenhelper.querySingle(MyDBCallBack.TABLE_NAME_PRINTER, "select * from " +
                MyDBCallBack.TABLE_NAME_PRINTER + " where " + PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{name});
        if (printer != null) {
            sb.append(printer.getBusinessNum()).append(",").append(printer.getKitchenNum()).append(",")
                    .append(printer.getCustomerNum()).append(",").append(printer.getSenderNum()).append(",").append(printer.getQrcode());
            result = sb.toString();
        }
        return result;
    }

    /**
     * 请求用户开启自启动权限
     */
    public static void askAutoStartPermission(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.tips_title)
                .setMessage("为了使您云打印机正常工作，本应用需要申请自启动权限，下次开机自启动")
                .setPositiveButton("前往授权", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
                            //MIUI手机引导用户开启自启动权限，否则提醒服务不能自启动
                            //另一方面可以提交小米应用市场，接入小米sdk,从而申请自启动白名单
                            CheckBrandOfPhone.checkXiaomi(context);
                        } else if (Build.MANUFACTURER.equalsIgnoreCase("HUAWEI")) {
                            CheckBrandOfPhone.checkHuawei(context);
                        } else if (Build.MANUFACTURER.equalsIgnoreCase("Letv")) {
                            CheckBrandOfPhone.checkLetv(context);
                        } else {
                            //其他手机引导用户开启自启动权限，否则提醒服务不能自启动
                            CheckBrandOfPhone.enableBootPermission(context);
                        }
                    }
                }).setNegativeButton("不在提醒", null)
                .create()
                .show();
    }


    /**
     * 获取两个List的不同元素
     *
     * @param list1
     * @param list2
     * @return
     */
    public static List<Integer> getDiffrentElement(List<Integer> list1, List<Integer> list2) {
        long st = System.nanoTime();
        List<Integer> diff = new ArrayList<Integer>();
        List<Integer> maxList = list1;
        List<Integer> minList = list2;
        if (list2.size() > list1.size()) {
            maxList = list2;
            minList = list1;
        }
        Map<String, Integer> map = new HashMap<String, Integer>(maxList.size());
        for (Integer key : maxList) {
            map.put(key + "", 1);
        }
        for (Integer key : minList) {
            if (map.get(key + "") != null) {
                map.put(key + "", 2);
                continue;
            }
            diff.add(key);
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                diff.add(Integer.parseInt(entry.getKey()));
            }
        }
        Log.d(TAG, "获取两个集合不同元素所花的时间为：" + (System.nanoTime() - st));
        return diff;

    }

}
