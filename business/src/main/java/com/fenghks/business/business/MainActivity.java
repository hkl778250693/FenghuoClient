package com.fenghks.business.business;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fenghks.business.AppConstants;
import com.fenghks.business.BaseActivity;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.adpter.ShopAdapter;
import com.fenghks.business.bean.Business;
import com.fenghks.business.orders.OrderActivity;
import com.fenghks.business.tools.GlideCircleTransform;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.SPUtil;
import com.fenghks.business.utils.VibratorUtil;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    //@BindView(R.id.tl_toolbar)
    //Toolbar tl_toolbar;
    @BindView(R.id.business_image)
    ImageView business_image;
    @BindView(R.id.business_name)
    TextView business_name;
    @BindView(R.id.business_address)
    TextView business_address;
    @BindView(R.id.account_balance)
    TextView account_balance;
    @BindView(R.id.shop_number)
    TextView shop_number;
    @BindView(R.id.rv_shops)
    RecyclerView rv_shops;

    private ShopAdapter shopAdapter;
    private double lastUpdated;  //最后一次更新时间
    private boolean receivePush;
    private long exitTime = 0;//退出程序
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // 开启logcat输出，方便debug，发布时请关闭
        //XGPushConfig.enableDebug(this, true);
        receivePush = SPUtil.getBoolean(mActivity, AppConstants.SP_SETTINGS, getString(R.string.pref_receive_notice), true);

        registerPush();
        setSwipeBackEnable(false);
        //tl_toolbar.setTitle(R.string.choose_shop);
        //setSupportActionBar(tl_toolbar);
        if (FenghuoApplication.business != null) {
            getCity();
            getSenders();
        }
        lastUpdated = System.currentTimeMillis();

        if (null != FenghuoApplication.business) {
            if (FenghuoApplication.business.getParentid() == 0) {
                initView();
            }
        }
    }

    private void registerPush() {
        // 开启logcat输出，方便debug，发布时请关闭
        //XGPushConfig.enableDebug(this, true);
        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
        // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
        // 具体可参考详细的开发指南
        // 传递的参数为ApplicationContext
        //XGPushManager.registerPush(mContext);
        XGPushManager.registerPush(mContext, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object obj, int flag) {
                Log.d(TAG, "XGPush注册成功,Token值为：" + obj);
                FenghuoApplication.xg_token = obj.toString();
                SPUtil.putString(mActivity, "xg_token", FenghuoApplication.xg_token);
                /*if (receivePush) {
                    CommonUtil.registerPushService(mActivity);
                }*/
            }

            @Override
            public void onFail(Object obj, int errCode, String msg) {
                Log.d(TAG, "TPush注册失败,错误码为：" + errCode + ",错误信息：" + msg);
            }
        });
    }

    private void initView() {
        updateBusinessView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        rv_shops.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        shopAdapter = new ShopAdapter(this);
        shopAdapter.setOnShopItemClickListener(new ShopAdapter.OnShopItemClickListener() {
            @Override
            public void onClick(int position) {
                VibratorUtil.vibrateOnce(mContext);
                if (FenghuoApplication.orders != null) {
                    FenghuoApplication.orders.clear();
                }
                FenghuoApplication.currentShop = position;
                Intent mIntent = new Intent(mActivity, OrderActivity.class);
                Business shop = FenghuoApplication.shops.get(FenghuoApplication.currentShop);
                FenghuoApplication.currentShopID = shop.getId();  //将当前点击的店铺ID保存到全局变量
                mActivity.startActivity(mIntent);
            }

            @Override
            public void onLongClick(int position) {
                VibratorUtil.vibrate(mContext, 100);
            }
        });
        rv_shops.setAdapter(shopAdapter);
        //设置增加或删除条目的动画
        rv_shops.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        rv_shops.addItemDecoration(new com.fenghks.business.tools.DividerItemDecoration(mActivity, 8));
    }

    public void updateBusinessView() {
        Glide.with(mActivity).load(FenghuoApplication.business.getLogimg()).
                transform(new GlideCircleTransform(mActivity)).
                into(business_image);
        business_name.setText(FenghuoApplication.business.getName());
        business_address.setText(FenghuoApplication.business.getAddress());
        account_balance.setText("￥" + FenghuoApplication.business.getSurplus());
        shop_number.setText("" + FenghuoApplication.shops.size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (System.currentTimeMillis() - lastUpdated > 3000) {
            lastUpdated = System.currentTimeMillis();
            getBusiness();
        }
        if (FenghuoApplication.senders == null || FenghuoApplication.senders.size() == 0) {
            getSenders();
        }
        getData();
    }

    @OnClick({R.id.btn_settings, R.id.charge_money})
    public void onBtnClick(View view) {
        VibratorUtil.vibrateOnce(mContext);
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.charge_money:
                intent.setClass(this, RechargeActivity.class);
                //Toast.makeText(mActivity,R.string.waiting_dev,Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_settings:
                intent.setClass(this, SettingsActivity.class);
                break;
        }
        startActivity(intent);
    }

    private void getBusiness() {
        final Map map = new HashMap();
        x.http().post(CommonUtil.genGetParam(CommonUtil.GET_BUSINESS_URL, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                JSONObject obj = ParseUtils.parseDataString(mContext, result);
                if (null != obj) {
                    FenghuoApplication.business = ParseUtils.parseBusiness(obj);
                    updateBusinessView();
                } else {
                    //CommonUtil.handleErr(mActivity, result);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getData() {
        if (FenghuoApplication.shops == null) {
            FenghuoApplication.shops = new ArrayList<>();
        }
        if (FenghuoApplication.business.getParentid() > 0) {
            if (FenghuoApplication.shops.size() == 0) {
                FenghuoApplication.shops.add(FenghuoApplication.business);
                FenghuoApplication.currentShop = 0;
            }
            Intent intent = new Intent(mActivity, OrderActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (FenghuoApplication.shops.size() > 0) {
                return;
            }
            final Map map = new HashMap();
            map.put("businessid", FenghuoApplication.business.getId());
            x.http().post(CommonUtil.genGetParam(CommonUtil.GET_SHOPS_URL, map), new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println(result);
                    JSONArray objs = ParseUtils.parseDataArray(mContext, result);
                    if (null != objs) {
                        FenghuoApplication.shops.clear();
                        for (int i = 0; i < objs.length(); i++) {
                            FenghuoApplication.shops.add(ParseUtils.parseBusiness(objs.optJSONObject(i)));
                        }
                        if (receivePush) {
                            CommonUtil.registerPushService(mActivity);
                        }
                        shop_number.setText("" + FenghuoApplication.shops.size());
                        shopAdapter.notifyDataSetChanged();
                    } else {
                        //CommonUtil.handleErr(mActivity, result);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    private void getSenders() {
        if (FenghuoApplication.senders == null) {
            FenghuoApplication.senders = new ArrayList<>();
        }
        if (FenghuoApplication.senders.size() > 0) {
            return;
        }
        final Map map = new HashMap();
        map.put("areaid", FenghuoApplication.business.getAreaid());
        x.http().post(CommonUtil.genGetParam(CommonUtil.LIST_REDIS_SENDERS_URL, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                JSONArray objs = ParseUtils.parseDataArray(mContext, result);
                if (null != objs) {
                    FenghuoApplication.senders.clear();
                    for (int i = 0; i < objs.length(); i++) {
                        FenghuoApplication.senders.add(ParseUtils.parseSender(objs.optJSONObject(i)));
                    }
                } else {
                    //CommonUtil.handleErr(mActivity, result);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getCity() {
        FenghuoApplication.city = SPUtil.getString(mActivity, "city");
        if (FenghuoApplication.city != null || !"".equals(FenghuoApplication.city)) {
            return;
        }
        final Map map = new HashMap();
        map.put("areaid", FenghuoApplication.business.getAreaid());
        x.http().post(CommonUtil.genGetParam(CommonUtil.GET_CITY_URL, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                JSONObject obj = ParseUtils.parseDataString(mContext,result);
                if (null != obj) {
                    FenghuoApplication.city = obj.optString("city");
                    SPUtil.putString(mActivity, "city", FenghuoApplication.city);
                } else {
                    //CommonUtil.handleErr(mActivity, result);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /*@Event(value = {R.id.btn_order}, type = View.OnClickListener.class)
    private void onTestButtonClick(View view){
        Intent intent = new Intent(mActivity,OrderActivity.class);
        startActivity(intent);
    }*/

    /**
     * 捕捉返回事件按钮
     * 如果 Activity继承 TabActivity,用onKeyDown无响应，
     * 可以改用 dispatchKeyEvent
     * <p/>
     * 一般的 Activity 用 onKeyDown就可以了
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    /**
     * 退出程序
     */
    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Log.d("exitTime==========>", System.currentTimeMillis() - exitTime + "");
            //-------------Activity.this的context 返回当前activity的上下文，属于activity，activity 摧毁他就摧毁
            //-------------getApplicationContext() 返回应用的上下文，生命周期是整个应用，应用摧毁它才摧毁
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
