package com.fenghks.business.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fenghks.business.AppConstants;
import com.fenghks.business.R;
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

/**
 * Created by fenghuo on 2017/10/10.
 */

public class SendRecordActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.send_record_rv)
    RecyclerView sendRecordRv;
    @BindView(R.id.no_data_tv)
    TextView noDataTv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.succeed_order_rb)
    RadioButton succeedOrderRb;
    @BindView(R.id.unusual_order_rb)
    RadioButton unusualOrderRb;

    private SendOrderListAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_record);
        ButterKnife.bind(this);

        initSettings();
        initClickListener();
    }

    @OnClick({R.id.back_iv, R.id.succeed_order_rb, R.id.unusual_order_rb})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                mActivity.finish();
                break;
            case R.id.succeed_order_rb:
                break;
            case R.id.unusual_order_rb:
                break;
        }
    }

    private void initSettings() {
        toolBar.setTitle(R.string.send_record);
        succeedOrderRb.setChecked(true);
        /**
         * 设置 下拉刷新 Header 和 footer 风格样式
         */
        refreshLayout.setRefreshHeader(new DeliveryHeader(mContext));
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
        refreshLayout.setDisableContentWhenRefresh(false);  //是否在刷新的时候禁止内容的一切手势操作（默认false）
        //refreshLayout.autoRefresh();  //进入自动刷新

        orderList.clear();
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            orderList.add(order);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        //设置布局管理器
        sendRecordRv.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        orderAdapter = new SendOrderListAdapter(mContext, orderList, AppConstants.TAG_SEND_ORDERS_RECORD);
        sendRecordRv.setAdapter(orderAdapter);
        //设置增加或删除条目的动画
        sendRecordRv.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        sendRecordRv.addItemDecoration(new DividerItemDecoration(mContext, 5));
        MyAnimationUtils.viewGroupOutAlphaAnimation(mContext, sendRecordRv, 0.1F);
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
}
