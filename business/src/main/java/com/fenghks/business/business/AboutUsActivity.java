package com.fenghks.business.business;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fenghks.business.BaseActivity;
import com.fenghks.business.R;
import com.fenghks.business.tools.GlideCircleTransform;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.VibratorUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.tl_toolbar)
    Toolbar tl_toolbar;
    @BindView(R.id.tv_version)
    TextView tv_version;
    @BindView(R.id.tv_check_new)
    TextView tv_check_new;
    @BindView(R.id.tv_contact_service)
    TextView tv_contact_service;
    @BindView(R.id.iv_logo)
    ImageView iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        tl_toolbar.setTitle(R.string.setting_about_us);
        setSupportActionBar(tl_toolbar);

        tv_version.setText(CommonUtil.getAppVersion(this));
        Glide.with(mActivity).load(R.mipmap.ic_logo).
                transform(new GlideCircleTransform(mActivity)).
                into(iv_logo);
        tv_check_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mActivity);
                CommonUtil.checkUpdate(mActivity,true);
            }
        });
        tv_contact_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mActivity);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse(getString(R.string.dial_service_phone));
                intent.setData(data);
                startActivity(intent);
            }
        });
    }
}
