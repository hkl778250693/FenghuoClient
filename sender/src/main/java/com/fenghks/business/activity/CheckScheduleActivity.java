package com.fenghks.business.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.fenghks.business.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fenghuo on 2017/10/9.
 */

public class CheckScheduleActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_schedule);
        ButterKnife.bind(this);
        toolBar.setTitle(R.string.schedule);
    }

    @OnClick(R.id.back_iv)
    public void onViewClicked() {
        mActivity.finish();
    }
}
