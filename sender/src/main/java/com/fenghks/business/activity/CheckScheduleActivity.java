package com.fenghks.business.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.fenghks.business.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by fenghuo on 2017/10/9.
 */

public class CheckScheduleActivity extends BaseActivity {
    public static final String TAG = "CheckScheduleActivity";
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.shop_name)
    TextView shopName;
    @BindView(R.id.nav_btn)
    Button navBtn;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    LatLng destination = new LatLng(29.591462, 106.571911);//店铺位置  测试

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_schedule);
        ButterKnife.bind(this);

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }


    @OnClick({R.id.back_iv, R.id.nav_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                mActivity.finish();
                break;
            case R.id.nav_btn:
                //跳转路线规划导航界面
                AmapNaviPage.getInstance().showRouteActivity(mContext, new AmapNaviParams(null, null, new Poi("五里店", destination, ""), AmapNaviType.DRIVER), new INaviInfoCallback() {
                    @Override
                    public void onInitNaviFailure() {
                        Log.d(TAG, "onInitNaviFailure");
                    }

                    @Override
                    public void onGetNavigationText(String s) {
                        Log.d(TAG, "onGetNavigationText");
                    }

                    @Override
                    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
                        Log.d(TAG, "onLocationChange");
                    }

                    @Override
                    public void onArriveDestination(boolean b) {
                        Log.d(TAG, "onArriveDestination");
                    }

                    @Override
                    public void onStartNavi(int i) {
                        Log.d(TAG, "onStartNavi");
                    }

                    @Override
                    public void onCalculateRouteSuccess(int[] ints) {
                        Log.d(TAG, "onCalculateRouteSuccess");
                    }

                    @Override
                    public void onCalculateRouteFailure(int i) {
                        Log.d(TAG, "onCalculateRouteFailure");
                    }

                    @Override
                    public void onStopSpeaking() {
                        Log.d(TAG, "onStopSpeaking");
                    }
                });
                break;
        }
    }
}
