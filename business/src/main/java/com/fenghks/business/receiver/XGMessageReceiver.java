package com.fenghks.business.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.bean.Printer;
import com.fenghks.business.bean.PrinterEntry;
import com.fenghks.business.bean.XGNotification;
import com.fenghks.business.print.params.Constant;
import com.fenghks.business.utils.MyDBCallBack;
import com.fenghks.business.utils.MySqliteDBOpenhelper;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.SPUtil;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class XGMessageReceiver extends XGPushBaseReceiver {
    private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");
    public static final String LogTag = "XGMessageReceiver";

    private void show(Context context, String text) {
        //Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    // 通知展示     获取通知
    @Override
    public void onNotifactionShowedResult(Context context,
                                          XGPushShowedResult notifiShowedRlt) {
        if (context == null || notifiShowedRlt == null) {
            return;
        }
        XGNotification notific = new XGNotification();
        notific.setMsg_id(notifiShowedRlt.getMsgId());
        notific.setTitle(notifiShowedRlt.getTitle());
        notific.setContent(notifiShowedRlt.getContent());
        // notificationActionType==1为Activity，2为url，3为intent
        notific.setNotificationActionType(notifiShowedRlt
                .getNotificationActionType());
        // Activity,url,intent都可以通过getActivity()获得
        notific.setActivity(notifiShowedRlt.getActivity());
        notific.setUpdate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance().getTime()));
        NotificationService.getInstance(context).save(notific);
        context.sendBroadcast(intent);
        show(context, "您有1条新消息, " + "通知被展示 ， " + notifiShowedRlt.toString());
    }

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }
        Log.d(LogTag, text);
        show(context, text);

    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        Log.d(LogTag, text);
        show(context, text);

    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        Log.d(LogTag, text);
        show(context, text);

    }

    // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
    @Override
    public void onNotifactionClickedResult(Context context,
                                           XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + message;
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + message;
        }
        Toast.makeText(context, "广播接收到通知被点击:" + message.toString(),
                Toast.LENGTH_SHORT).show();
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    Log.d(LogTag, "get custom value:" + value);
                }
                // ...
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理的过程。。。
        Log.d(LogTag, text);
        show(context, text);
    }

    @Override
    public void onRegisterResult(Context context, int errorCode,
                                 XGPushRegisterResult message) {
        // TODO Auto-generated method stub
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = message + "注册成功";
            // 在这里拿token
            String token = message.getToken();
        } else {
            text = message + "注册失败，错误码：" + errorCode;
        }
        Log.d(LogTag, text);
        show(context, text);
    }

    // 消息透传
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        // TODO Auto-generated method stub
        String text = "收到消息:" + message.toString();
        Log.d(LogTag, "接收到的所有推送消息为：" + text);
        String contentType = message.getContent();   //消息类型
        if (null != contentType) {
            String customContent = message.getCustomContent();   // 获取自定义key-value
            if (customContent != null && customContent.length() != 0) {
                try {
                    JSONObject obj = new JSONObject(customContent);
                    // key1为前台配置的key
                    Intent broadcast;
                    if ("regclouddevice".equals(contentType)) {   //注册云打印设备
                        if (obj.optInt("result") == 1) {   //注册成功
                            Log.d(LogTag, "云打印机注册成功，发送注册成功广播！");
                            SPUtil.putBoolean(context, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_INITIALIZE, true);  //更新本地注册状态
                            broadcast = new Intent(AppConstants.ACTION_INITIALIZE_RESULT);
                            broadcast.putExtra(AppConstants.IS_INITIALIZE, true);
                            context.sendBroadcast(broadcast);  //发送注册成功广播
                        } else {
                            SPUtil.putBoolean(context, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_INITIALIZE, false);  //更新本地注册状态
                            broadcast = new Intent(AppConstants.ACTION_INITIALIZE_RESULT);
                            broadcast.putExtra(AppConstants.IS_INITIALIZE, false);
                            context.sendBroadcast(broadcast);  //发送注册失败广播
                        }
                    }
                    if (!"".equals(obj.optString("requestId"))) {
                        String requestId = obj.optString("requestId");
                        Log.d(LogTag, "得到的UUID是：" + requestId);
                        SPUtil.putBoolean(context, AppConstants.KEY_PREVIOUS_OPERATION_STATE, true);
                        if (SPUtil.getString(context, AppConstants.KEY_CURRENT_UUID).equals(requestId)) {  //判断是否是当前操作的UUID
                            if ("activate".equals(contentType)) {      //激活云打印设备
                                if (obj.optInt("isactive") == 1) {   //激活设备
                                    if (obj.optInt("result") == 1) {   //激活成功
                                        SPUtil.putBoolean(context, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_ACTIVATED, true);  //更新本地激活状态
                                        broadcast = new Intent(AppConstants.ACTION_ACTIVATE_RESULT);
                                        broadcast.putExtra(AppConstants.IS_ACTIVATED, true);
                                        context.sendBroadcast(broadcast);
                                    } else {
                                        SPUtil.putBoolean(context, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_ACTIVATED, false);  //更新本地激活状态
                                        broadcast = new Intent(AppConstants.ACTION_ACTIVATE_RESULT);
                                        broadcast.putExtra(AppConstants.IS_ACTIVATED, false);
                                        context.sendBroadcast(broadcast);
                                    }
                                } else {   //反激活设备
                                    if (obj.optInt("result") == 1) {   //反激活成功
                                        SPUtil.putBoolean(context, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_INITIALIZE, false);  //更新本地注册状态
                                        SPUtil.putBoolean(context, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_ACTIVATED, false);  //更新本地激活状态
                                        broadcast = new Intent(AppConstants.ACTION_ACTIVATE_RESULT);
                                        context.sendBroadcast(broadcast);
                                    } else {
                                        SPUtil.putBoolean(context, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_ACTIVATED, true);  //更新本地激活状态
                                        broadcast = new Intent(AppConstants.ACTION_ACTIVATE_RESULT);
                                        context.sendBroadcast(broadcast);
                                    }
                                }
                            } else if ("getprinters".equals(contentType)) {   //获取打印机列表
                                //清空本地打印机表
                                MySqliteDBOpenhelper.deleteFrom(MyDBCallBack.TABLE_NAME_PRINTER);
                                if (obj.optInt("result") == 1) {   //获取列表成功
                                    JSONArray array = obj.optJSONArray("printers");
                                    if (array.length() > 0) {
                                        Log.d(LogTag, "获取到打印机个数为：" + array.length());
                                        for (int i = 0; i < array.length(); i++) {
                                            insertDataBase(ParseUtils.parsePrinter(array.getJSONObject(i)));   //解析消息体并插入数据库
                                        }
                                        broadcast = new Intent(AppConstants.ACTION_OBTAIN_PRINTERS_RESULT);
                                        broadcast.putExtra(AppConstants.IS_GET_PEINTERS, true);
                                        context.sendBroadcast(broadcast);
                                    }
                                } else {
                                    broadcast = new Intent(AppConstants.ACTION_OBTAIN_PRINTERS_RESULT);
                                    broadcast.putExtra(AppConstants.IS_GET_PEINTERS, false);
                                    context.sendBroadcast(broadcast);
                                }
                            } else if ("setprinter".equals(contentType)) {    //设置打印机
                                if (obj.optInt("result") == 1) {   //设置成功
                                    Log.d(LogTag, "打印机设置成功！");
                                    if (null != MySqliteDBOpenhelper.querySingle(MyDBCallBack.TABLE_NAME_PRINTER, "select * from " +
                                            MyDBCallBack.TABLE_NAME_PRINTER + " where " + PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{obj.optString("name")})) {
                                        savePrinterSetting(ParseUtils.parsePrinter(obj.optJSONObject("printer")), obj.optString("name"));
                                        broadcast = new Intent(AppConstants.ACTION_SETTING_PRINTER_RESULT);
                                        broadcast.putExtra(AppConstants.IS_SETTING_PRINTER, true);
                                        context.sendBroadcast(broadcast);
                                        Log.d(LogTag, "打印机设置成功！");
                                    } else {
                                        //没有查到本地对应的打印机
                                    }
                                } else {
                                    broadcast = new Intent(AppConstants.ACTION_SETTING_PRINTER_RESULT);
                                    broadcast.putExtra(AppConstants.IS_SETTING_PRINTER, false);
                                    broadcast.putExtra(AppConstants.NOTE, obj.optString("note"));
                                    context.sendBroadcast(broadcast);
                                    Log.d(LogTag, "打印机设置失败！");
                                }
                            } else if ("addprinter".equals(contentType)) {    //添加打印机
                                if (obj.optInt("result") == 1) {   //添加成功
                                    addNetWorkPrinter(ParseUtils.parsePrinter(obj.optJSONObject("printer")));
                                    broadcast = new Intent(AppConstants.ACTION_ADD_PRINTER_RESULT);
                                    broadcast.putExtra(AppConstants.IS_ADD_PEINTER, true);
                                    context.sendBroadcast(broadcast);
                                } else {
                                    broadcast = new Intent(AppConstants.ACTION_ADD_PRINTER_RESULT);
                                    broadcast.putExtra(AppConstants.IS_ADD_PEINTER, false);
                                    broadcast.putExtra(AppConstants.NOTE, obj.optString("note"));
                                    context.sendBroadcast(broadcast);
                                }
                            } else if ("delprinter".equals(contentType)) {    //删除打印机
                                if (obj.optInt("result") == 1) {   //成功删除网络打印
                                    if (null != MySqliteDBOpenhelper.querySingle(MyDBCallBack.TABLE_NAME_PRINTER, "select * from " +
                                            MyDBCallBack.TABLE_NAME_PRINTER + " where " + PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{obj.optString("name")})) {
                                        deleteNetPrinter(obj.optString("name"));
                                        broadcast = new Intent(AppConstants.ACTION_DELETE_PRINTER_RESULT);
                                        broadcast.putExtra(AppConstants.IS_DELETE_PRINTER, true);
                                        context.sendBroadcast(broadcast);
                                    } else {
                                        //没有查到本地对应的打印机,无法
                                    }
                                } else {
                                    broadcast = new Intent(AppConstants.ACTION_DELETE_PRINTER_RESULT);
                                    broadcast.putExtra(AppConstants.IS_DELETE_PRINTER, false);
                                    broadcast.putExtra(AppConstants.NOTE, obj.optString("note"));
                                    context.sendBroadcast(broadcast);
                                }
                            }
                        }
                    }
                    //接收打印机异常和订单异常的推送消息
                    if ("printerException".equals(contentType)) {  //打印机异常
                        show(context, "设备名称为 ‘" + obj.optString("name") + " ’的打印机已掉线，请您检测设备实际情况！");
                    } else if ("orderException".equals(contentType)) {    //订单异常
                        show(context, "订单号为 " + obj.optInt("orderId") + " 的订单处理失败，平台来自：" +
                                obj.optString("clientfrom") + "，平台的订单流水号为：" + obj.optInt("DaySn") + "，备注为：" + obj.optString("note"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将打印机列表数据保存本地数据库
     */
    private void insertDataBase(Printer printer) {
        MySqliteDBOpenhelper.insert(MyDBCallBack.TABLE_NAME_PRINTER, printer);
    }

    // 保存打印机设置
    private void savePrinterSetting(Printer printer, String name) {
        MySqliteDBOpenhelper.update(MyDBCallBack.TABLE_NAME_PRINTER, printer, PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{name});
    }

    //添加网络打印机
    private void addNetWorkPrinter(Printer printer) {
        MySqliteDBOpenhelper.insert(MyDBCallBack.TABLE_NAME_PRINTER, printer);
    }

    //删除网络打印机
    private void deleteNetPrinter(String name) {
        MySqliteDBOpenhelper.delete(MyDBCallBack.TABLE_NAME_PRINTER, PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{name});
    }
}
