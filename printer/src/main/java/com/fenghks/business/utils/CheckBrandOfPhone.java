package com.fenghks.business.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by fenghuo on 2017/9/26.
 */

public class CheckBrandOfPhone {
    private static final String TAG = "CheckBrandOfPhone";
    public static void checkXiaomi(Context context) {
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(localIntent);
    }

    public static void checkHuawei(Context context) {
        try {
            // 华为大坑，不能直接用Intent来启动，会启不起来
            // --> _ -->   多谢这位大兄弟的提醒
            Log.d(TAG, "华为检测进来了");
            String cmd = "am start -n com.checkHuawei.systemmanager/.optimize.process.ProtectActivity";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial(context);
                Log.d(TAG, "华为检测进来了");
            }
            Log.d(TAG, "华为检测进来了");
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void checkLetv(Context ctx) {
        boolean hasLetvsafe;
        try {
            ApplicationInfo info = ctx.getPackageManager().getApplicationInfo("com.letv.android.letvsafe", PackageManager.GET_UNINSTALLED_PACKAGES);
            hasLetvsafe = info != null;
        } catch (PackageManager.NameNotFoundException e) {
            hasLetvsafe = false;
        }
        if (hasLetvsafe) {
            String ACTION_PERMISSION_AUTOBOOT = "com.letv.android.permissionautoboot";
            Intent intent = new Intent(ACTION_PERMISSION_AUTOBOOT);
            ctx.startActivity(intent);
        }
    }


    public static void enableBootPermission(Context context) {
        Intent localIntent1 = new Intent();
        localIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent1.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent1.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent1);
    }

    public static String getUserSerial(Context ctx) {
        //noinspection ResourceType
        Object userManager = ctx.getSystemService("user");
        if (userManager == null) return "";
        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            return String.valueOf(userSerial);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
