package com.fenghks.business.orders;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.fenghks.business.BaseActivity;
import com.fenghks.business.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.tl_toolbar)
    Toolbar tl_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);

        tl_toolbar.setTitle(R.string.order_detail);
        setSupportActionBar(tl_toolbar);
    }
}
