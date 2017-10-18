package com.fenghks.business.orders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.BaseActivity;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.bean.Order;
import com.fenghks.business.business.PrintersManageActivity;
import com.fenghks.business.business.SettingsActivity;
import com.fenghks.business.tools.DragFloatActionButton;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.SPUtil;
import com.fenghks.business.utils.VibratorUtil;

import org.json.JSONArray;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends BaseActivity implements OrderFragment.OnFragmentInteractionListener {
    @BindView(R.id.root_layout)
    CoordinatorLayout root_layout;
    @BindView(R.id.tl_toolbar)
    Toolbar tl_toolbar;
    @BindView(R.id.tab_order_layout)
    TabLayout tab_order_layout;
    @BindView(R.id.vp_orders_pager)
    ViewPager orders_pager;
    @BindView(R.id.fab_add_bill)
    DragFloatActionButton fab_add_bill;

    OrderFragPagerAdapter viewPagerAdapter;
    //private final List<Fragment> listFragments = new ArrayList<>();//添加的Fragment的集合
    //private final List<String> listFragTitles = new ArrayList<>();//每个Fragment对应的title的集合

    private OrderHelper orderHelper;
    private double lastUpdated = 0;
    //private int pageNo = 1;
    private int lastOrderId = 0;
    private long lastUpdateTime = 0;

    public static final int MSG_ORDER_UPDATED = 1;
    private static final String TAG = "OrderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        defaultSettings(); //初始化
        fab_add_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(root_layout, R.string.app_name, Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();*/
                //VibratorUtil.vibrateOnce(mActivity);
                Intent intent = new Intent(mActivity, AddOrderActivity.class);
                startActivity(intent);
            }
        });

    }

    //默认设置，初始化
    private void defaultSettings() {
        if (FenghuoApplication.business.getParentid() > 0) {
            setSwipeBackEnable(false);
        }
        tl_toolbar.setTitle(FenghuoApplication.shops.get(FenghuoApplication.currentShop).getName());
        setSupportActionBar(tl_toolbar);
        orderHelper = OrderHelper.getInstance(this);
        lastOrderId = SPUtil.getInt(mContext, "lastOrderId");

        initViewPager();

        if (FenghuoApplication.orders == null) {
            FenghuoApplication.orders = new ArrayList<>();
        }
        //orderList = new ArrayList<>();
        updateData(true);

        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_ORDER_CREATED);
        filter.addAction(AppConstants.ACTION_ORDER_STATE_CHANGED);
        registerReceiver(receiver, filter);
    }

    private void initViewPager() {
        viewPagerAdapter = new OrderFragPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(OrderFragment.newInstance(AppConstants.ALL_ORDER, null),
                getString(R.string.all_order));//添加Fragment
        viewPagerAdapter.addFragment(OrderFragment.newInstance(AppConstants.WAITING_PLACE_ORDER, null),
                getString(R.string.waiting_place_order));//添加Fragment
        viewPagerAdapter.addFragment(OrderFragment.newInstance(AppConstants.WAITING_ORDER, null),
                getString(R.string.waiting_order));//添加Fragment
        viewPagerAdapter.addFragment(OrderFragment.newInstance(AppConstants.TRANSPORTING_ORDER, null),
                getString(R.string.transporting_order));//添加Fragment
        viewPagerAdapter.addFragment(OrderFragment.newInstance(AppConstants.COMPLETE_ORDER, null),
                getString(R.string.complete_order));//添加Fragment
        viewPagerAdapter.addFragment(OrderFragment.newInstance(AppConstants.ABNORMAL_ORDER, null),
                getString(R.string.abnormal_order));//添加Fragment
        viewPagerAdapter.addFragment(OrderFragment.newInstance(AppConstants.CHARGEBACK_ORDER, null),
                getString(R.string.chargeback_order));//添加Fragment
        orders_pager.setAdapter(viewPagerAdapter);//设置适配器
        orders_pager.setCurrentItem(1);

        //为订单分类tab导航TabLayout添加Tab
        //tab_order_layout.setVisibility(View.VISIBLE);
        tab_order_layout.addTab(tab_order_layout.newTab().setText(R.string.all_order));
        tab_order_layout.addTab(tab_order_layout.newTab().setText(R.string.waiting_place_order));
        tab_order_layout.addTab(tab_order_layout.newTab().setText(R.string.waiting_order));
        tab_order_layout.addTab(tab_order_layout.newTab().setText(R.string.transporting_order));
        tab_order_layout.addTab(tab_order_layout.newTab().setText(R.string.complete_order));
        tab_order_layout.addTab(tab_order_layout.newTab().setText(R.string.abnormal_order));
        tab_order_layout.addTab(tab_order_layout.newTab().setText(R.string.chargeback_order));
        //给TabLayout设置关联ViewPager，ViewPagerAdapter中的getPageTitle()方法返回的就是Tab上的标题
        tab_order_layout.setupWithViewPager(orders_pager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lastUpdated > 0) {
            updateData(false);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, R.string.hello_blank_fragment, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReqDataUpdate(int orderType) {
        updateData(true);
        /*int index = 1;
        switch (orderType) {
            case AppConstants.ALL_ORDER://全部
                index = 0;
                break;
            case AppConstants.WAITING_PLACE_ORDER://待下单
                index = 1;
                break;
            *//*case AppConstants.WAITING_ORDER:
                index = 2;
                break;*//*
            case AppConstants.TRANSPORTING_ORDER://配送中
                index = 3;
                break;
            case AppConstants.COMPLETE_ORDER://已完成
                index = 4;
                break;
            case AppConstants.ABNORMAL_ORDER://异常单
                index = 5;
                break;
            case AppConstants.CHARGEBACK_ORDER://退单
                index = 6;
                break;
            default://待取餐
                index = 1;
                break;
        }
        viewPagerAdapter.getItem(index).updateData();*/
    }

    @Override
    public void onLoadMore(int orderType) {

    }

    private void updateData(boolean forceUpdate) {
        //double now = System.currentTimeMillis();
        double interval = forceUpdate ? 3000 : 60000;
        if (0 == lastUpdated || System.currentTimeMillis() - lastUpdated > interval) {
            final Map map = new HashMap();
            map.put("businessid", FenghuoApplication.shops.get(FenghuoApplication.currentShop).getId());
            /*if(lastUpdateTime > 0){
                map.put("lastReqTime",lastUpdateTime);
            }else {
                map.put("getAll",1);
            }*/
            map.put("getAll", 1);
            //map.put("pageNo",pageNo);
            x.http().post(CommonUtil.genGetParam(CommonUtil.GET_SHOP_ORDERS_URL, map), new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            lastUpdateTime = System.currentTimeMillis() / 1000;
                            System.out.println(result);
                            JSONArray objs = ParseUtils.parseDataArray(mContext,result);
                            if (null != objs) {
                                FenghuoApplication.orders.clear();
                                for (int i = 0; i < objs.length(); i++) {
                                    Order order = ParseUtils.parseOrder(objs.optJSONObject(i));
                                    if (order.getId() > lastOrderId) lastOrderId = order.getId();
                                    order.setBusinessname(FenghuoApplication.shops.get(FenghuoApplication.currentShop).getName());
                                    FenghuoApplication.orders.add(order);
                                }
                                SPUtil.putInt(mContext, "lastOrderId", lastOrderId);
                                lastUpdated = System.currentTimeMillis();
                                viewPagerAdapter.getItem(orders_pager.getCurrentItem()).refreshData(true);
                                Toast.makeText(mActivity, R.string.orders_refresh_success, Toast.LENGTH_SHORT).show();
                            } else {
                                viewPagerAdapter.getItem(orders_pager.getCurrentItem()).refreshData(false);
                                //CommonUtil.handleErr(mActivity, result);
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            viewPagerAdapter.getItem(orders_pager.getCurrentItem()).refreshData(false);
                            ex.printStackTrace();
                            CommonUtil.networkErr(ex, isOnCallback);
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                            viewPagerAdapter.getItem(orders_pager.getCurrentItem()).refreshData(false);
                        }

                        @Override
                        public void onFinished() {
                            viewPagerAdapter.getItem(orders_pager.getCurrentItem()).refreshData(true);
                        }
                    });
        } else {
            viewPagerAdapter.getItem(orders_pager.getCurrentItem()).refreshData(false);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                //mSearchView.setIconified(false);
                //orders_pager.setCurrentItem(0);
                VibratorUtil.vibrateOnce(mActivity);
                Intent mIntent = new Intent(mActivity, OrderSearchActivity.class);
                startActivity(mIntent);
                return true;
            case R.id.menu_refresh:
                VibratorUtil.vibrateOnce(mActivity);
                updateData(true);
                return true;
            /*case R.id.menu_bluetooth:
                VibratorUtil.vibrateOnce(mActivity);
                Intent intentBT = new Intent(this, BluetoothActivity.class);
                startActivity(intentBT);
                break;*/
            case R.id.menu_settings:
                VibratorUtil.vibrateOnce(mActivity);
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.printer_manage:
                VibratorUtil.vibrateOnce(mActivity);
                Intent intent = new Intent(this, PrintersManageActivity.class);
                startActivity(intent);
                break;
            /*case R.id.menu_logout:
                VibratorUtil.vibrateOnce(mActivity);
                CommonUtil.logout(mActivity);
                finish();
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return super.onCreateOptionsMenu(menu);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case AppConstants.ACTION_ORDER_CREATED:
                case AppConstants.ACTION_ORDER_STATE_CHANGED:
                    updateData(true);
                    break;
            }
        }
    };
}
