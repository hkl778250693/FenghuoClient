package com.fenghks.business.business;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fenghks.business.AppConstants;
import com.fenghks.business.BaseActivity;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.bean.Printer;
import com.fenghks.business.bean.PrinterEntry;
import com.fenghks.business.tools.CommonPopupWindow;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.MapUtil;
import com.fenghks.business.utils.MyDBCallBack;
import com.fenghks.business.utils.MySqliteDBOpenhelper;
import com.fenghks.business.utils.SPUtil;
import com.fenghks.business.utils.VibratorUtil;
import com.fenghks.business.utils.WindowUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fenghuo on 2017/9/13.
 */

public class PrinterSettingActivity extends BaseActivity {
    @BindView(R.id.business_cardview)
    CardView businessCardview;
    @BindView(R.id.kitchen_cardview)
    CardView kitchenCardview;
    @BindView(R.id.customer_cardview)
    CardView customerCardview;
    @BindView(R.id.sender_cardview)
    CardView senderCardview;
    @BindView(R.id.is_qrcode)
    CheckBox isQrcode;
    @BindView(R.id.is_pause)
    CheckBox isPause;
    @BindView(R.id.loading)
    ImageView loading;
    @BindView(R.id.save_cardview)
    CardView saveCardview;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.type_tv)
    TextView typeTv;
    @BindView(R.id.business_count)
    TextView businessCount;
    @BindView(R.id.kitchen_count)
    TextView kitchenCount;
    @BindView(R.id.customer_count)
    TextView customerCount;
    @BindView(R.id.sender_count)
    TextView senderCount;
    @BindView(R.id.is_pause_cardview)
    CardView isPauseCardview;
    @BindView(R.id.ip_edit)
    EditText ipEdit;
    @BindView(R.id.coordinate_layout)
    CoordinatorLayout coordinateLayout;
    @BindView(R.id.ip_layout)
    LinearLayout ipLayout;

    private String getName;
    private int type, is_qrcode, is_pause;
    private Printer printer;
    private static final String TAG = "PrinterSettingActivity";
    private CommonPopupWindow window;
    private TextView titleTv, noPrint, oneCount, twoCount, threeCount, fourCount, fiveCount;
    private CardView cancel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_setting);
        ButterKnife.bind(this);
        defaultSettings();
    }

    private void defaultSettings() {
        Intent intent = getIntent();
        if (null != intent) {
            getName = intent.getStringExtra("name");
            type = intent.getIntExtra("type", 2);
            nameTv.setText(getName);
            if (type == 2) {
                typeTv.setText(R.string.usb_printer);
                ipLayout.setVisibility(View.GONE);
            } else {
                typeTv.setText(R.string.network_printer);
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                /** 查询打印机设置信息 */
                printer = MySqliteDBOpenhelper.querySingle(MyDBCallBack.TABLE_NAME_PRINTER, "select * from " +
                        MyDBCallBack.TABLE_NAME_PRINTER + " where " + PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{getName});
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != printer) {
                            businessCount.setText(String.valueOf(printer.getBusinessNum()));
                            kitchenCount.setText(String.valueOf(printer.getKitchenNum()));
                            customerCount.setText(String.valueOf(printer.getCustomerNum()));
                            senderCount.setText(String.valueOf(printer.getSenderNum()));
                            if (printer.getQrcode() == 1) {
                                isQrcode.setChecked(true);
                            } else {
                                isQrcode.setChecked(false);
                            }
                            if (printer.getPrinterType() == 2) {
                                if (printer.getIspause() == 0) {
                                    isPause.setChecked(false);
                                } else {
                                    isPause.setChecked(true);
                                }
                            }
                            ipEdit.setText(printer.getIp());
                        }
                    }
                });
            }
        }).start();

    }

    @OnClick({R.id.business_cardview, R.id.kitchen_cardview, R.id.customer_cardview, R.id.sender_cardview, R.id.save_cardview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.business_cardview:
                createChooseWindow("商家联", 1);
                break;
            case R.id.kitchen_cardview:
                createChooseWindow("后厨联", 2);
                break;
            case R.id.customer_cardview:
                createChooseWindow("顾客联", 3);
                break;
            case R.id.sender_cardview:
                createChooseWindow("配送联", 4);
                break;
            case R.id.save_cardview:
                //保存设置
                VibratorUtil.vibrateOnce(mActivity);
                saveCardview.setEnabled(false);
                loading.setVisibility(View.VISIBLE);
                WindowUtil.startFrameAnimation(loading);  //开始加载动画
                new Thread(new Runnable() {                 //测试
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            savePrinterSetting();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }

    /*// 保存打印机设置                //测试
    private void savePrinterSetting() {
        printer.setBusinessNum(Integer.parseInt(businessCount.getText().toString()));
        printer.setKitchenNum(Integer.parseInt(kitchenCount.getText().toString()));
        printer.setCustomerNum(Integer.parseInt(customerCount.getText().toString()));
        printer.setSenderNum(Integer.parseInt(senderCount.getText().toString()));
        if(isQrcode.isChecked()){
            is_qrcode = 1;
        }else {
            is_qrcode = 0;
        }
        printer.setQrcode(is_qrcode);
        if (type == 2) {   //只有USB打印机可以停用
            if(isPause.isChecked()){
                is_pause = 1;
            }else {
                is_pause = 0;
            }
            printer.setIspause(is_pause);
        }
        MySqliteDBOpenhelper.update(MyDBCallBack.TABLE_NAME_PRINTER, printer, PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{getName});
    }*/

    //保存打印机设置
    private void savePrinterSetting() {
        final Map<String, String> map = new HashMap<>();
        map.put("manageToken", FenghuoApplication.xg_token);
        final String uuid = UUID.randomUUID().toString();
        map.put("requestId", uuid);
        map.put("businessid", FenghuoApplication.currentShopID + "");   //店铺id（不能传商家id）
        if (type == 2) {
            map.put("ip", "0");
        } else {
            map.put("ip", ipEdit.getText().toString());
        }
        map.put("port", "9100");
        map.put("name", getName);
        map.put("businessNum", businessCount.getText().toString());
        map.put("kitchenNum", kitchenCount.getText().toString());
        map.put("customerNum", customerCount.getText().toString());
        map.put("senderNum", senderCount.getText().toString());
        if (isQrcode.isChecked()) {
            map.put("qrcode", "1");
        } else {
            map.put("qrcode", "0");
        }
        if (isPause.isChecked()) {
            map.put("ispause", "1");
        } else {
            map.put("ispause", "0");
        }
        x.http().post(CommonUtil.genGetParam(AppConstants.SET_CLOUD_PRINTER, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "savePrinterSetting=========onSuccess===="+result);
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.optInt("status") == 0) {  //消息发送成功
                        SPUtil.putBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, false);
                        MapUtil.saveRequestParams(mContext, map);
                        SPUtil.putString(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL, AppConstants.SET_CLOUD_PRINTER);
                        SPUtil.putInt(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL_TYPE, AppConstants.TYPE_SET_PRINTER);
                        SPUtil.putString(mContext, AppConstants.KEY_CURRENT_UUID, uuid);
                        printer.setStatus(AppConstants.STATUS_SETTING);
                        MySqliteDBOpenhelper.update(MyDBCallBack.TABLE_NAME_PRINTER, printer, PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{getName});
                        setResult(RESULT_OK, null);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "savePrinterSetting=========onError");
                saveCardview.setEnabled(true);
                loading.setVisibility(View.GONE);
                Snackbar.make(coordinateLayout, R.string.network_error, Snackbar.LENGTH_SHORT).show();  //网络错误
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "savePrinterSetting=========onCancelled");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "savePrinterSetting=========onFinished");
                setResult(RESULT_OK, null);
                finish();
            }
        });
    }

    /**
     * 弹出选择打印联数窗口
     */
    private void createChooseWindow(String title, final int flag) {
        window = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popupwindow_choose_print_count)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.AnimUp)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        //绑定控件ID
                        titleTv = (TextView) view.findViewById(R.id.title);
                        noPrint = (TextView) view.findViewById(R.id.no_print);
                        oneCount = (TextView) view.findViewById(R.id.one_count);
                        twoCount = (TextView) view.findViewById(R.id.two_count);
                        threeCount = (TextView) view.findViewById(R.id.three_count);
                        fourCount = (TextView) view.findViewById(R.id.four_count);
                        fiveCount = (TextView) view.findViewById(R.id.five_count);
                        cancel = (CardView) view.findViewById(R.id.cancle);
                    }
                })
                .setOutsideTouchable(true)
                .create();
        window.showAtLocation(coordinateLayout, Gravity.BOTTOM, 0, 0);
        titleTv.setText(title);
        //设置点击事件
        noPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                modifyValue(flag, "0");
            }
        });
        oneCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                modifyValue(flag, "1");
            }
        });
        twoCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                modifyValue(flag, "2");
            }
        });
        threeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                modifyValue(flag, "3");
            }
        });
        fourCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                modifyValue(flag, "4");
            }
        });
        fiveCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                modifyValue(flag, "5");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
    }

    //给控件赋值
    private void modifyValue(int flag, String count) {
        if (flag == 1) {
            businessCount.setText(count);
        } else if (flag == 2) {
            kitchenCount.setText(count);
        } else if (flag == 3) {
            customerCount.setText(count);
        } else if (flag == 4) {
            senderCount.setText(count);
        }
    }
}
