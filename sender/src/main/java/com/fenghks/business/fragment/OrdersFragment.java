package com.fenghks.business.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.fenghks.business.AppConstants;
import com.fenghks.business.R;
import com.fenghks.business.activity.GetOrderConfirmActivity;
import com.fenghks.business.activity.OrderDetailActivity;
import com.fenghks.business.adapter.SendOrderListAdapter;
import com.fenghks.business.bean.Order;
import com.fenghks.business.custom_view.CommonPopupWindow;
import com.fenghks.business.tools.DividerItemDecoration;
import com.fenghks.business.utils.MyAnimationUtils;
import com.fenghks.business.zxing.android.CaptureActivity;
import com.scwang.smartrefresh.header.DeliveryHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by fenghuo on 2017/9/30.
 */

public class OrdersFragment extends Fragment{
    public static final String TAG = "OrdersFragment";
    @BindView(R.id.orders_rv)
    RecyclerView ordersRv;
    @BindView(R.id.no_orders_tv)
    TextView noOrdersTv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    private View view;
    private SendOrderListAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();
    private CommonPopupWindow window;
    private RadioButton cancleRb, doRb;
    LatLng destination = new LatLng(29.591462, 106.571911);//店铺位置

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orders, null);
        unbinder = ButterKnife.bind(this, view);

        initSettings();
        initClickListener();
        return view;
    }

    private void initSettings() {
        /**
         * 设置 下拉刷新 Header 和 footer 风格样式
         */
        refreshLayout.setRefreshHeader(new DeliveryHeader(getActivity()));
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
        refreshLayout.setDisableContentWhenRefresh(false);  //是否在刷新的时候禁止内容的一切手势操作（默认false）
        //refreshLayout.autoRefresh();  //进入自动刷新

        orderList.clear();
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            orderList.add(order);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //设置布局管理器
        ordersRv.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        orderAdapter = new SendOrderListAdapter(getActivity(), orderList, AppConstants.TAG_ORDERS);
        ordersRv.setAdapter(orderAdapter);
        //设置增加或删除条目的动画
        ordersRv.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        ordersRv.addItemDecoration(new DividerItemDecoration(getActivity(), 5));
        MyAnimationUtils.viewGroupOutAlphaAnimation(getActivity(), ordersRv, 0.1F);
        orderAdapter.setOnOrderItemClickListener(new SendOrderListAdapter.OnOrderItemClickListener() {
            @Override
            public void onClick(int position) {
                //跳转订单详情界面
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                startActivity(intent);
            }
        });
        orderAdapter.setOnNavigationClickListener(new SendOrderListAdapter.OnNavigationClickListener() {
            @Override
            public void onClick(int position) {
                //跳转路线规划导航界面
                AmapNaviPage.getInstance().showRouteActivity(getActivity(), new AmapNaviParams(null, null, new Poi("五里店", destination, ""), AmapNaviType.DRIVER), new INaviInfoCallback() {
                    @Override
                    public void onInitNaviFailure() {
                        Log.d(TAG,"onInitNaviFailure");
                    }

                    @Override
                    public void onGetNavigationText(String s) {
                        Log.d(TAG,"onGetNavigationText");
                    }

                    @Override
                    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
                        Log.d(TAG,"onLocationChange");
                    }

                    @Override
                    public void onArriveDestination(boolean b) {
                        Log.d(TAG,"onArriveDestination");
                    }

                    @Override
                    public void onStartNavi(int i) {
                        Log.d(TAG,"onStartNavi");
                    }

                    @Override
                    public void onCalculateRouteSuccess(int[] ints) {
                        Log.d(TAG,"onCalculateRouteSuccess");
                    }

                    @Override
                    public void onCalculateRouteFailure(int i) {
                        Log.d(TAG,"onCalculateRouteFailure");
                    }

                    @Override
                    public void onStopSpeaking() {
                        Log.d(TAG,"onStopSpeaking");
                    }
                });
            }
        });
        orderAdapter.setOnCallBusinessClickListener(new SendOrderListAdapter.OnCallBusinessClickListener() {
            @Override
            public void onClick(int position) {
                //拨打商家电话
                callWindow(AppConstants.TAG_BUSINESS, "023-67750215");
            }
        });
        orderAdapter.setOnCallCustomerClickListener(new SendOrderListAdapter.OnCallCustomerClickListener() {
            @Override
            public void onClick(int position) {
                //拨打客户电话
                callWindow(AppConstants.TAG_CUSTOMER, "023-85227290");
            }
        });
        orderAdapter.setOnSendConfirmClickListener(new SendOrderListAdapter.OnSendConfirmClickListener() {
            @Override
            public void onClick(int position) {
                //送达确认界面
                Intent intent = new Intent(getActivity(), GetOrderConfirmActivity.class);
                startActivity(intent);
            }
        });
        orderAdapter.setOnScanGetOrderClickListener(new SendOrderListAdapter.OnScanGetOrderClickListener() {
            @Override
            public void onClick(int position) {
                //扫码取餐
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initClickListener() {
        /**
         * 下拉刷新监听
         */
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(1000);
            }
        });
        /**
         * 上拉加载监听
         */
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(1000);
            }
        });
    }

    /**
     * 拨打商家和客户
     */
    private void callWindow(final int flag, final String phoneNumber) {
        window = new CommonPopupWindow.Builder(getActivity())
                .setView(R.layout.popupwindow_contact_service)
                .setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.AnimDown)
                .setBackGroundLevel(0.5F)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        TextView tips = (TextView) view.findViewById(R.id.content);
                        if (flag == AppConstants.TAG_BUSINESS) {
                            tips.setText("商家电话：" + phoneNumber);
                        } else if (flag == AppConstants.TAG_CUSTOMER) {
                            tips.setText("客户电话：" + phoneNumber);
                        }
                        cancleRb = (RadioButton) view.findViewById(R.id.cancel_rb);
                        doRb = (RadioButton) view.findViewById(R.id.do_rb);
                    }
                })
                .setOutsideTouchable(true)
                .create();
        window.showAtLocation(coordinatorLayout, Gravity.CENTER, 0, 0);
        cancleRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        doRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));//跳转到拨号界面，同时传递电话号码
                startActivity(dialIntent);
                window.dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
