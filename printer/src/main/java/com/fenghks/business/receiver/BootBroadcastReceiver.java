package com.fenghks.business.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.fenghks.business.MainActivity;
import com.fenghks.business.utils.CheckBrandOfPhone;

/**
 * Android 系统在完成启动后，会发射一个ACTION_BOOT_COMPLETED的广播，并且这个广播只有系统才能发送，
 * 来广播系统启动已完成，因此我们如果可以在APP内拦截这个广播的话，就可以让APP做到开机自启动。
 * 自定义BroadcastReceiver 来接收BOOT_COMPLETE广播
 * Created by fenghuo on 2017/9/1.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "boot completed");

            Intent intent1 = context.getPackageManager().getLaunchIntentForPackage("com.fenghks.business");
            //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
        //intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            Intent intent1 = context.getPackageManager().getLaunchIntentForPackage("com.fenghks.business");
            context.startActivity(intent1);
        }
    }
}
