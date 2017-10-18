package com.fenghks.business;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.fenghks.business.utils.CommonUtil;

import butterknife.ButterKnife;
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
        mActivity = this;
        mContext = getApplicationContext();
        if(FenghuoApplication.business == null){
            /*CommonUtil.autoLogin(mContext,AppConstants.FROM_ACTIVITY);
            Intent intent = new Intent(mActivity, SplashActivity.class);
            startActivity(intent);*/
        }
        getSwipeBackLayout().setEdgeSize((int)(getResources().getDisplayMetrics().density * 12));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mActivity = this;
        mContext = getApplicationContext();
        if(FenghuoApplication.business == null){
            /*CommonUtil.autoLogin(mContext,AppConstants.FROM_ACTIVITY);
            Intent intent = new Intent(mActivity, SplashActivity.class);
            startActivity(intent);*/
        }
        getSwipeBackLayout().setEdgeSize((int)(getResources().getDisplayMetrics().density * 8));
    }
}
