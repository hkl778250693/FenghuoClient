package com.fenghks.business.utils_amap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.fenghks.business.AppConstants;

/**
 * Created by fenghuo on 2017/10/20.
 */

public class GeocoderUtil implements GeocodeSearch.OnGeocodeSearchListener {
    public static final String TAG = "GeocoderUtil";
    private Context context;
    private GeocodeSearch geocoderSearch;
    private LatLng latLng;

    public GeocoderUtil(Context context) {
        this.context = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        geocoderSearch = new GeocodeSearch(context);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 响应地理编码
     */
    private void getLatlon(String name, String cityCode) {
        GeocodeQuery query = new GeocodeQuery(name, cityCode);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * 返回地理坐标
     *
     * @return
     */
    public LatLng getLatLngByName(String name, String cityCode) {
        Log.d(TAG, "地址为：" + name + "         城市编码为：" + cityCode);
        getLatlon(name, cityCode);
        if (latLng == null) {
            return null;
        }
        return latLng;
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                    && geocodeResult.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                latLng = AMapUtil.convertToLatLng(address.getLatLonPoint());
                Log.d(TAG, "返回的坐标为：" + latLng);
                Intent broadcast = new Intent(AppConstants.ACTION_GEOCAODER_RESULT);
                broadcast.putExtra("latlng",latLng);
                context.sendBroadcast(broadcast);

            } else {
                Log.d(TAG, "没有搜索到相关数据");
            }
        } else {
            ToastUtil.showerror(context, i);
            Log.d(TAG, "服务错误码为：" + i);
        }
    }
}
