package com.fenghks.business.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    /**
     * 测量View的宽高
     *
     * @param view View
     */
    public static void measureWidthAndHeight(View view) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
