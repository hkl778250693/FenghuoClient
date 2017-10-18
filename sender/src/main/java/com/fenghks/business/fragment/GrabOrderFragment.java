package com.fenghks.business.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.R;
import com.fenghks.business.activity.GrabOrderMapActivity;
import com.fenghks.business.adapter.GrabOrderListAdapter;
import com.fenghks.business.adapter.SendOrderListAdapter;
import com.fenghks.business.bean.Order;
import com.fenghks.business.tools.DividerItemDecoration;
import com.fenghks.business.utils.MyAnimationUtils;
import com.scwang.smartrefresh.header.DeliveryHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by fenghuo on 2017/9/30.
 */

public class GrabOrderFragment extends Fragment {
    @BindView(R.id.grab_orders_rv)
    RecyclerView grabOrdersRv;
    @BindView(R.id.no_orders_tv)
    TextView noOrdersTv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private GrabOrderListAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    Unbinder unbinder;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grab_orders, null);
        unbinder = ButterKnife.bind(this, view);

        initSettings();
        initClickListener();
        return view;
    }

    private void initSettings() {
        /**
         * 设置 下拉刷新 Header 和 footer 风格样式
         */
        //mRefreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
        refreshLayout.setRefreshHeader(new DeliveryHeader(getActivity()));
        //mRefreshLayout.setRefreshHeader(new CircleHeader(this));
        //mRefreshLayout.setRefreshHeader(new DropboxHeader(this));
        //mRefreshLayout.setRefreshHeader(new FunGameHeader(this));
        //mRefreshLayout.setRefreshHeader(new FalsifyHeader(this));
        //mRefreshLayout.setRefreshHeader(new PhoenixHeader(this));
        //refreshLayout.setRefreshHeader(new WaterDropHeader(getActivity()));
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
        //mRefreshLayout.setRefreshHeader(new WaveSwipeHeader(this));
        //mRefreshLayout.setRefreshHeader(new TaurusHeader(this));
        //mRefreshLayout.setRefreshHeader(new StoreHouseHeader(this));
        //mRefreshLayout.setRefreshHeader(new FunGameHitBlockHeader(this));
        //mRefreshLayout.setRefreshHeader(new FunGameBattleCityHeader(this));
        //mRefreshLayout.setRefreshHeader(new FlyRefreshHeader(this));
        //refreshLayout.setEnableRefresh(false);  //关闭下拉加载功能
        //refreshLayout.setEnableLoadmore(false);  //关闭上拉加载功能
        refreshLayout.setDisableContentWhenRefresh(false);  //是否在刷新的时候禁止内容的一切手势操作（默认false）
        //refreshLayout.autoRefresh();  //进入自动刷新

        orderList.clear();
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            orderList.add(order);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //设置布局管理器
        grabOrdersRv.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        adapter = new GrabOrderListAdapter(getActivity(), orderList);
        grabOrdersRv.setAdapter(adapter);
        //设置增加或删除条目的动画
        grabOrdersRv.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        grabOrdersRv.addItemDecoration(new DividerItemDecoration(getActivity(), 5));
        MyAnimationUtils.viewGroupOutAlphaAnimation(getActivity(), grabOrdersRv, 0.1F);
        adapter.setOnOrderItemClickListener(new GrabOrderListAdapter.OnOrderItemClickListener() {
            @Override
            public void onClick(int position) {
                //跳转地图界面
                Intent intent = new Intent(getActivity(), GrabOrderMapActivity.class);
                startActivity(intent);
            }
        });
        adapter.setOnGrabClickListener(new GrabOrderListAdapter.OnGrabClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(getActivity(),R.string.grab_order_succeed,Toast.LENGTH_SHORT).show();
                //抢单成功则将该订单移除列表
                orderList.remove(position);
                adapter.notifyItemRemoved(position);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.no_orders_tv)
    public void onViewClicked() {
    }
}
