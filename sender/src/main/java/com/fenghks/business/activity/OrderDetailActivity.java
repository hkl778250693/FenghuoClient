package com.fenghks.business.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fenghks.business.AppConstants;
import com.fenghks.business.R;
import com.fenghks.business.custom_view.CommonPopupWindow;
import com.fenghks.business.utils.VibratorUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by fenghuo on 2017/10/13.
 */

public class OrderDetailActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @BindView(R.id.call_customer_btn)
    Button callCustomerBtn;
    @BindView(R.id.nav_btn)
    Button navBtn;
    @BindView(R.id.call_business_btn)
    Button callBusinessBtn;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.get_order_check)
    Button getOrderCheck;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    private CommonPopupWindow window;
    private RadioButton cancleRb, doRb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }

    @OnClick({R.id.back_iv, R.id.call_customer_btn, R.id.nav_btn, R.id.call_business_btn, R.id.get_order_check})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.back_iv:
                mActivity.finish();
                break;
            case R.id.call_customer_btn:
                //拨打客户电话
                callWindow(AppConstants.TAG_CUSTOMER, "023-85227290");
                break;
            case R.id.nav_btn:
                //导航路线
                break;
            case R.id.call_business_btn:
                //拨打商家电话
                callWindow(AppConstants.TAG_BUSINESS, "023-67750215");
                break;
            case R.id.get_order_check:
                intent = new Intent(mContext, GetOrderConfirmActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //View view = MenuItemCompat.getActionView(item);
        switch (item.getItemId()) {
            case R.id.menu_info:
                VibratorUtil.vibrateOnce(mActivity);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 拨打商家和客户
     */
    private void callWindow(final int flag, final String phoneNumber) {
        window = new CommonPopupWindow.Builder(mActivity)
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

}
