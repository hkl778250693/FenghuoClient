package com.fenghks.business.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fenghks.business.R;
import com.fenghks.business.utils.VibratorUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by fenghuo on 2017/10/13.
 */

public class GetOrderConfirmActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.send_address_tv)
    TextView sendAddressTv;
    @BindView(R.id.user_name_tv)
    TextView userNameTv;
    @BindView(R.id.customer_phone_num)
    TextView customerPhoneNum;
    @BindView(R.id.check_code_edit)
    EditText checkCodeEdit;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_order_check);
        ButterKnife.bind(this);

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }

    @OnClick({R.id.back_iv, R.id.confirm_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                mActivity.finish();
                break;
            case R.id.confirm_btn:

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
}
