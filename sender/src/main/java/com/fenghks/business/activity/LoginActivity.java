package com.fenghks.business.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.fenghks.business.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by fenghuo on 2017/10/16.
 */

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.phone_num_edit)
    EditText phoneNumEdit;
    @BindView(R.id.pw_edit)
    EditText pwEdit;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_login);

        mContext = LoginActivity.this;
        ButterKnife.bind(this);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        phoneNumEdit.requestFocus();
    }

    @OnClick(R.id.login_btn)
    public void onViewClicked() {
        Intent intent = new Intent(mContext,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
