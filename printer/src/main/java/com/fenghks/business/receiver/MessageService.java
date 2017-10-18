package com.fenghks.business.receiver;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.view.WindowManager;

import com.fenghks.business.AppConstants;
import com.fenghks.business.R;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.StringUtil;

/**
 * 版本更新服务
 */
public class MessageService extends Service {
    ServiceReceiver receiver;

    public MessageService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new ServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_RECEIVER_UPDATE);
        filter.addAction(AppConstants.ACTION_RECEIVER_MSG);
        registerReceiver(receiver,filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public class ServiceReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            AlertDialog dialog = null;
            switch (action) {
                case AppConstants.ACTION_RECEIVER_UPDATE:
                    String title = getString(R.string.new_version_title) + intent.getStringExtra("androidv");
                    String info = intent.getStringExtra("info");
                    if(StringUtil.isEmpty(info)) info = getString(R.string.new_version_available);
                    builder.setTitle(title)
                            .setMessage(info)
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
                                    Intent intent1 = new Intent();
                                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent1.setAction("android.intent.action.VIEW");
                                    intent1.setData(Uri.parse(intent.getStringExtra("androidUrl")));
                                    context.startActivity(intent1);
                                }
                            });
                    /*dialog=builder.create();
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    dialog.show();*/
                    break;
                case AppConstants.ACTION_RECEIVER_MSG:
                    builder.setTitle(R.string.notice)
                            .setMessage(intent.getStringExtra("message"))
							/*.setNegativeButton(R.string.msg_cancel_text, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							})*/
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if(CommonUtil.isAppIsInBackground(context)){
                                        /*Intent intent2 = new Intent(context, SplashActivity.class);
                                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent2);*/
                                    }
                                }
                            });
                    break;
            }
            dialog=builder.create();
            dialog.setCancelable(false);
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            dialog.show();
        }
    }

}
