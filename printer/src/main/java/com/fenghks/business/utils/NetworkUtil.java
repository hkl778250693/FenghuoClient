package com.fenghks.business.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fenghuo on 2017/9/22.
 */

public class NetworkUtil {
    /**
     * 获得 IP 地址，分为两种情况，一是wifi下，二是移动网络下，得到的 IP 地址是不一样的
     *
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //调用方法将int转换为地址字符串
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的 IP 转换为 String 类型
     *
     * @param ip
     * @return
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    /**
     * 获取mac地址有一点需要注意的就是android 6.0版本后，以下注释方法不再适用，不管任何手机都会返回"02:00:00:00:00:02"这个默认的mac地址，
     * 这是googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址。
     *
     * @return 返回mac地址
     */
    // String macAddress= "";
    // WifiManager wifiManager = (WifiManager) MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
    // WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    // macAddress = wifiInfo.getMacAddress();
    // return macAddress;
    public static String getMacAddress() {
        String macAddress = null;
        StringBuilder sb = new StringBuilder();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                sb.append(String.format("%02X:", b));
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            macAddress = sb.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }

    /**
     * 获取设备的mac地址 (该方法在6.0以上不能成功返回)
     *
     * @param ac
     * @param callback 成功获取到mac地址之后会回调此方法
     */
    public static void oldGetMacAddress(final Activity ac, final SimpleCallback callback) {
        final WifiManager wm = (WifiManager) ac.getApplicationContext().getSystemService(Service.WIFI_SERVICE);
        // 如果本次开机后打开过WIFI，则能够直接获取到mac信息，立刻返回数据
        WifiInfo info = wm.getConnectionInfo();
        if (info != null && info.getMacAddress() != null) {
            if (callback != null) {
                callback.onComplete(info.getMacAddress());
            }
            return;
        }
        // 尝试打开WIFI，并获取mac地址
        if (!wm.isWifiEnabled()) {
            wm.setWifiEnabled(true);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int tryCount = 0;
                final int MAX_COUNT = 10;
                while (tryCount < MAX_COUNT) {
                    final WifiInfo info = wm.getConnectionInfo();
                    if (info != null && info.getMacAddress() != null) {
                        if (callback != null) {
                            ac.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onComplete(info.getMacAddress());
                                }
                            });
                        }
                        return;
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tryCount++;
                }
                // 未获取到mac地址
                if (callback != null) {
                    callback.onComplete(null);
                }
            }
        }).start();
    }

    public interface SimpleCallback {
        void onComplete(String result);
    }

    /**
     * 获取手机的 IMEI 号码
     *
     * @return
     */
    public static String getPhoneIMEI(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String mtype = android.os.Build.MODEL; // 手机型号
        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
        return imei;
    }

    /**
     * 获取手机硬件信息
     */
    public static Map<String, String> getDeviceInfo() {
        Map<String, String> map = new HashMap<>();
        //运用反射得到build类里的字段
        Field[] fields = Build.class.getDeclaredFields();
        //遍历字段名数组
        for (Field field : fields) {
            try {
                //将字段都设为public可获取
                field.setAccessible(true);
                //filed.get(null)得到的即是设备信息
                map.put(field.getName(), field.get(null).toString());
                Log.d("CrashHandler", field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                return null;
            }
        }
        return map;
    }

}
