package com.fenghks.business.receiver;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.Printer;
import com.fenghks.business.bean.PrinterDevice;
import com.fenghks.business.print.EscPos;
import com.fenghks.business.print.USBPrinter;
import com.fenghks.business.print.params.Constant;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.PrintOderUtil;
import com.fenghks.business.utils.SPUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

/**
 * 专门打印订单的服务
 * <p>
 * Created by fenghuo on 2017/9/6.
 */

public class PrintOrderIntentService extends IntentService {
    private static final String TAG = "PrintOrderIntentService";
    private String printSettings = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public PrintOrderIntentService() {
        super(TAG);  // 调用父类的有参构造函数
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Thread id is " + Thread.currentThread().getId() + "Thread name is" + Thread.currentThread().getName());  //当前线程的ID和名称
        if (intent == null) {
            return;
        }
        Order order = (Order) intent.getSerializableExtra("order");  //获取订单信息
        if(order == null){
            Log.d(TAG, "订单内容为空！");
            return;
        }
        /**
         * 遍历打印机列表,循环调用打印
         */
        byte[] printData;  //打印内容字节数组
        int result;  //打印返回的值
        boolean isReport = false; //是否上报
        List<PrinterDevice> deviceList = JSON.parseArray(SPUtil.getString(this, AppConstants.PRINTER_LIST), PrinterDevice.class);
        Log.d(TAG, "查询本地打印机列表，长度为：" + deviceList.size());
        if (deviceList.size() > 0) {
            for (int i = 0; i < deviceList.size(); i++) {
                PrinterDevice entity = deviceList.get(i);
                Printer printer = CommonUtil.getSingleEntity(entity.getName());
                if (printer != null) {
                    if (printer.getIspause() == 0) {   //检测打印机未停用，正常调用打印
                        if (entity.getType() == AppConstants.PRINTER_TYPE_USB) {   //USB打印
                            UsbDevice device = entity.getPrinter();
                            printData = PrintOderUtil.usbPrintOrder(this, order, CommonUtil.getPrinterSetting(entity.getName()));
                            result = USBPrinter.print(this, device, printData);   //打印订单
                            if (result > 0) {  //打印成功，将订单号保存
                                Log.d(TAG, "USB打印成功！保存订单ID");
                            } else {    //打印失败，尝试重新打印（打印三次，若还是失败则上报）
                                for (int j = 0; j < 3; j++) {
                                    Log.d(TAG, "USB打印失败！重复第" + j + "次打印");
                                    result = USBPrinter.print(this, device, printData);   //重新打印订单
                                    if (result > 0) {
                                        Log.d(TAG, "USB重复第" + j + "次的时候打印成功！保存订单ID");
                                        break;
                                    } else {
                                        isReport = true;
                                    }
                                }
                                if (isReport) {   //为true则上报打印失败
                                    Log.d(TAG, "USB打印失败！上报故障信息");
                                    CommonUtil.pushPrintFailure(this, SPUtil.getString(this, AppConstants.APP_XG_TOKEN), order.getId(), order.getOrdercode(), order.getClientfrom(), "重试三次后打印失败，请检查故障信息！");
                                }
                            }
                        } else {  //网络打印
                            Log.d(TAG, "网络打印开始！");
                            boolean repeatPrintSucceed = true;
                            boolean succeed = PrintOderUtil.networkPrintOrder(this, entity.getIpAddress(), order, CommonUtil.getPrinterSetting(entity.getName()));
                            if (!succeed) {   //打印失败，尝试重新打印
                                for (int j = 0; j < 3; j++) {
                                    Log.d(TAG, "网络打印失败！重复第" + j + "次打印");
                                    repeatPrintSucceed = PrintOderUtil.networkPrintOrder(this, entity.getIpAddress(), order, CommonUtil.getPrinterSetting(entity.getName()));   //重新打印订单
                                    if (repeatPrintSucceed) {
                                        Log.d(TAG, "网络重复第" + j + "次的时候打印成功！保存订单ID");
                                        break;
                                    }
                                }
                                if (!repeatPrintSucceed) {   //上报打印失败
                                    Log.d(TAG, "网络打印失败！上报故障信息");
                                    CommonUtil.pushPrintFailure(this, SPUtil.getString(this, AppConstants.APP_XG_TOKEN), order.getId(), order.getOrdercode(), order.getClientfrom(), "重试三次后打印失败，请检查故障信息！");
                                }
                            }
                        }
                    } else {
                        //检测到打印机停用了，则不打印
                        Log.d(TAG, "检测到打印机停用了，不打印");
                    }
                } else {
                    Log.d(TAG, "检测到该打印机被删除了，不打印");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy executed");
    }
}
