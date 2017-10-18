package com.fenghks.business.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.fenghks.business.AppConstants;
import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.PrinterDevice;
import com.fenghks.business.print.EscPos;
import com.fenghks.business.print.PrintContent;
import com.fenghks.business.print.params.Constant;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * 打印订单工具类（网络打印机、USB打印机）
 * Created by fenghuo on 2017/9/7.
 */

public class PrintOderUtil {
    private static final String TAG = "PrintOderUtil";

    /**
     * USB打印订单
     *
     * @param context
     * @param order
     * @param print_settings
     * @return
     */
    public static byte[] usbPrintOrder(Context context, Order order, String print_settings) {
        ByteArrayOutputStream baos = null;
        byte[] temp = null;
        try {
            baos = new ByteArrayOutputStream();
            String[] nums = print_settings.split(",");
            EscPos escPos = EscPos.getInstance(baos);
            String template = null;
            for (int i = 0; i < nums.length; i++) {
                int count = Integer.parseInt(nums[i]);
                if (count >= 1) {
                    if (i == 4) {
                        String[] nos = order.getOrdercode().split("#");
                        String qr_str = "";
                        if (nos.length > 1 && nos[nos.length - 1].matches("[0-9]+")) {
                            qr_str += "#" + nos[nos.length - 1] + " : ";
                        }
                        escPos.printQR(CommonUtil.QR_BASE + order.getId(), qr_str + order.getGetcode());
                    } else {
                        template = readTemplate(context, getTemplateName(i));
                        String content = PrintContent.gen(context, order, i);
                        for (int j = 0; j < count; j++) {
                            escPos.print(template, content);
                        }
                    }
                }
            }
            baos.flush();
            temp = baos.toByteArray();
            Log.d(TAG, "USB打印成功！");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return temp;
    }

    /**
     * 网络打印机打印订单
     *
     * @param context
     * @param ipAddress
     * @param order
     * @param print_settings
     * @return
     */
    public static boolean networkPrintOrder(Context context, String ipAddress, Order order, String print_settings) {
        boolean isSucceed = false;
        OutputStream outputStream = null;
        ByteArrayOutputStream baos = null;
        Socket socket = null;
        Log.d(TAG, "字节流开始写入数据！" + "设置为：" + print_settings);
        try {
            baos = new ByteArrayOutputStream();  //获取字节数组输出流
            String[] nums = print_settings.split(",");
            EscPos escPos = EscPos.getInstance(baos);
            String template;
            for (int i = 0; i < nums.length; i++) {
                int count = Integer.parseInt(nums[i]);
                if (count >= 1) {
                    if (i == 4) {
                        String[] nos = order.getOrdercode().split("#");
                        String qr_str = "";
                        if (nos.length > 1 && nos[nos.length - 1].matches("[0-9]+")) {
                            qr_str += "#" + nos[nos.length - 1] + " : ";
                        }
                        escPos.printQR(CommonUtil.QR_BASE + order.getId(), qr_str + order.getGetcode());
                    } else {
                        template = readTemplate(context, getTemplateName(i));
                        String content = PrintContent.gen(context, order, i);
                        for (int j = 0; j < count; j++) {
                            escPos.print(template, content);
                        }
                    }
                }
            }
            baos.flush();
            byte[] tmp = baos.toByteArray();
            Log.d(TAG, "字节流结束写入数据！数据长度为：" + tmp.length + "====打印的内容为：" + tmp.toString());
            socket = new Socket(ipAddress, Constant.DEFAULT_PORT);
            outputStream = socket.getOutputStream();
            outputStream.write(tmp);
            outputStream.flush();
            Log.d(TAG, "输出流刷新成功！");
            if (tmp.length > 0) {
                isSucceed = true;
            } else {
                isSucceed = false;
            }
            Log.d(TAG, "网络打印完成，返回成功信息！");
            //将打印成功的订单ID保存到SP
            List<Integer> orderIdList = JSON.parseArray(SPUtil.getString(context, AppConstants.PRINT_ORDER_ID_LIST), Integer.class);
            Log.d(TAG, "打印成功的订单ID列表的长度添加前为：" + orderIdList.size());
            orderIdList.add(order.getId());
            SPUtil.putString(context, AppConstants.PRINT_ORDER_ID_LIST, JSON.toJSONString(orderIdList));
            Log.d(TAG, "打印成功的订单ID列表的长度添加后为：" + orderIdList.size());

            return isSucceed;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isSucceed;
    }

    private static String getTemplateName(int print_type) {
        switch (print_type) {
            case 0:
                return "business.json";
            case 1:
                return "kitchen.json";
            case 2:
                return "customer.json";
            case 3:
                return "sender.json";
            case 4:
                return "qrcode.json";
        }
        return "business.json";
    }

    private static String readTemplate(Context context, String name) {
        try {
            InputStreamReader isr = new InputStreamReader(context.getAssets().open(name), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
