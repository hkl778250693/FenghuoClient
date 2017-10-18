package com.fenghks.business.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.fenghks.business.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by fenghuo on 2017/10/9.
 */

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.suggestion)
    LinearLayout suggestion;
    @BindView(R.id.offline_map)
    LinearLayout offlineMap;
    @BindView(R.id.version)
    LinearLayout version;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }

    @OnClick({R.id.back_iv, R.id.suggestion, R.id.offline_map, R.id.version})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.back_iv:
                mActivity.finish();
                break;
            case R.id.suggestion:
                intent = new Intent(mContext,SuggestionFeedBackActivity.class);
                startActivity(intent);
                break;
            case R.id.offline_map:
                intent = new Intent(mContext,OfflineMapActivity.class);
                startActivity(intent);
                break;
            case R.id.version:
                break;
        }
    }
}
