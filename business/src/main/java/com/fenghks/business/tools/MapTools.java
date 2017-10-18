package com.fenghks.business.tools;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.bean.Business;
import com.fenghks.business.utils.GPSUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fei on 2017/5/1.
 */

public class MapTools {

    /**
     * 创建地图标记点
     * @param context
     * @param lat 为0时，添加店铺位置
     * @param lon
     * @param type 0店铺，1客户，2配送员
     * @return
     */
    public static MarkerOptions createMaket(Context context,double lat,double lon,String content,int type){
        MarkerOptions markerOption = new MarkerOptions();
        if(lat == 0){
            Business shop = FenghuoApplication.shops.get(FenghuoApplication.currentShop);
            double[] location = GPSUtil.bd09_To_Gcj02(shop.getLat(),shop.getLon());
            LatLng latlng = new LatLng(location[0],location[1]);
            markerOption.position(latlng);
            type = 0;
            content = shop.getName();
        }else {
            markerOption.position(new LatLng(lat,lon));
        }

        int makerid = -1;
        String title = null;
        switch (type) {
            case 0:
                makerid = R.mipmap.ic_maker_shop;
                title = context.getString(R.string.loc_shop);
                break;
            case 1:
                makerid = R.mipmap.ic_maker_customer;
                title = context.getString(R.string.loc_customer);
                break;
            case 2:
                makerid = R.mipmap.ic_maker_sender;
                title = context.getString(R.string.loc_sender);
                break;
        }
        markerOption.title(title).snippet(content);

        markerOption.draggable(false);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(context.getResources(),makerid)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(false);//设置marker平贴地图效果
        return markerOption;
    }

    public static void drawLine(AMap aMap,List<LatLng> latLngs){
        Polyline polyline =aMap.addPolyline(new PolylineOptions().
                addAll(latLngs).width(6).color(Color.argb(255, 65, 105, 225)));
    }

    public static AMap initAmap(Context context, final AMap aMap, ArrayList<MarkerOptions> makers){

        aMap.setTrafficEnabled(true);

        if(null == makers){
            Business shop = FenghuoApplication.shops.get(FenghuoApplication.currentShop);
            double[] shop_loc = GPSUtil.bd09_To_Gcj02(shop.getLat(),shop.getLon());
            LatLng shop_latlng = new LatLng(shop_loc[0],shop_loc[1]);
            aMap.addMarker(createMaket(context,shop_loc[0],shop_loc[1],shop.getName(),0));
            aMap.animateCamera(CameraUpdateFactory.newLatLng(shop_latlng));
            aMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }else if(makers.size() == 1) {
            MarkerOptions maker = makers.get(0);
            aMap.addMarker(maker);
            aMap.animateCamera(CameraUpdateFactory.newLatLng(maker.getPosition()));
            aMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }else {
            aMap.addMarkers(makers,true);
            List<LatLng> list = new ArrayList<>();
            for(MarkerOptions maker : makers){
                list.add(maker.getPosition());
            }
            drawLine(aMap,list);
        }

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                List<Marker> makers = aMap.getMapScreenMarkers();
                for(Marker maker : makers){
                    maker.hideInfoWindow();
                }
            }
        });
        return aMap;
    }
}
