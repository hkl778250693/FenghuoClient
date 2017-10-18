package com.fenghks.business.print;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.bean.Printer;
import com.fenghks.business.bean.PrinterDevice;
import com.fenghks.business.bean.PrinterEntry;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.MyDBCallBack;
import com.fenghks.business.utils.MySqliteDBOpenhelper;
import com.fenghks.business.utils.SPUtil;

import java.util.HashMap;
import java.util.List;

/**
 * USB打印机类（初始化、打印方法等）
 * Created by fenghuo on 2017/9/7.
 */

public class USBPrinter {
    private static final String TAG = "USBPrinter";
    private static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";
    private static USBPrinter mInstance;
    private Context mContext;
    private UsbDevice mUsbDevice;
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mUsbDeviceConnection;
    private PrinterDevice entity;
    private List<PrinterDevice> allDeviceList;

    /*private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        mUsbDevice = usbDevice;
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (mUsbDevice != null) {
                    Toast.makeText(context, "打印机掉线了！", Toast.LENGTH_SHORT).show();
                    if (mUsbDeviceConnection != null) {
                        mUsbDeviceConnection.close();
                    }
                }
            }
        }
    };*/

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();  //这里是通过USBManager获取到设备列表
            allDeviceList = JSON.parseArray(SPUtil.getString(context, AppConstants.PRINTER_LIST), PrinterDevice.class);
            Log.e(TAG, "======接收到广播， action: " + action + "连接的设备个数有：" + deviceList.size());
            if (!SPUtil.getBoolean(context, AppConstants.IS_ACTIVATED, false)) {   //如果已激活，再判断USB的接入和拔出
                if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {   //设备插入
                    mUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (mUsbManager.hasPermission(mUsbDevice)) {
                        if (mUsbDevice != null) {
                            String printerName = mUsbDevice.getDeviceName();
                            int deviceClass = mUsbDevice.getDeviceClass();
                            if (deviceClass == 0) {
                                UsbInterface mInterface = mUsbDevice.getInterface(0);
                                int interfaceClass = mInterface.getInterfaceClass();
                                if (interfaceClass == 7) {  //此设备是打印机，添加到打印机列表中
                                    Printer printer = CommonUtil.getSingleEntity(printerName);
                                    if (null == printer) {
                                        //本地数据库不存在该打印机，直接添加到本地数据库和SP
                                        savePrinterToSP(context, printerName);
                                        boolean succeed = MySqliteDBOpenhelper.insert(MyDBCallBack.TABLE_NAME_PRINTER, new Printer(SPUtil.getInt(context, AppConstants.APP_SHOP_ID), "0", 9100, printerName, 1, 0, 0, 0, 1, 0, AppConstants.PRINTER_TYPE_USB, AppConstants.STATUS_NORMAL));  //将当前连接的打印机添加到本地数据库，并赋予默认值
                                        if (succeed)
                                            Log.e(TAG, "本地数据库添加打印设备成功！设备名称：" + printerName);
                                    } else {
                                        //如果本地存在，则判断该打印机状态
                                        if (printer.getStatus() == AppConstants.STATUS_OFF_LINE) {
                                            Log.d(TAG, "当前打印机之前已有保存记录哦！");
                                            //直接将打印机添加到SP列表，并同时修改数据库中该打印机为正常状态
                                            savePrinterToSP(context, printerName);
                                            boolean succeed = MySqliteDBOpenhelper.update(MyDBCallBack.TABLE_NAME_PRINTER, printer, PrinterEntry.COLUMN_NAME_NAME + "=?" + " and " + PrinterEntry.COLUMN_NAME_PRINTER_STATUS + "=?", new String[]{printerName, String.valueOf(AppConstants.STATUS_NORMAL)});
                                            if(succeed){
                                                Log.d(TAG, "当前USB打印机状态已修改为正常状态！");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, mUsbDevice.getDeviceName() + "设备没有权限");
                    }
                } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {    //设备移除
                    mUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (mUsbDevice != null) {
                        String printerName = mUsbDevice.getDeviceName();
                        int deviceClass = mUsbDevice.getDeviceClass();
                        entity = new PrinterDevice(mUsbDevice, printerName, 2, "0");   //实例化打印机设备
                        if (deviceClass == 0) {
                            UsbInterface mInterface = mUsbDevice.getInterface(0);
                            int interfaceClass = mInterface.getInterfaceClass();
                            if (interfaceClass == 7) {  //此设备是打印机，从SP打印机列表中移除，但是不删除数据库的配置信息，下次再次连接直接用（但要修改本地状态为掉线）
                                Printer printer = CommonUtil.getSingleEntity(printerName);
                                if (null != printer) {
                                    //如果本地存在，则判断该打印机状态是否为正常状态
                                    if (printer.getStatus() == AppConstants.STATUS_NORMAL) {
                                        //直接将打印机移除SP列表，并同时修改数据库中该打印机为离线（掉线）状态
                                        removePrinterFromSP(context, printerName);
                                        boolean succeed = MySqliteDBOpenhelper.update(MyDBCallBack.TABLE_NAME_PRINTER, printer, PrinterEntry.COLUMN_NAME_NAME + "=?" + " and " + PrinterEntry.COLUMN_NAME_PRINTER_STATUS + "=?", new String[]{printerName, String.valueOf(AppConstants.STATUS_OFF_LINE)});
                                        if(succeed){
                                            Log.d(TAG, "当前USB打印机状态已修改为离线（掉线）状态！");
                                        }
                                        //上报打印机掉线

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    /**
     *  将打印机添加到SP
     *
     * @param context
     * @param printerName
     */
    private void savePrinterToSP(Context context, String printerName) {
        entity = new PrinterDevice(mUsbDevice, printerName, 2, "0");   //实例化打印机设备
        Log.d(TAG, "添加前SP打印机列表的长度为：" + allDeviceList.size());
        allDeviceList.add(entity);    //将USB打印机设备添加到SP集合
        Log.d(TAG, "添加后SP打印机列表的长度为：" + allDeviceList.size());
        SPUtil.putString(context, AppConstants.PRINTER_LIST, JSON.toJSONString(allDeviceList));
    }

    /**
     *  将打印机从SP 移除
     *
     * @param context
     * @param printerName
     */
    private void removePrinterFromSP(Context context, String printerName) {
        Log.d(TAG, "删除前打印机列表的长度为：" + allDeviceList.size());
        for (int i = 0; i < allDeviceList.size(); i++) {
            if (allDeviceList.get(i).getName().equals(printerName)) {
                allDeviceList.remove(i);    //将网络打印机设备移除集合
            }
        }
        Log.d(TAG, "删除后打印机列表的长度为：" + allDeviceList.size());
        SPUtil.putString(context, AppConstants.PRINTER_LIST, JSON.toJSONString(allDeviceList));
        Log.e(TAG, "移除打印设备！设备名称：" + printerName);
    }

    private USBPrinter() {

    }

    /**
     * 获取类实例
     *
     * @return
     */
    private static USBPrinter getInstance() {
        if (mInstance == null) {
            mInstance = new USBPrinter();
        }
        return mInstance;
    }

    /**
     * 初始化打印机，需要与destroy对应
     *
     * @param context 上下文
     */
    public static void initUSBPrinter(Context context) {
        getInstance().init(context);
    }

    /**
     * 销毁打印机持有的对象
     */
    public static void destroyPrinter() {
        getInstance().destroy();
    }

    /**
     * 初始化工作
     *
     * @param context
     */
    private void init(Context context) {
        mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mContext.registerReceiver(mUsbReceiver, filter);

        /*//列出所有的USB设备，并且都请求获取USB权限
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            mUsbManager.requestPermission(device, mPermissionIntent);
        }*/
    }

    /**
     * 关闭连接，反注册广播
     */
    private void destroy() {
        mContext.unregisterReceiver(mUsbReceiver);
        /*if (mUsbDeviceConnection != null) {
            mUsbDeviceConnection.close();
            mUsbDeviceConnection = null;
        }*/
        mContext = null;
        mUsbManager = null;
    }

    /**
     * USB打印方法，传入打印机实例和打印内容
     * 返回值：返回负数则打印失败，正值代表打印内容的字节大小
     *
     * @param printer
     * @param printData
     */
    public static int print(Context context, UsbDevice printer, final byte[] printData) {
        int status = -1;  //默认-1，负数代表失败
        UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (printer != null) {
            UsbInterface usbInterface = printer.getInterface(0);
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {   //USBEndpoint为读写数据所需的节点
                final UsbEndpoint ep = usbInterface.getEndpoint(i);
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {   //大块传输端点类型
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {     //输出
                        final UsbDeviceConnection mUsbDeviceConnection = mUsbManager.openDevice(printer); //打开conn连接通道
                        if (mUsbDeviceConnection != null) {
                            Log.d(TAG, "打印机连接成功！");
                            mUsbDeviceConnection.claimInterface(usbInterface, true);
                            status = mUsbDeviceConnection.bulkTransfer(ep, printData, printData.length, 100000);
                            Log.d(TAG, "打印返回的状态值为：" + status);
                            mUsbDeviceConnection.releaseInterface(usbInterface);  //断开设备
                            mUsbDeviceConnection.close();
                            break;
                        } else {
                            Log.d(TAG, "打印机连接失败！");
                        }
                    }
                }
            }
            return status;
        } else {
            Log.d(TAG, "没有可用的USB打印机：");
        }
        return status;
    }
}
