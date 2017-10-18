package com.fenghks.business.business;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fenghks.business.AppConstants;
import com.fenghks.business.BaseActivity;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
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
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by fenghuo on 2017/9/15.
 */

public class AddPrinterActivity extends BaseActivity {
    @BindView(R.id.tl_toolbar)
    Toolbar tlToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @BindView(R.id.name_edit)
    EditText nameEdit;
    @BindView(R.id.ip_edit)
    EditText ipEdit;
    @BindView(R.id.loading)
    ImageView loading;
    @BindView(R.id.finish_cardview)
    CardView finishCardview;
    @BindView(R.id.coordinate_layout)
    CoordinatorLayout coordinateLayout;
    @BindView(R.id.root_layout)
    LinearLayout rootLayout;
    @BindView(R.id.business_count)
    TextView businessCount;
    @BindView(R.id.business_cardview)
    CardView businessCardview;
    @BindView(R.id.kitchen_count)
    TextView kitchenCount;
    @BindView(R.id.kitchen_cardview)
    CardView kitchenCardview;
    @BindView(R.id.customer_count)
    TextView customerCount;
    @BindView(R.id.customer_cardview)
    CardView customerCardview;
    @BindView(R.id.sender_count)
    TextView senderCount;
    @BindView(R.id.sender_cardview)
    CardView senderCardview;
    @BindView(R.id.is_qrcode)
    CheckBox isQrcode;

    private static final String TAG = "AddPrinterActivity";
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    private CommonPopupWindow window;
    private TextView titleTv, noPrint, oneCount, twoCount, threeCount, fourCount, fiveCount;
    private CardView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_printer);
        ButterKnife.bind(this);

        businessCount.setText("1");
        isQrcode.setChecked(true);
        clickListener();
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }

    private void clickListener() {
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != MySqliteDBOpenhelper.querySingle(MyDBCallBack.TABLE_NAME_PRINTER, "select * from " +
                                MyDBCallBack.TABLE_NAME_PRINTER + " where " + PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{nameEdit.getText().toString()})) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(coordinateLayout, R.string.device_already_existed, Snackbar.LENGTH_SHORT).show();  //提示名称已被使用
                                }
                            });
                        }
                    }
                }).start();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @OnClick({R.id.business_cardview, R.id.kitchen_cardview, R.id.customer_cardview, R.id.sender_cardview, R.id.finish_cardview})
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
            case R.id.finish_cardview:
                //完成添加
                VibratorUtil.vibrateOnce(mActivity);
                if (!"".equals(nameEdit.getText().toString())) {
                    if (!"".equals(ipEdit.getText().toString())) {
                        finishCardview.setEnabled(false);
                        loading.setVisibility(View.VISIBLE);
                        WindowUtil.startFrameAnimation(loading);  //开始加载动画
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1500);
                                    addNetWorkPrinter();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        /**
                         * 注释的是测试代码
                         */
                           /* new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                        addNetWorkPrinter();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, "正在添加打印机至云接单设备，请稍后...", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent();
                                                intent.putExtra("new_printer",printer);
                                                setResult(RESULT_OK,intent);
                                                finish();
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();*/
                    } else {
                        Snackbar.make(coordinateLayout, R.string.null_printer_ip_tip, Snackbar.LENGTH_SHORT).show();  //IP为空
                    }
                } else {
                    Snackbar.make(coordinateLayout, R.string.null_printer_name_tip, Snackbar.LENGTH_SHORT).show();  //名称为空
                }
                break;
        }
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
        window.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);
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

    /*//添加网络打印机
    private void addNetWorkPrinter() {
        printer = new Printer(FenghuoApplication.currentShopID,ipEdit.getText().toString(), Integer.parseInt(portEdit.getText().toString()), nameEdit.getText().toString(), 1, 0, 0, 0, 1, FenghuoApplication.xg_token, 0, 1,2);
        MySqliteDBOpenhelper.insert(MyDBCallBack.TABLE_NAME_PRINTER, printer);
    }*/

    //添加网络打印机至云接单设备
    private void addNetWorkPrinter() {
        final Map<String, String> map = new HashMap<>();
        map.put("manageToken", FenghuoApplication.xg_token);
        final String uuid = UUID.randomUUID().toString();
        map.put("requestId", uuid);
        map.put("businessid", FenghuoApplication.currentShopID + "");   //店铺id（不能传商家id）
        map.put("ip", ipEdit.getText().toString());
        map.put("port", "9100");
        map.put("name", nameEdit.getText().toString());
        map.put("businessNum", businessCount.getText().toString());
        map.put("kitchenNum", kitchenCount.getText().toString());
        map.put("customerNum", customerCount.getText().toString());
        map.put("senderNum", senderCount.getText().toString());
        if (isQrcode.isChecked()) {
            map.put("qrcode", "1");
        } else {
            map.put("qrcode", "0");
        }
        x.http().post(CommonUtil.genGetParam(AppConstants.ADD_NETWORK_PRINTER, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.optInt("status") == 0) {  //消息发送成功
                        SPUtil.putBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, false);
                        MapUtil.saveRequestParams(mContext, map);
                        SPUtil.putString(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL, AppConstants.ADD_NETWORK_PRINTER);
                        SPUtil.putInt(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL_TYPE, AppConstants.TYPE_ADD_PRINTER);
                        SPUtil.putString(mContext, AppConstants.KEY_CURRENT_UUID, uuid);
                        setResult(RESULT_OK, null);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "requestPrinterList=========onError");
                finishCardview.setEnabled(true);
                loading.setVisibility(View.GONE);
                Snackbar.make(coordinateLayout, R.string.network_error, Snackbar.LENGTH_SHORT).show();  //网络错误
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "requestPrinterList=========onCancelled");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "requestPrinterList=========onFinished");
                setResult(RESULT_OK, null);
                finish();
            }
        });
    }
}
