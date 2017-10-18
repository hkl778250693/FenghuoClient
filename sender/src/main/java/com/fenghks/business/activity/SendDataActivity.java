package com.fenghks.business.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fenghks.business.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by fenghuo on 2017/10/10.
 */

public class SendDataActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.date_choose_rl)
    RelativeLayout dateChooseRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);
        ButterKnife.bind(this);

        toolBar.setTitle(R.string.send_data);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        refreshLayout.setEnableLoadmore(false);  //关闭上拉加载功能
        refreshLayout.setDisableContentWhenRefresh(false);  //是否在刷新的时候禁止内容的一切手势操作（默认false）
    }

    @OnClick({R.id.back_iv, R.id.date_choose_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                mActivity.finish();
                break;
            case R.id.date_choose_rl:
                break;
        }
    }
}
