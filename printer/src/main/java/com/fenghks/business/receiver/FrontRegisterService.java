package com.fenghks.business.receiver;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.print.USBPrinter;
import com.fenghks.business.print.params.Constant;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.PollingUtils;
import com.fenghks.business.utils.SPUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

/**
 * 开启一个前台服务，监听与商家的通信，拿到商家ID和token用于提交服务器注册
 * Created by fenghuo on 2017/8/31.
 */
public class FrontRegisterService extends Service {
    private static final String TAG = "FrontRegisterService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //用startForeground()启用前台服务
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("监听与商家UDP通信！");
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setTicker("通知");
        builder.setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        //设置通知默认效果
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        startForeground(AppConstants.SERVICE_REGISTER_ID, notification);    //开启前台服务
        new Thread(new Runnable() {
            @Override
            public void run() {
                UDPReceiveServer();   //开启监听
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 开启监听，与客户端连接，获取shopID、token信息
     */
    public void UDPReceiveServer() {
        //UDP服务器监听的端口
        Integer port = 8888;
        // 接收的字节大小，客户端发送的数据不能超过这个大小
        byte[] buffer = new byte[1024];
        DatagramSocket datagramSocket = null;
        try {
            // 建立Socket连接
            datagramSocket = new DatagramSocket(port);
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            while (true) {
                // 准备接收数据
                datagramSocket.receive(datagramPacket);
                String result = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                Log.d(TAG, datagramPacket.getAddress().getHostAddress() + ":" + new String(datagramPacket.getData()));
                Log.e(TAG, "接受到的数据为：" + result + "======字节数组为：" + datagramPacket.getData().length + "======长度为：" + datagramPacket.getLength());
                if (!"".equals(result)) {
                    boolean succeed = CommonUtil.registerCloudPrintDevice(result, getApplicationContext());   //调用接口，注册云接单设备信息
                    if (succeed) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //stopForeground(STOP_FOREGROUND_REMOVE);  //关闭服务
                            stopSelf();
                        }
                        break;    //注册成功 退出循环
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
