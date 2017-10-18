package com.fenghks.business.utils_amap;

import android.view.animation.LinearInterpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.fenghks.business.R;

/**
 * Created by fenghuo on 2017/10/17.
 */

public class AnimationUtil {
    public static void growMarker(AMap aMap, LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().anchor(0.5f, 0.5f).position(latLng).title("抢这单").snippet("这里有一单，要不要接？").draggable(false).icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
        Marker marker = aMap.addMarker(markerOptions);
        //地上生长的Marker
        Animation animation = new ScaleAnimation(0,1,0,1);
        animation.setInterpolator(new LinearInterpolator());
        //整个移动所需要的时间
        animation.setDuration(1000);
        //设置动画
        marker.setAnimation(animation);
        //开始动画
        marker.startAnimation();
    }
}
