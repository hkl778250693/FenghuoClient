package com.fenghks.business.orders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.fenghks.business.AppConstants;
import com.fenghks.business.BaseActivity;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.bean.Business;
import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.Sender;
import com.fenghks.business.tools.MapTools;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.GPSUtil;
import com.fenghks.business.utils.ParseUtils;

import org.json.JSONArray;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AMapActivity extends BaseActivity {

    private Order order;
    private Business shop;
    private Sender sender;

    @BindView(R.id.tl_toolbar)
    Toolbar tl_toolbar;
    @BindView(R.id.amap_view)
    MapView amap_view;

    AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap);
        ButterKnife.bind(this);

        setSupportActionBar(tl_toolbar);
        tl_toolbar.setTitle(R.string.order_map);

        amap_view.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = amap_view.getMap();
        }
        
        Intent initIntent = getIntent();
        if (initIntent != null) {
            order = (Order) initIntent.getSerializableExtra(AppConstants.EXTRA_ORDER);
        }
        shop = FenghuoApplication.shops.get(FenghuoApplication.currentShop);
        ArrayList<MarkerOptions> makers = null;
        if(order.getState()>3 || order.getState()<2 ){
            double[] shop_loc = GPSUtil.bd09_To_Gcj02(shop.getLat(),shop.getLon());
            makers = new ArrayList<>();
            makers.add(MapTools.createMaket(this,shop_loc[0],shop_loc[1],shop.getName(),0));

            if(order.getLat()>0){
                double[] order_loc = GPSUtil.bd09_To_Gcj02(order.getLat(),order.getLon());
                makers.add(MapTools.createMaket(this,order_loc[0],order_loc[1],
                        order.getClientfrom() + " " + order.getOrdercode() + "\n" +
                        order.getCustomername() + " " + order.getCustomerphone() + "\n" +
                        order.getSendaddress(),1));
            }
        }else{
            Map<String,String> map = new HashMap<>();
            map.put("queryField","id");
            map.put("condition",order.getSenderid()+"");
            x.http().post(CommonUtil.genGetParam(CommonUtil.GET_SENDER_DETAIL_URL, map), new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println(result);
                    JSONArray objs = ParseUtils.parseDataArray(mContext,result);
                    if(null != objs){
                        Sender sender = ParseUtils.parseSender(objs.optJSONObject(0));
                        ArrayList<MarkerOptions> makers = new ArrayList<>();
                        List<LatLng> latLngs = new ArrayList<LatLng>();
                        double[] shop_loc = GPSUtil.bd09_To_Gcj02(shop.getLat(),shop.getLon());
                        /*makers.add(MapTools.createMaket(mActivity,shop_loc[0],shop_loc[1],shop.getName(),0));*/
                        latLngs.add(new LatLng(shop_loc[0],shop_loc[1]));

                        double[] sender_loc = GPSUtil.bd09_To_Gcj02(sender.getLat(),sender.getLon());
                        makers.add(MapTools.createMaket(mActivity,sender_loc[0],sender_loc[1],
                                sender.getName() + " " + sender.getPhone(),2));
                        latLngs.add(new LatLng(sender_loc[0],sender_loc[1]));
                        if(order.getLat()>0){
                            double[] order_loc = GPSUtil.bd09_To_Gcj02(order.getLat(),order.getLon());
                            makers.add(MapTools.createMaket(mActivity,order_loc[0],order_loc[1],
                                    order.getClientfrom() + " " + order.getOrdercode() + "\n" +
                                    order.getCustomername() + " " + order.getCustomerphone() + "\n" +
                                    order.getSendaddress(),1));
                            latLngs.add(new LatLng(order_loc[0],order_loc[1]));
                        }
                        aMap.addMarkers(makers,true);

                        MapTools.drawLine(aMap,latLngs);
                    }else {
                        //CommonUtil.handleErr(mActivity,result);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    CommonUtil.networkErr(ex,isOnCallback);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }

        MapTools.initAmap(this,aMap,makers);
    }

    @Override
    protected void onResume() {
        super.onResume();
        amap_view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        amap_view.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        amap_view.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        amap_view.onDestroy();
    }
}
