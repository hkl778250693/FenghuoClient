package com.fenghks.business.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import com.fenghks.business.R;
import com.fenghks.business.splash.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.fenghks.business.authorize.AuthorizeActivity.AUTHORIZE_ELEME;
import static com.fenghks.business.authorize.AuthorizeActivity.AUTHORIZE_MEITUAN;

public class CommonUtil {

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

    //登录
    public static final String LOGIN_URL = "/v1/business/login";
    //检查登录
    public static final String CHECK_LOGIN_URL = "/v1/business/checkBusinessLogin";
    //获取城市/区域名称
    public static final String GET_CITY_URL = "/v1/business/getCity";

    //获取商家详情
    public static final String GET_BUSINESS_URL = "/v1/business/businessDetail";
    //获取商家名下的店铺列表
    public static final String GET_SHOPS_URL = "/v1/business/listShop";

    //获取区域的配送员列表
    public static final String LIST_REDIS_SENDERS_URL = "/v1/business/listSendersR";
    //获取配送员详情
    public static final String GET_REDIS_SENDER_URL = "/v1/business/getSenderR";
    //获取配送员详情
    public static final String GET_SENDER_DETAIL_URL = "/v1/business/senderDetail";

    //获取店铺订单列表
    public static final String GET_SHOP_ORDERS_URL = "/v1/business/listOrders";
    //获取订单菜品列表
    public static final String GET_ORDERS_ITEMS_URL = "/v1/business/getOrderItems";
    //创建订单
    public static final String CREATE_ORDERS_URL = "/v1/business/createOrder";
    //下单
    public static final String START_ORDERS_URL = "/v1/business/startOrder";
    //退单、异常单
    public static final String CHARGEBACK_ORDERS_URL = "/v1/business/chargeback";
    //给配送员发推送消息
    public static final String PUSH_TO_SENDER_URL = "/v1/business/pushToSender";

    //微信预下单
    public static final String WX_PREORDER_URL = "/v1/business/wxAppUnifiedOrder";

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


    //获取美团devID
    public static final String GET_MEITUAN_ID_URL = "/v1/meituan/getdevid";

    //美团回调URL
    public static final String MEITUAN_BIND_CALLBACK_URL = "/v1/meituan/callback/bindcallback";

    //获取饿了么授权
    public static final String GET_ELEME_AUTH_URL = "/v1/eleme/getOAuthUrl";


    public static String app_version;
    public static String app_url;

    public static RequestParams genGetParam(String url, Map<String, String> map) {
        RequestParams params = new RequestParams(AppConstants.BASE_URL + url);
        if (null != map) {
            if (null != FenghuoApplication.token) {
                params.addParameter("token", FenghuoApplication.token);
            }
            /*if(0 < FenghuoApplication.business.getId()){
                params.addParameter("businessid",FenghuoApplication.business.getId());
			}*/
            params.addParameter("deviceType", "1");
            params.addParameter("type", "business");
            for (String key : map.keySet()) {
                params.addParameter(key, map.get(key));
            }
        }
        return params;
    }

    public static RequestParams genGetThirdParam(String url, Map<String, String> map, int authorize_platform) {
        String baseUrl;
        boolean isHttps = false;
        switch (authorize_platform) {
            case AUTHORIZE_ELEME:
                baseUrl = AppConstants.ELEME_BASE_URL;
                isHttps = true;
                break;
            case AUTHORIZE_MEITUAN:
                baseUrl = AppConstants.MEITUAN_BASE_URL;
                break;
            default:
                baseUrl = AppConstants.BASE_URL;
                break;
        }
        RequestParams params = new RequestParams(baseUrl + url);
        if (null != map) {
            if (null != FenghuoApplication.token) {
                params.addParameter("token", FenghuoApplication.token);
            }
            /*if(0 < FenghuoApplication.business.getId()){
				params.addParameter("businessid",FenghuoApplication.business.getId());
			}*/
            params.addParameter("deviceType", "1");
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
        Log.d("genGetUrl", "网址是：" + url);
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

    /**
     * 检查登录情况
     *
     * @param context
     */
    public static void checkLogin(final Context context, final int from) {
        final String username = SPUtil.getString(context, "account", "");
        final String password = SPUtil.getString(context, "password", "");
        final int accountType = SPUtil.getInt(context, "accountType", 0);
        if (StringUtil.isEmpty(FenghuoApplication.token)) {
            FenghuoApplication.token = SPUtil.getString(context, "token");
        }
        if (StringUtil.isEmpty(FenghuoApplication.token) && StringUtil.isEmpty(username)) {
            FenghuoApplication.needLogin = true;
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("from", from);
            context.startActivity(intent);
            return;
        }
        final Map map = new HashMap();
        map.put("businessname", username);
        map.put("businesspwd", password);
        map.put("accountType", accountType);
        x.http().post(genGetParam(CHECK_LOGIN_URL, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                JSONObject obj = ParseUtils.parseDataString(context, result);
                if (null != obj) {
                    FenghuoApplication.business = ParseUtils.parseBusiness(obj);
                    if (obj.has("token")) {
                        FenghuoApplication.token = obj.optString("token");
                        SPUtil.putString(context, "token", FenghuoApplication.token);
                    }
                    FenghuoApplication.needLogin = false;
					/*if(getIsFirst(context)){
						registerDevice(context);
					}*/
                } else {
                    if (from == AppConstants.FROM_SPLASH) {
                        FenghuoApplication.needLogin = true;
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("from", from);
                        context.startActivity(intent);
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

    public static void startLogin(Context context, int from) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("source ", from);
        context.startActivity(intent);
		/*if(from == AppConstants.FROM_SPLASH){
		}else {
			autoLogin(context,from);
		}*/
    }

    public static void autoLogin(final Context context, final int from) {
        final String username = SPUtil.getString(context, "account", "");
        final String password = SPUtil.getString(context, "password", "");
        final int accountType = SPUtil.getInt(context, "accountType", 0);
        if ("".equals(username) || "".equals(password)) {
            if (from == AppConstants.FROM_SPLASH) {
                FenghuoApplication.needLogin = true;
            } else {
                //startLogin(context,from);
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("from", from);
                context.startActivity(intent);
            }
        } else {
            final Map map = new HashMap();
            map.put("businessname", username);
            map.put("businesspwd", password);
            map.put("accountType", accountType);
            x.http().post(CommonUtil.genGetParam(CommonUtil.LOGIN_URL, map), new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println(result);
                    JSONObject obj = ParseUtils.parseDataString(context, result);
                    if (null != obj) {
                        FenghuoApplication.business = ParseUtils.parseBusiness(obj);
                        FenghuoApplication.token = obj.optString("token");
                        SPUtil.putString(context, "token", FenghuoApplication.token);
                        FenghuoApplication.needLogin = false;
						/*if(getIsFirst(context)){
							registerDevice(context);
						}*/
                    } else {
                        if (from == AppConstants.FROM_SPLASH) {
                            FenghuoApplication.needLogin = true;
                        } else {
                            startLogin(context, from);
                        }
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    networkErr(ex, isOnCallback);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    public static void logout(Context context) {
        SPUtil.putString(context, "token", "");
        FenghuoApplication.token = "";
        FenghuoApplication.needLogin = true;
        FenghuoApplication.business = null;
        FenghuoApplication.currentShop = 0;
        FenghuoApplication.shops.clear();
		/*if(FenghuoApplication.orders != null){
			FenghuoApplication.orders.clear();
		}*/
        SPUtil.putString(context, "city", "");
        FenghuoApplication.city = null;
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK/* | Intent.FLAG_ACTIVITY_NEW_TASK*/);
        intent.putExtra("from", AppConstants.FROM_SPLASH);
        context.startActivity(intent);
    }

    public static void handleErr(Context context, String json, String note) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
            switch (obj.optInt("status")) {
                case AppConstants.ERR_INVALID_TOKEN:
                    autoLogin(context, AppConstants.FROM_ACTIVITY);
                    break;
            }
            Toast.makeText(context, note, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveLogin(Context context, String token, String account, String password, int accountType) {
        SPUtil.putString(context, "token", token);
        SPUtil.putString(context, "account", account);
        SPUtil.putString(context, "password", password);
        SPUtil.putInt(context, "accountType", accountType);
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
        x.http().post(CommonUtil.genGetParam(CommonUtil.REGISTER_URL, map), new Callback.CommonCallback<String>() {
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

    /**
     * 读取打印设置信息
     *
     * @param context
     * @return
     */
    public static String getPrintSettings(Context context) {
        String print_setting = SPUtil.getString(context, AppConstants.SP_SETTINGS, "pref_print_business", "0")
                + "," + SPUtil.getString(context, AppConstants.SP_SETTINGS, "pref_print_kitchen", "0")
                + "," + SPUtil.getString(context, AppConstants.SP_SETTINGS, "pref_print_customer", "0")
                + "," + SPUtil.getString(context, AppConstants.SP_SETTINGS, "pref_print_sender", "0")
                + "," + (SPUtil.getBoolean(context, AppConstants.SP_SETTINGS, "pref_print_qr", false) ? "1" : "0");
        return print_setting;
    }

    /**
     * 后台服务器注册推送服务
     */
    public static void registerPushService(final Context context) {
        if (FenghuoApplication.shops == null || FenghuoApplication.shops.size() == 0 || StringUtil.isEmpty(FenghuoApplication.xg_token)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("device_token", FenghuoApplication.xg_token);
        //map.put("deviceType","1");
        String businessids = "";
        for (int i = 0; i < FenghuoApplication.shops.size(); i++) {
            businessids += FenghuoApplication.shops.get(i).getId() + ",";
        }
        map.put("businessid", businessids.substring(0, businessids.length() - 1));
        x.http().post(CommonUtil.genGetParam(CommonUtil.ADD_PUSH_DEVICE_URL, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                JSONArray obj = ParseUtils.parseDataArray(context, result);
                if (obj != null) {
                    System.out.println("Register push service successed!");
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
     * 反注册推送服务
     */
    public static void unregisterPushService(final Context context) {
        if (FenghuoApplication.shops == null || FenghuoApplication.shops.size() == 0
                || StringUtil.isEmpty(FenghuoApplication.xg_token)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("device_token", FenghuoApplication.xg_token);
        //map.put("deviceType","1");
        String businessids = "";
        for (int i = 0; i < FenghuoApplication.shops.size(); i++) {
            businessids += FenghuoApplication.shops.get(i).getId() + ",";
        }
        map.put("businessid", businessids.substring(0, businessids.length() - 1));
        x.http().post(CommonUtil.genGetParam(CommonUtil.REMOVE_PUSH_DEVICE_URL, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                JSONArray obj = ParseUtils.parseDataArray(context, result);
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
     * 检查更新
     *
     * @param mContext
     * @param isManual
     */
    public static void checkUpdate(final Context mContext, final boolean isManual) {
        app_version = getAppVersion(mContext);
        Map<String, String> map = new HashMap<String, String>();
        map.put("appcode", "fenghuo");
        map.put("pushtoken", "");
        map.put("appType", "business");
        x.http().post(CommonUtil.genGetParam(CommonUtil.GET_UPDATE_URL, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("---Check New Version--->:" + result);
                JSONObject obj = ParseUtils.parseDataString(mContext, result);
                String androidv = obj.optString("version");
                app_url = obj.optString("AppUrl");
                String info = obj.optString("info");
                if (null == androidv || androidv.equals(app_version) ||
                        Double.parseDouble(androidv) <= Double.parseDouble(app_version) ||
                        StringUtil.isEmpty(app_url)) {
                    if (isManual) {
                        Toast.makeText(mContext, R.string.no_new_version, Toast.LENGTH_SHORT).show();
                    }
                    return;
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("androidv", androidv);
                    intent.putExtra("version", app_version);
                    intent.putExtra("androidUrl", app_url);
                    intent.putExtra("info", info);
                    //设置广播的action，只有和这个action一样的接受者才能接受者才能接收广播
                    intent.setAction(AppConstants.ACTION_RECEIVER_UPDATE);
                    mContext.sendBroadcast(intent);   //发送广播
                    //Toast.makeText(mContext, R.string.new_version_available, Toast.LENGTH_SHORT).show();
					/*AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setTitle(R.string.new_version_title)
							.setMessage(R.string.new_version_available)
							.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							})
							.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									Intent intent = new Intent();
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent.setAction("android.intent.action.VIEW");
									intent.setData(Uri.parse(app_url));
									mContext.startActivity(intent);
								}
					});
					AlertDialog dialog=builder.create();
					//dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
					dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					dialog.show();*/
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
     * 返回当前程序版本号
     */
    public static String getAppVersion(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "版本号未知";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
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
            Toast.makeText(x.app(), R.string.network_error, Toast.LENGTH_LONG).show();
        } else { // 其他错误
            Toast.makeText(x.app(), R.string.other_error, Toast.LENGTH_LONG).show();
        }
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

}
