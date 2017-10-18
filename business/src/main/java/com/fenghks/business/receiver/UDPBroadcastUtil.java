package com.fenghks.business.receiver;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by fenghuo on 2017/8/30.
 */

public class UDPBroadcastUtil {
    /**
     * 发送UDP广播
     *
     * @param context
     * @param message
     */
    public static void sendUDPBroadcast(Context context, String message) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        /*这里获取了IP地址，获取到的IP地址还是int类型的。*/
        int ip = wifiInfo.getIpAddress();
        /*这里就是将int类型的IP地址通过工具转化成String类型的，便于阅读
        String ips = Formatter.formatIpAddress(ip);
        */
        //这一步就是将本机的IP地址转换成xxx.xxx.xxx.255
        int broadCastIP = ip | 0xFF000000;
        message = (message == null ? "Hello Android!" : message);
        int serverPort = 8888;
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress ipAddress = null;
        try {
            //换成服务器端IP
            ipAddress = InetAddress.getByName(Formatter.formatIpAddress(broadCastIP));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int msgLength = message.length();
        byte[] messageByte = message.getBytes();
        Log.e("UDPBroadcastUtil", "原始数据为：" + message + "=====发送的数据为：" + message + "===字节数组为：" + messageByte + "======数据长度为：" + msgLength);
        DatagramPacket datagramPacket = new DatagramPacket(messageByte, msgLength, ipAddress, serverPort);
        try {
            if (socket != null) {
                socket.send(datagramPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
