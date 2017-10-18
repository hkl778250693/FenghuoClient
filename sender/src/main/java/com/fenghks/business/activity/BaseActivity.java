package com.fenghks.business.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by Fei on 2017/1/19.
 */

public class BaseActivity extends SwipeBackActivity {
    protected Activity mActivity;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //去掉标题栏
        mActivity = this;
        mContext = getApplicationContext();
        getSwipeBackLayout().setEdgeSize((int)(getResources().getDisplayMetrics().density * 12));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //去掉标题栏
        mActivity = this;
        mContext = getApplicationContext();
        getSwipeBackLayout().setEdgeSize((int)(getResources().getDisplayMetrics().density * 8));
    }
}
