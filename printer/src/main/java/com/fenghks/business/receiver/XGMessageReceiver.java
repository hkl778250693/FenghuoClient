package com.fenghks.business.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.Printer;
import com.fenghks.business.bean.PrinterDevice;
import com.fenghks.business.bean.PrinterEntry;
import com.fenghks.business.bean.XGNotification;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.MyDBCallBack;
import com.fenghks.business.utils.MySqliteDBOpenhelper;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.PollingUtils;
import com.fenghks.business.utils.SPUtil;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class XGMessageReceiver extends XGPushBaseReceiver {
    private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");
    public static final String LogTag = "TPushReceiver";
    public static final String TAG = "XGMessageReceiver";

    private void show(Context context, String text) {
//		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    // 通知展示
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
        Log.d(TAG, "接收到的所有推送消息为：" + text);
        String contentType = message.getContent();   //消息类型
        if (null != contentType) {
            String customContent = message.getCustomContent();   // 获取自定义key-value
            if (customContent != null && customContent.length() != 0) {
                try {
                    JSONObject obj = new JSONObject(customContent);
                    if ("order".equals(contentType)) {    //接收到订单类型消息
                        Order order;
                        if (obj.isNull("customContent")) {
                            order = ParseUtils.parseOrder(obj);
                            Log.d(TAG, "获取到的订单ID为：" + order.getId());
                            Log.d(TAG, "本地保存的最新订单ID为：" + FenghuoApplication.lastOrderID);
                            CommonUtil.pushOrderResult(context, order.getId(), 1);//上报成功接收订单
                            if (order.getId() > SPUtil.getInt(context, AppConstants.LAST_ORDER_ID)) {   //返回的订单ID大于上次保存的订单ID则保留并打印
                                SPUtil.putInt(context, AppConstants.LAST_ORDER_ID, order.getId());
                                Log.d(TAG, "更新本地的最新订单ID为：" + SPUtil.getInt(context, AppConstants.LAST_ORDER_ID));
                                //接收到一条订单，立即打印
                                Intent printService = new Intent(context, PrintOrderIntentService.class);
                                printService.putExtra("order", order);  //传递订单参数
                                context.startService(printService);
                            }
                        }
                    } else if ("activate".equals(contentType)) {      //激活云打印设备
                        SPUtil.putString(context, AppConstants.APP_XG_TOKEN, obj.optString("manageToken"));
                        if (obj.optInt("isactive") == 1) {   //激活设备
                            Log.d(TAG, "收到激活的消息推送！");
                            //上报收到激活消息
                            CommonUtil.pushGotActivateInfo(context, obj.optString("manageToken"), obj.optString("requestId"), 1, 1);
                            SPUtil.putBoolean(context, "shop" + obj.optInt("businessid"), AppConstants.IS_ACTIVATED, true);  //更新本地激活状态
                            //开启订单定时请求服务  轮询服务
                            PollingUtils.startPollingService(context, 2 * 60, ObtainOrderListService.class);
                        } else {   //反激活设备
                            Log.d(TAG, "收到反激活的消息推送！");
                            CommonUtil.pushGotActivateInfo(context, obj.optString("manageToken"), obj.optString("requestId"), 0, 1);
                            SPUtil.putBoolean(context, AppConstants.IS_INITIALIZE, false);
                            SPUtil.putBoolean(context, "shop" + obj.optInt("businessid"), AppConstants.IS_ACTIVATED, false);  //更新本地激活状态
                            SPUtil.removeKey(context, AppConstants.APP_XG_TOKEN);
                            //关闭订单定时请求服务
                            PollingUtils.stopPollingService(context, ObtainOrderListService.class);
                        }
                    } else if ("getprinters".equals(contentType)) {   //获取打印机列表
                        //查询本地打印机表
                        List<Printer> printerList = CommonUtil.getLocalPrinters(context);
                        SPUtil.putString(context, AppConstants.APP_XG_TOKEN, obj.optString("manageToken"));
                        if (printerList != null && printerList.size() > 0) {
                            CommonUtil.pushPrinters(context, obj.optString("manageToken"), printerList, obj.optString("requestId"), 1);    //上报列表数据   有数据
                        } else {
                            CommonUtil.pushPrinters(context, obj.optString("manageToken"), printerList, obj.optString("requestId"), 0);    //上报列表数据   列表为空
                        }
                    } else if ("setprinter".equals(contentType)) {    //设置打印机
                        Printer oldPrinter = CommonUtil.getSingleEntity(obj.optString("name"));
                        SPUtil.putString(context, AppConstants.APP_XG_TOKEN, obj.optString("manageToken"));
                        if (null != CommonUtil.getSingleEntity(obj.optString("name"))) {
                            Printer newPrinter = ParseUtils.parsePrinter(context, obj.optJSONObject("printer"));
                            boolean succeed = CommonUtil.savePrinterSetting(newPrinter, obj.optString("name"));
                            if (succeed) {
                                CommonUtil.pushSettingOrAddResult(context, obj.optString("manageToken"), obj.optString("name"), 0, newPrinter, obj.optString("requestId"), 1, "");   //上报设置成功，返回最新打印设置
                            } else {
                                CommonUtil.pushSettingOrAddResult(context, obj.optString("manageToken"), obj.optString("name"), 0, oldPrinter, obj.optString("requestId"), 0, "设置失败，可能是数据库写入出错！");   //设置失败，返回原来的打印设置
                            }
                        } else {
                            CommonUtil.pushSettingOrAddResult(context, obj.optString("manageToken"), obj.optString("name"), 0, oldPrinter, obj.optString("requestId"), 0, "设置失败，没有找到对应的打印机！");   //没有找到对应的打印机，设置失败，返回原来的打印设置
                        }
                    } else if ("addprinter".equals(contentType)) {    //添加打印机
                        Printer addPrinter = ParseUtils.parsePrinter(context, obj.optJSONObject("printer"));
                        SPUtil.putString(context, AppConstants.APP_XG_TOKEN, obj.optString("manageToken"));
                        if (null == CommonUtil.getSingleEntity(obj.optString("name"))) {
                            boolean succeed = CommonUtil.addNetWorkPrinter(addPrinter);
                            if (succeed) {
                                Log.d(TAG, "成功添加一台网络打印机！");
                                PrinterDevice entity = new PrinterDevice(addPrinter.getName(), addPrinter.getPrinterType(), addPrinter.getIp());
                                //FenghuoApplication.printerDeviceList.add(entity);
                                CommonUtil.pushSettingOrAddResult(context, obj.optString("manageToken"), obj.optString("name"), 1, addPrinter, obj.optString("requestId"), 1, "");   //上报添加成功，返回最新打印设置

                                List<PrinterDevice> deviceList = JSON.parseArray(SPUtil.getString(context, AppConstants.PRINTER_LIST), PrinterDevice.class);
                                Log.d(TAG, "添加前打印机列表的长度为：" + deviceList.size());
                                deviceList.add(entity);    //将网络打印机设备添加到集合
                                Log.d(TAG, "添加后打印机列表的长度为：" + deviceList.size());
                                SPUtil.putString(context, AppConstants.PRINTER_LIST, JSON.toJSONString(deviceList));
                            } else {
                                CommonUtil.pushSettingOrAddResult(context, obj.optString("manageToken"), obj.optString("name"), 1, addPrinter, obj.optString("requestId"), 0, "添加失败，可能是数据库写入出错！");   //上报添加失败，原样返回打印设置
                            }
                        } else {
                            CommonUtil.pushSettingOrAddResult(context, obj.optString("manageToken"), obj.optString("name"), 1, addPrinter, obj.optString("requestId"), 0, "添加失败，已存在该名称打印机！");   //已存在，添加失败，原样的打印设置
                        }
                    } else if ("delprinter".equals(contentType)) {    //删除打印机
                        SPUtil.putString(context, AppConstants.APP_XG_TOKEN, obj.optString("manageToken"));
                        if (null != CommonUtil.getSingleEntity(obj.optString("name"))) {
                            boolean succeed = CommonUtil.deleteNetPrinter(obj.optString("name"));
                            if (succeed) {
                                CommonUtil.pushDeleteResult(context, obj.optString("manageToken"), obj.optString("name"), obj.optString("requestId"), 1, "");  //删除成功

                                List<PrinterDevice> deviceList = JSON.parseArray(SPUtil.getString(context, AppConstants.PRINTER_LIST), PrinterDevice.class);
                                Log.d(TAG, "删除前打印机列表的长度为：" + deviceList.size());
                                for (int i = 0; i < deviceList.size(); i++) {
                                    if(deviceList.get(i).getName().equals(obj.optString("name"))){
                                        deviceList.remove(i);    //将网络打印机设备移除集合
                                    }
                                }
                                Log.d(TAG, "删除后打印机列表的长度为：" + deviceList.size());
                                SPUtil.putString(context, AppConstants.PRINTER_LIST, JSON.toJSONString(deviceList));
                            } else {
                                CommonUtil.pushDeleteResult(context, obj.optString("manageToken"), obj.optString("name"), obj.optString("requestId"), 0, "删除失败，可能是数据库写入出错！");  //删除失败
                            }
                        } else {
                            CommonUtil.pushDeleteResult(context, obj.optString("manageToken"), obj.optString("name"), obj.optString("requestId"), 0, "删除失败，没有找到对应的打印机！");  //删除失败
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
