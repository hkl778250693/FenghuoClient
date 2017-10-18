package com.fenghks.business.business;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fenghks.business.AppConstants;
import com.fenghks.business.BaseActivity;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.adpter.PrintersListAdapter;
import com.fenghks.business.bean.Printer;
import com.fenghks.business.bean.PrinterEntry;
import com.fenghks.business.print.params.Constant;
import com.fenghks.business.receiver.UDPBroadcastUtil;
import com.fenghks.business.tools.CommonPopupWindow;
import com.fenghks.business.tools.DividerItemDecoration;
import com.fenghks.business.tools.DragFloatActionButton;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.MapUtil;
import com.fenghks.business.utils.MyAnimationUtils;
import com.fenghks.business.utils.MyDBCallBack;
import com.fenghks.business.utils.MySqliteDBOpenhelper;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.SPUtil;
import com.fenghks.business.utils.VibratorUtil;
import com.fenghks.business.utils.WindowUtil;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by fenghuo on 2017/9/11.
 */

public class PrintersManageActivity extends BaseActivity {
    @BindView(R.id.tl_toolbar)
    Toolbar tlToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @BindView(R.id.rv_printers)
    RecyclerView rvPrinters;
    @BindView(R.id.coordinate_layout)
    CoordinatorLayout coordinateLayout;
    /**
     * 下拉刷新
     */
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.refresh_cardview)
    CardView refreshCardview;
    @BindView(R.id.add_printer)
    DragFloatActionButton addPrinter;
    @BindView(R.id.loading)
    ImageView loadingRefresh;
    @BindView(R.id.more_setting)
    ImageView moreSetting;
    @BindView(R.id.no_printer)
    TextView noPrinter;

    private PrintersListAdapter adapter;
    private Unbinder unbinder;
    private boolean flag = true;
    private static final String TAG = "PrintersManageActivity";
    private ImageButton close;
    private TextView stepTips;
    private TextView stepName;
    private CardView stepCardview;
    private ImageView loading;
    private List<Printer> printerList = new ArrayList<>();
    private Printer printer;
    private static final int ADD_PRINTER_REQUEST_CODE = 1000;
    public static int positionRequestCode = 0;
    private RadioButton confirm, cancel;
    private CommonPopupWindow window;
    private boolean isRegister = false;   //注册激活广播是否注册
    private ImageView loadAnimation;
    private TextView unActivate,unRegister, delOrPauTitle, delOrPauText;
    private RelativeLayout deleteOrPause;
    private CardView delOrPauCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printers_manage);
        unbinder = ButterKnife.bind(this);
        defaultSettings();
        clickListener();
        /**
         * 插入演示数据
         */
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                insertDemoData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshCardview.setEnabled(true);
                    }
                });
            }
        }).start();*/
        //注册接收相应信息的广播
        rgReceiver();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            if (flag) {
                showLoadingWindow(getResources().getString(R.string.loading_get_state_tips)); //加载动画
                checkShopState();  //检测店铺初始化以及激活状态
            /*//判断是否初始化或是否激活  默认未初始化、未激活
            if (SPUtil.getBoolean(mContext, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_INITIALIZE, false)) {
                if (SPUtil.getBoolean(mContext, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_ACTIVATED, false)) {
                    refreshPrinterList();//刷新打印机列表
                } else {
                    showPopupwindow(1);  //已注册，未激活
                }
            } else {
                showPopupwindow(0);   //未注册，未激活
            }*/
                flag = false;
            }
        }
    }

    private void defaultSettings() {
        moreSetting.setVisibility(View.VISIBLE);
        /**
         * 设置 下拉刷新 Header 和 footer 风格样式
         */
        //mRefreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
        //mRefreshLayout.setRefreshHeader(new DeliveryHeader(this));
        //mRefreshLayout.setRefreshHeader(new CircleHeader(this));
        //mRefreshLayout.setRefreshHeader(new DropboxHeader(this));
        //mRefreshLayout.setRefreshHeader(new FunGameHeader(this));
        //mRefreshLayout.setRefreshHeader(new FalsifyHeader(this));
        //mRefreshLayout.setRefreshHeader(new PhoenixHeader(this));
        refreshLayout.setRefreshHeader(new WaterDropHeader(this));
        refreshLayout.setPrimaryColorsId(R.color.ltgray, android.R.color.white);
        //mRefreshLayout.setRefreshHeader(new WaveSwipeHeader(this));
        //mRefreshLayout.setRefreshHeader(new TaurusHeader(this));
        //mRefreshLayout.setRefreshHeader(new StoreHouseHeader(this));
        //mRefreshLayout.setRefreshHeader(new FunGameHitBlockHeader(this));
        //mRefreshLayout.setRefreshHeader(new FunGameBattleCityHeader(this));
        //mRefreshLayout.setRefreshHeader(new FlyRefreshHeader(this));
        refreshLayout.setEnableRefresh(false);  //关闭下拉加载功能
        refreshLayout.setEnableLoadmore(false);  //关闭上拉加载功能
        refreshLayout.setDisableContentWhenRefresh(true);  //是否在刷新的时候禁止内容的一切手势操作（默认false）
        //refreshLayout.autoRefresh();  //进入自动刷新
    }

    @OnClick({R.id.refresh_cardview, R.id.add_printer, R.id.more_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.refresh_cardview:
                VibratorUtil.vibrateOnce(mActivity);
                if (SPUtil.getBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, true)) {   //上一次操作完成，执行当前操作，默认上次操作完成
                    //获取打印机列表
                    requestPrinterList();  //真实数据
                    //refreshPrinterList();   //测试数据
                } else {
                    tipPopupWindow(); //上一次操作未完成提示框
                }
                break;
            case R.id.add_printer:
                VibratorUtil.vibrateOnce(mContext);
                if (SPUtil.getBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, true)) {   //上一次操作完成，执行当前操作，默认上次操作完成
                    //手动添加网络打印机
                    Intent intent = new Intent(mActivity, AddPrinterActivity.class);
                    startActivityForResult(intent, ADD_PRINTER_REQUEST_CODE);
                } else {
                    tipPopupWindow(); //上一次操作未完成提示框
                }
                break;
            case R.id.more_setting:
                moreSettingWindow();
                break;
        }
    }

    private void clickListener() {
        refreshCardview.setEnabled(false);
        /**
         * 下拉刷新监听
         */
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(1000);
                //获取云打印设备所管理的打印机
                requestPrinterList();
            }
        });
    }

    /**
     * 检测店铺初始化状态以及激活状态
     */
    private void checkShopState() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    Map<String, String> map = new HashMap<>();
                    map.put("businessid", FenghuoApplication.currentShopID + "");   //店铺id（不能传商家id）
                    map.put("requestId", UUID.randomUUID().toString());
                    x.http().post(CommonUtil.genGetParam(AppConstants.OBTAIN_DEVICE_STATE, map), new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "当前店铺的状态为：" + result);
                            JSONObject obj = ParseUtils.parseDataString(mContext,result);
                            if (null != obj) {
                                if (obj.optInt("businessid") == FenghuoApplication.currentShopID) {
                                    window.dismiss();
                                    if (obj.optInt("isbund") == 1) {   //设备（绑定）注册成功
                                        SPUtil.putBoolean(mContext, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_INITIALIZE, true);
                                        if (obj.optInt("isactive") == 1) {   //设备已激活
                                            SPUtil.putBoolean(mContext, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_ACTIVATED, true);
                                            refreshPrinterList(0);//刷新打印机列表
                                        } else {
                                            SPUtil.putBoolean(mContext, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_ACTIVATED, false);
                                            showPopupwindow(1);
                                        }
                                    } else {
                                        SPUtil.putBoolean(mContext, "shop" + FenghuoApplication.currentShopID, AppConstants.IS_INITIALIZE, false);
                                        showPopupwindow(0);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Snackbar.make(coordinateLayout, R.string.network_error, Snackbar.LENGTH_SHORT).show();  //提示网络错误
                            window.dismiss();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                        }

                        @Override
                        public void onFinished() {
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 弹出更多设置框
     */
    private void moreSettingWindow() {
        window = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popupwindow_more_setting)
                .setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, moreSetting.getHeight())
                .setAnimationStyle(R.style.AnimRight)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        unActivate = (TextView) view.findViewById(R.id.un_acticate_tv);
                        unRegister = (TextView) view.findViewById(R.id.un_register_tv);
                    }
                })
                .setOutsideTouchable(true)
                .create();
        window.showAsDropDown(moreSetting, -window.getWidth(), -moreSetting.getHeight());
        unActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                unActivateTipWindow();
            }
        });
        unRegister.setOnClickListener(new View.OnClickListener() {   //解绑云设备
            @Override
            public void onClick(View v) {
                window.dismiss();
                unRegisterTipWindow();
            }
        });
    }

    /**
     * 反激活提示框
     */
    private void unActivateTipWindow() {
        window = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popupwindow_custom_tips)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .setAnimationStyle(R.style.AnimDown)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView tips = (TextView) view.findViewById(R.id.tips);
                        title.setText(R.string.window_title);
                        tips.setText(R.string.is_sure_unactivate_tips);
                        confirm = (RadioButton) view.findViewById(R.id.save_rb);
                        cancel = (RadioButton) view.findViewById(R.id.cancel_rb);
                    }
                })
                .setOutsideTouchable(false)
                .create();
        window.showAtLocation(coordinateLayout, Gravity.CENTER, 0, 0);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPUtil.getBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, true)) {   //上一次操作完成，执行当前操作，默认上次操作完成
                    //调用反激活接口
                    window.dismiss();
                    showLoadingWindow(getResources().getString(R.string.loading_unactivate_tips));
                    requestActivateOrNot("0");
                } else {
                    window.dismiss();
                    tipPopupWindow(); //上一次操作未完成提示框
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
    }

    /**
     * 解绑提示框
     */
    private void unRegisterTipWindow() {
        window = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popupwindow_custom_tips)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .setAnimationStyle(R.style.AnimDown)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView tips = (TextView) view.findViewById(R.id.tips);
                        title.setText(R.string.window_title);
                        tips.setText(R.string.is_sure_unregister_tips);
                        confirm = (RadioButton) view.findViewById(R.id.save_rb);
                        cancel = (RadioButton) view.findViewById(R.id.cancel_rb);
                    }
                })
                .setOutsideTouchable(false)
                .create();
        window.showAtLocation(coordinateLayout, Gravity.CENTER, 0, 0);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPUtil.getBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, true)) {   //上一次操作完成，执行当前操作，默认上次操作完成
                    window.dismiss();
                    showLoadingWindow(getResources().getString(R.string.loading_unregister_tips));
                    UnRegisterDevice();
                } else {
                    window.dismiss();
                    tipPopupWindow(); //上一次操作未完成提示框
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
    }


    /**
     * 获取数据刷新列表
     */
    private void refreshPrinterList(int flag) {
        if(flag == 0){
            showLoadingWindow(getResources().getString(R.string.loading_local_printers_tips)); //刷新本地打印机列表
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    getLocalPrinters();           //获取本地打印机列表数据
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshCardview.setEnabled(true);
                            loadingRefresh.setVisibility(View.GONE);
                            window.dismiss();
                            if (printerList.size() != 0) {
                                noPrinter.setVisibility(View.GONE);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
                                //设置布局管理器
                                rvPrinters.setLayoutManager(layoutManager);
                                //设置为垂直布局，这也是默认的
                                layoutManager.setOrientation(OrientationHelper.VERTICAL);
                                adapter = new PrintersListAdapter(PrintersManageActivity.this, printerList);
                                rvPrinters.setAdapter(adapter);
                                //设置增加或删除条目的动画
                                rvPrinters.setItemAnimator(new DefaultItemAnimator());
                                //设置分隔线
                                rvPrinters.addItemDecoration(new DividerItemDecoration(mActivity, 5));
                                MyAnimationUtils.viewGroupOutAlphaAnimation(mActivity, rvPrinters, 0.1F);
                                OverScrollDecoratorHelper.setUpOverScroll(rvPrinters,OrientationHelper.HORIZONTAL);
                                //adapter.notifyItemRangeInserted(0, printerList.size());  //刷新打印机列表,伴有动画效果
                                //Snackbar.make(coordinateLayout, R.string.add_printer_suceessful, Snackbar.LENGTH_SHORT).show();
                                //打印机列表点击事件
                                adapter.setOnSettingClickListener(new PrintersListAdapter.OnSettingClickListener() {
                                    @Override
                                    public void onClick(int position) {
                                        positionRequestCode = position;
                                        VibratorUtil.vibrateOnce(mContext);
                                        if (SPUtil.getBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, true)) {   //上一次操作完成，执行当前操作，默认上次操作完成
                                            printer = printerList.get(positionRequestCode);
                                            Intent intent = new Intent(mContext, PrinterSettingActivity.class);
                                            intent.putExtra("name", printer.getName());
                                            intent.putExtra("type", printer.getPrinterType());
                                            mActivity.startActivityForResult(intent, positionRequestCode);
                                        } else {
                                            tipPopupWindow(); //上一次操作未完成提示框
                                        }
                                    }
                                });
                                adapter.setOnPauseClickListener(new PrintersListAdapter.OnPauseClickListener() {
                                    @Override
                                    public void onClick(int position) {
                                        positionRequestCode = position;
                                        VibratorUtil.vibrateOnce(mContext);
                                        if (SPUtil.getBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, true)) {   //上一次操作完成，执行当前操作，默认上次操作完成
                                            printer = printerList.get(positionRequestCode);
                                            createDeleteOrPauseWindow(printer.getName(), AppConstants.FLAG_PAUSE);
                                        } else {
                                            tipPopupWindow(); //上一次操作未完成提示框
                                        }
                                    }
                                });
                                adapter.setOnDeleteClickListener(new PrintersListAdapter.OnDeleteClickListener() {
                                    @Override
                                    public void onClick(int position) {
                                        positionRequestCode = position;
                                        VibratorUtil.vibrateOnce(mContext);
                                        if (SPUtil.getBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, true)) {   //上一次操作完成，执行当前操作，默认上次操作完成
                                            printer = printerList.get(positionRequestCode);
                                            createDeleteOrPauseWindow(printer.getName(), AppConstants.FLAG_DELETE);
                                        } else {
                                            tipPopupWindow(); //上一次操作未完成提示框
                                        }
                                    }
                                });
                            } else {
                                noPrinter.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 打印机列表演示数据
     */
    private void insertDemoData() {
        /** 暂时添加演示数据到本地数据库 */
        //清空本地打印机表
        MySqliteDBOpenhelper.deleteFrom(MyDBCallBack.TABLE_NAME_PRINTER);
                /*设置id从1开始（sqlite默认id从1开始），若没有这一句，id将会延续删除之前的id
                db.execSQL("update sqlite_sequence set seq=0 where name='表名'");*/
        List<Printer> list = new ArrayList<Printer>();
        for (int i = 0; i < 10; i++) {
            Printer printer;
            if (i == 3 || i == 5 || i == 9) {
                printer = new Printer(FenghuoApplication.currentShopID, "192.168.1.10" + i, Constant.DEFAULT_PORT, "网络打印机" + i, 1, 0, 0, 0, 1, 0, 1, 0);
            } else {
                printer = new Printer(FenghuoApplication.currentShopID, "0", Constant.DEFAULT_PORT, "USB打印机" + i, 1, 0, 0, 0, 1, 0, 2, 0);
            }
            list.add(printer);
        }
        Log.d(TAG, "插入打印机个数为：" + list.size());
        MySqliteDBOpenhelper.insert(MyDBCallBack.TABLE_NAME_PRINTER, list);
    }

    /**
     * 查询保存到本地的打印机列表
     */
    public void getLocalPrinters() {
        printerList.clear();
        printerList = MySqliteDBOpenhelper.queryList(MyDBCallBack.TABLE_NAME_PRINTER, "select * from " +
                MyDBCallBack.TABLE_NAME_PRINTER + " where " + PrinterEntry.COLUMN_NAME_SHOP_ID + "=?", new String[]{String.valueOf(FenghuoApplication.currentShopID)});
        Log.d(TAG, "打印机列表长度为：" + printerList.size());
    }

    /**
     * 弹出提示框
     */
    private void tipPopupWindow() {
        final CommonPopupWindow window = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popupwindow_custom_tips)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .setAnimationStyle(R.style.AnimDown)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView tips = (TextView) view.findViewById(R.id.tips);
                        title.setText(R.string.window_title);
                        tips.setText(R.string.window_tips);
                        confirm = (RadioButton) view.findViewById(R.id.save_rb);
                        cancel = (RadioButton) view.findViewById(R.id.cancel_rb);
                    }
                })
                .setOutsideTouchable(false)
                .create();
        window.showAtLocation(coordinateLayout, Gravity.CENTER, 0, 0);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                showLoadingWindow(getResources().getString(R.string.loading_repeat_tips)); //正在重试
                final Map<String, String> map = MapUtil.getRequestParams(mContext);
                final String uuid = UUID.randomUUID().toString();
                map.put("requestId", uuid);
                final String requestURL = SPUtil.getString(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL);
                x.http().post(CommonUtil.genGetParam(requestURL, map), new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            Log.d(TAG, "重试上次的类型为：" + obj.optInt("status") + "，返回结果为：" + result);
                            if (SPUtil.getInt(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL_TYPE) == AppConstants.TYPE_OBTAIN_PRINTERS) {
                                if (obj.optInt("status") == 0) {  //获取列表类型
                                    SPUtil.putBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, false);
                                    MapUtil.saveRequestParams(mContext, map);
                                    SPUtil.putString(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL, requestURL);
                                    SPUtil.putInt(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL_TYPE, AppConstants.TYPE_OBTAIN_PRINTERS);
                                    SPUtil.putString(mContext, AppConstants.KEY_CURRENT_UUID, uuid);
                                }
                            } else if (SPUtil.getInt(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL_TYPE) == AppConstants.TYPE_ADD_PRINTER) {
                                if (obj.optInt("status") == 0) {  //添加打印机类型
                                    Snackbar.make(coordinateLayout, R.string.repeat_adding_printer_tips, Snackbar.LENGTH_LONG).show();
                                    SPUtil.putBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, false);
                                    MapUtil.saveRequestParams(mContext, map);
                                    SPUtil.putString(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL, requestURL);
                                    SPUtil.putInt(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL_TYPE, AppConstants.TYPE_ADD_PRINTER);
                                    SPUtil.putString(mContext, AppConstants.KEY_CURRENT_UUID, uuid);
                                }
                            } else if (SPUtil.getInt(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL_TYPE) == AppConstants.TYPE_DELETE_PRINTER) {
                                if (obj.optInt("status") == 0) {  //删除打印机类型
                                    Snackbar.make(coordinateLayout, R.string.repeat_delete_printer_tips, Snackbar.LENGTH_LONG).show();
                                    SPUtil.putBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, false);
                                    MapUtil.saveRequestParams(mContext, map);
                                    SPUtil.putString(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL, requestURL);
                                    SPUtil.putInt(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL_TYPE, AppConstants.TYPE_DELETE_PRINTER);
                                    SPUtil.putString(mContext, AppConstants.KEY_CURRENT_UUID, uuid);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.d(TAG, "requestPrinterList=========onError");
                        Snackbar.make(coordinateLayout, R.string.network_error, Snackbar.LENGTH_SHORT).show();  //提示网络错误
                        window.dismiss();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Log.d(TAG, "requestPrinterList=========onCancelled");
                    }

                    @Override
                    public void onFinished() {
                        Log.d(TAG, "requestPrinterList=========onFinished");
                    }
                });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
    }


    /**
     * 弹出加载框
     */
    private void showLoadingWindow(final String tipWords) {
        window = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popupwindow_loading)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .setAnimationStyle(R.style.window_down_style)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        loadAnimation = (ImageView) view.findViewById(R.id.frame_animation);
                        TextView tips = (TextView) view.findViewById(R.id.tips);
                        tips.setText(tipWords);
                    }
                })
                .setOutsideTouchable(false)
                .create();
        window.showAtLocation(coordinateLayout, Gravity.CENTER, 0, 0);
        WindowUtil.startFrameAnimation(loadAnimation);  //开始加载动画
    }

    /**
     * 弹出操作提示步骤
     *
     * @param flag 0：代表设备未注册，未激活       1：代表设备已注册（初始化），未激活
     */
    public void showPopupwindow(int flag) {
        window = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popupwindow_printer_manage_steps)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .setAnimationStyle(R.style.AnimDown)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        //绑定控件
                        close = (ImageButton) view.findViewById(R.id.close);
                        stepTips = (TextView) view.findViewById(R.id.step_tips);
                        stepName = (TextView) view.findViewById(R.id.step_name);
                        loading = (ImageView) view.findViewById(R.id.loading);
                        stepCardview = (CardView) view.findViewById(R.id.step_cardview);
                    }
                })
                .setOutsideTouchable(false)
                .create();
        window.showAtLocation(coordinateLayout, Gravity.CENTER, 0, 0);
        Log.d(TAG, "初始化状态：" + SPUtil.getBoolean(mContext, AppConstants.IS_INITIALIZE, false));
        if (flag == 0) {    //未注册，未激活
            stepTips.setText(R.string.initialize_tips);
            stepName.setText(R.string.initialize);
        } else {   //已注册，未激活
            stepTips.setText(R.string.activate_tips);
            stepName.setText(R.string.activate_device);
        }
        //点击事件
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mActivity);
                window.dismiss();
                finish();
            }
        });
        stepCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mActivity);
                stepCardview.setEnabled(false);
                loading.setVisibility(View.VISIBLE);
                WindowUtil.startFrameAnimation(loading);
                if ("初始化".equals(stepName.getText().toString())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                /*Intent initializeIntent = new Intent(AppConstants.ACTION_INITIALIZE_RESULT);
                                sendBroadcast(initializeIntent);*/
                                //发送UDP广播将店铺ID和信鸽返回的token传给云打印设备注册
                                UDPBroadcastUtil.sendUDPBroadcast(mActivity, FenghuoApplication.currentShopID + "," + FenghuoApplication.xg_token);
                                /**
                                 * 如果传输的是中文，切记：UTF-8传输时是按三个字节传输的，但是在计算字符串长度时，则是按照一个符号为1长度来计算的
                                 * 所以接收到接收到的字符串会被截断一半
                                 * 解决办法：看你使用的工具包是否有关于非英文字符传输的帮助，如果没有的话，最简单的方法是在传输中文字符时，自己在后面加上一堆无用字符即可。
                                 */
                                //UDPBroadcastUtil.sendUDPBroadcast(OrderActivity.this, "我是测试数据*");
                                Log.d(TAG, "开启广播，开始发送数据");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                //调用激活接口
                                requestActivateOrNot("1");
                                /*Intent activateIntent = new Intent(AppConstants.ACTION_ACTIVATE_RESULT);
                                sendBroadcast(activateIntent);*/
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
    }

    //调用激活接口
    private void requestActivateOrNot(String type) {
        Map<String, String> map = new HashMap();
        final String uuid = UUID.randomUUID().toString();
        map.put("manageToken", FenghuoApplication.xg_token);
        map.put("requestId", uuid);  //以UUID作为唯一标识，来区分推送结果
        Log.d(TAG, "激活的UUID为" + uuid);
        map.put("businessid", FenghuoApplication.currentShopID + "");
        map.put("activate", type);
        x.http().post(CommonUtil.genGetParam(AppConstants.ACTIVATE_DEVICE, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "反激活成功！返回的结果是：" + result);
                SPUtil.putString(mContext, AppConstants.KEY_CURRENT_UUID, uuid);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "激活失败！错误是：" + ex);
                Snackbar.make(coordinateLayout, R.string.network_error, Snackbar.LENGTH_SHORT).show();  //提示网络错误
                window.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "激活取消！异常是：" + cex);
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "激活完成！");
            }
        });
    }

    //调用解绑设备接口
    private void UnRegisterDevice() {
        Map<String, String> map = new HashMap();
        final String uuid = UUID.randomUUID().toString();
        map.put("manageToken", FenghuoApplication.xg_token);
        map.put("requestId", uuid);  //以UUID作为唯一标识，来区分推送结果
        Log.d(TAG, "激活的UUID为" + uuid);
        map.put("businessid", FenghuoApplication.currentShopID + "");
        x.http().post(CommonUtil.genGetParam(AppConstants.UNREGISTER_DEVICE, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "解绑成功！返回的结果是：" + result);
                SPUtil.putString(mContext, AppConstants.KEY_CURRENT_UUID, uuid);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "解绑失败！错误是：" + ex);
                Snackbar.make(coordinateLayout, R.string.network_error, Snackbar.LENGTH_SHORT).show();  //提示网络错误
                window.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "解绑取消！异常是：" + cex);
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "解绑完成！");
            }
        });
    }

    //获取打印机列表
    private void requestPrinterList() {
        showLoadingWindow(getResources().getString(R.string.loading_printers_tips)); //获取打印机列表
        final Map<String, String> map = new HashMap<>();
        map.put("manageToken", FenghuoApplication.xg_token);
        final String uuid = UUID.randomUUID().toString();
        map.put("requestId", uuid);
        map.put("businessid", FenghuoApplication.currentShopID + "");   //店铺id（不能传商家id）
        x.http().post(CommonUtil.genGetParam(AppConstants.OBTAIN_CLOUD_PRINTER_LIST, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "获取打印机列表的后台返回结果为：" + result);
                JSONObject obj = ParseUtils.parseDataString(mContext,result);
                if (null != obj) {  //消息发送成功
                    SPUtil.putBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, false);
                    MapUtil.saveRequestParams(mContext, map);
                    SPUtil.putString(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL, AppConstants.OBTAIN_CLOUD_PRINTER_LIST);
                    SPUtil.putInt(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL_TYPE, AppConstants.TYPE_OBTAIN_PRINTERS);
                    SPUtil.putString(mContext, AppConstants.KEY_CURRENT_UUID, uuid);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "requestPrinterList=========onError");
                Snackbar.make(coordinateLayout, R.string.network_error, Snackbar.LENGTH_SHORT).show();  //提示网络错误
                window.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "requestPrinterList=========onCancelled");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "requestPrinterList=========onFinished");
            }
        });
    }

    //初始化接收广播
    BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case AppConstants.ACTION_INITIALIZE_RESULT:
                    if (intent.getBooleanExtra(AppConstants.IS_INITIALIZE, true)) {
                        Snackbar.make(coordinateLayout, R.string.initialize_succeed, Snackbar.LENGTH_SHORT).show();  //提示初始化成功
                        stepCardview.setEnabled(true);
                        loading.setVisibility(View.GONE);
                        stepTips.setText(R.string.activate_tips);
                        stepName.setText(R.string.activate_device);
                    } else {
                        //提示用户初始化失败
                        Snackbar.make(coordinateLayout, R.string.initialize_failure, Snackbar.LENGTH_SHORT).show();  //提示初始化失败
                        stepCardview.setEnabled(true);
                        loading.setVisibility(View.GONE);
                    }
                    break;
                case AppConstants.ACTION_ACTIVATE_RESULT:
                    if (intent.getBooleanExtra(AppConstants.IS_ACTIVATED, true)) {
                        Log.d(TAG, "激活成功！");
                        stepCardview.setEnabled(true);
                        loading.setVisibility(View.GONE);
                        Snackbar.make(coordinateLayout, R.string.activate_succeed, Snackbar.LENGTH_SHORT).show();  //提示激活成功
                        window.dismiss();
                        refreshPrinterList(0); //刷新打印机列表
                    } else {
                        Snackbar.make(coordinateLayout, R.string.activate_failure, Snackbar.LENGTH_SHORT).show();  //提示激活失败
                        stepCardview.setEnabled(true);
                        loading.setVisibility(View.GONE);
                    }
                    break;
                case AppConstants.ACTION_OBTAIN_PRINTERS_RESULT:
                    if (intent.getBooleanExtra(AppConstants.IS_GET_PEINTERS, true)) {
                        window.dismiss();
                        //重新读取本地数据，刷新打印机列表
                        refreshPrinterList(1);
                        Snackbar.make(coordinateLayout, R.string.printer_list_refresh_succeed, Snackbar.LENGTH_SHORT).show();
                    } else {
                        //提示用户获取列表为空
                        refreshCardview.setEnabled(true);
                        loadingRefresh.setVisibility(View.GONE);
                        window.dismiss();
                        Snackbar.make(coordinateLayout, R.string.printer_list_refresh_failure, Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                case AppConstants.ACTION_SETTING_PRINTER_RESULT:
                    if (intent.getBooleanExtra(AppConstants.IS_SETTING_PRINTER, true)) {
                        //更新设置状态
                        //printer.setStatus(AppConstants.STATUS_NORMAL);
                        adapter.notifyItemChanged(positionRequestCode, AppConstants.STATUS_NORMAL);
                        Snackbar.make(coordinateLayout, R.string.printer_setting_succeed, Snackbar.LENGTH_SHORT).show();

                    } else {
                        //更新设置状态，并提示用户设置失败
                        //printer.setStatus(AppConstants.STATUS_SET_FAILURE);
                        adapter.notifyItemChanged(positionRequestCode, AppConstants.STATUS_SET_FAILURE);
                        Snackbar.make(coordinateLayout, intent.getStringExtra(AppConstants.NOTE), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                case AppConstants.ACTION_ADD_PRINTER_RESULT:
                    if (intent.getBooleanExtra(AppConstants.IS_ADD_PEINTER, true)) {
                        //重新读取本地数据，刷新打印机列表
                        refreshPrinterList(1);
                        Snackbar.make(coordinateLayout, R.string.add_succeed, Snackbar.LENGTH_SHORT).show();
                    } else {
                        //提示用户添加失败
                        Snackbar.make(coordinateLayout, intent.getStringExtra(AppConstants.NOTE), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                case AppConstants.ACTION_DELETE_PRINTER_RESULT:
                    if (intent.getBooleanExtra(AppConstants.IS_DELETE_PRINTER, true)) {
                        //更新列表
                        printerList.remove(positionRequestCode);
                        adapter.notifyItemRemoved(positionRequestCode);
                        Snackbar.make(coordinateLayout, R.string.delete_succeed, Snackbar.LENGTH_SHORT).show();
                    } else {
                        //更新删除状态，并提示用户删除失败
                        //printer.setStatus(AppConstants.STATUS_DELETE_FAILURE);
                        adapter.notifyItemChanged(positionRequestCode, AppConstants.STATUS_DELETE_FAILURE);
                        Snackbar.make(coordinateLayout, intent.getStringExtra(AppConstants.NOTE), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 注册初始化和激活的广播
     */
    private void rgReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_INITIALIZE_RESULT); //初始化（注册）
        filter.addAction(AppConstants.ACTION_ACTIVATE_RESULT);   //激活
        filter.addAction(AppConstants.ACTION_OBTAIN_PRINTERS_RESULT); //获取打印机列表
        filter.addAction(AppConstants.ACTION_SETTING_PRINTER_RESULT); //设置打印机
        filter.addAction(AppConstants.ACTION_ADD_PRINTER_RESULT);     //添加打印机
        filter.addAction(AppConstants.ACTION_DELETE_PRINTER_RESULT);  //删除打印机
        registerReceiver(resultReceiver, filter);
        isRegister = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_PRINTER_REQUEST_CODE) {
                Snackbar.make(coordinateLayout, R.string.adding_printer_tips, Snackbar.LENGTH_LONG).show();
            } else if (requestCode == positionRequestCode) {
                Log.d(TAG, "设置回调进来了！");
                //printer.setStatus(AppConstants.STATUS_SETTING);
                adapter.notifyItemChanged(positionRequestCode, AppConstants.STATUS_SETTING);
            }
        }
    }

    /**
     * 是否删除或停用的弹出框
     *
     * @param name
     */
    private void createDeleteOrPauseWindow(final String name, final int flag) {
        window = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popupwindow_delete)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.AnimUp)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        delOrPauTitle = (TextView) view.findViewById(R.id.title);
                        delOrPauText = (TextView) view.findViewById(R.id.delete_pause_tv);
                        deleteOrPause = (RelativeLayout) view.findViewById(R.id.delete_pause_layout);
                        delOrPauCancel = (CardView) view.findViewById(R.id.cancle);
                        if (flag == AppConstants.FLAG_DELETE) {
                            delOrPauTitle.setText(R.string.delete_tip);
                            delOrPauText.setText(R.string.delete);
                        } else {
                            delOrPauTitle.setText(R.string.printer_pause_tip);
                            delOrPauText.setText(R.string.printer_pause);
                        }
                    }
                })
                .setOutsideTouchable(true)
                .create();
        window.showAtLocation(coordinateLayout, Gravity.BOTTOM, 0, 0);
        deleteOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOrPause.setEnabled(false);
                if (flag == AppConstants.FLAG_DELETE) {
                    deleteNetWorkPrinter(name);
                } else {
                    pausePrinter(name);
                }
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    deleteWindow.dismiss();
                                    printerList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    MySqliteDBOpenhelper.delete(MyDBCallBack.TABLE_NAME_PRINTER, PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{name});
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();*/
            }
        });
        delOrPauCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
    }

    //停用打印机
    private void pausePrinter(final String name) {
        final Map<String, String> map = new HashMap<>();
        map.put("manageToken", FenghuoApplication.xg_token);
        final String uuid = UUID.randomUUID().toString();
        map.put("requestId", uuid);
        map.put("businessid", FenghuoApplication.currentShopID + "");   //店铺id（不能传商家id）
        map.put("ip", printer.getIp());
        map.put("port", "9100");
        map.put("name", printer.getName());
        map.put("businessNum", printer.getBusinessNum() + "");
        map.put("kitchenNum", printer.getKitchenNum() + "");
        map.put("customerNum", printer.getCustomerNum() + "");
        map.put("senderNum", printer.getSenderNum() + "");
        map.put("qrcode", printer.getQrcode() + "");
        map.put("ispause", "1");
        x.http().post(CommonUtil.genGetParam(AppConstants.SET_CLOUD_PRINTER, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.optInt("status") == 0) {  //消息发送成功
                        adapter.notifyItemChanged(positionRequestCode, AppConstants.STATUS_DELETING);
                        window.dismiss();
                        Snackbar.make(coordinateLayout, R.string.pause_printer_tips, Snackbar.LENGTH_LONG).show();
                        SPUtil.putBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, false);
                        MapUtil.saveRequestParams(mContext, map);
                        SPUtil.putString(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL, AppConstants.SET_CLOUD_PRINTER);
                        SPUtil.putInt(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL_TYPE, AppConstants.TYPE_SET_PRINTER);
                        SPUtil.putString(mContext, AppConstants.KEY_CURRENT_UUID, uuid);
                        printer.setStatus(AppConstants.STATUS_PAUSEING);
                        MySqliteDBOpenhelper.update(MyDBCallBack.TABLE_NAME_PRINTER, printer, PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{name});
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "pausePrinter=========onError");
                Snackbar.make(coordinateLayout, R.string.network_error, Snackbar.LENGTH_SHORT).show();  //网络错误
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "pausePrinter=========onCancelled");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "pausePrinter=========onFinished");
                window.dismiss();
            }
        });
    }

    //删除网络打印机
    private void deleteNetWorkPrinter(final String name) {
        final Map<String, String> map = new HashMap<>();
        map.put("manageToken", FenghuoApplication.xg_token);
        final String uuid = UUID.randomUUID().toString();
        map.put("requestId", uuid);
        map.put("businessid", FenghuoApplication.currentShopID + "");   //店铺id（不能传商家id）
        map.put("name", name);
        x.http().post(CommonUtil.genGetParam(AppConstants.DELETE_CLOUD_PRINTER, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.optInt("status") == 0) {  //消息发送成功
                        //printer.setStatus(AppConstants.STATUS_DELETING);
                        adapter.notifyItemChanged(positionRequestCode, AppConstants.STATUS_DELETING);
                        window.dismiss();
                        Snackbar.make(coordinateLayout, R.string.delete_printer_tips, Snackbar.LENGTH_LONG).show();
                        SPUtil.putBoolean(mActivity, AppConstants.KEY_PREVIOUS_OPERATION_STATE, false);
                        MapUtil.saveRequestParams(mContext, map);
                        SPUtil.putString(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL, AppConstants.DELETE_CLOUD_PRINTER);
                        SPUtil.putInt(mContext, AppConstants.KEY_PREVIOUS_OPERATION_URL_TYPE, AppConstants.TYPE_DELETE_PRINTER);
                        SPUtil.putString(mContext, AppConstants.KEY_CURRENT_UUID, uuid);
                        printer.setStatus(AppConstants.STATUS_DELETING);
                        MySqliteDBOpenhelper.update(MyDBCallBack.TABLE_NAME_PRINTER, printer, PrinterEntry.COLUMN_NAME_NAME + "=?", new String[]{name});
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "deleteNetWorkPrinter=========onError");
                Snackbar.make(coordinateLayout, R.string.network_error, Snackbar.LENGTH_SHORT).show();  //网络错误
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "deleteNetWorkPrinter=========onCancelled");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "deleteNetWorkPrinter=========onFinished");
                window.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (isRegister) {
            unregisterReceiver(resultReceiver);
        }
        if (window.isShowing()) {
            window.dismiss();
            window = null;
        }
    }
}
