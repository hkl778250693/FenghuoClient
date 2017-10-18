package com.fenghks.business.activity;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.fenghks.business.R;
import com.fenghks.business.utils_amap.AnimationUtil;
import com.fenghks.business.utils_amap.Constants;
import com.fenghks.business.utils_amap.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fenghuo on 2017/10/13.
 */

public class GrabOrderMapActivity extends BaseActivity implements AMap.OnMarkerClickListener, AMap.OnMapLoadedListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter {
    public static final String TAG = "GrabOrderMapActivity";
    @BindView(R.id.map)
    MapView mapView;
    //初始化地图控制器对象
    AMap aMap;
    @BindView(R.id.back_tv)
    TextView backTv;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析aMapLocation获取相应内容。
                    StringBuilder sb = new StringBuilder();
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    Date date = new Date(aMapLocation.getTime());
                    df.format(date);
                    sb.append(aMapLocation.getLocationType())     //获取当前定位结果来源，如网络定位结果，详见定位类型表
                            .append(aMapLocation.getLatitude()).append("，")   //获取纬度
                            .append(aMapLocation.getLongitude()).append("，")  //获取经度
                            .append(aMapLocation.getAccuracy()).append("，")   //获取精度信息
                            .append(aMapLocation.getAddress()).append("，")    //地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                            .append(aMapLocation.getCountry()).append("，")   //国家信息
                            .append(aMapLocation.getProvince()).append("，") //省信息
                            .append(aMapLocation.getCity()).append("，")     //城市信息
                            .append(aMapLocation.getDistrict()).append("，")//城区信息
                            .append(aMapLocation.getStreet()).append("，")   //街道信息
                            .append(aMapLocation.getStreetNum()).append("，") //街道门牌号信息
                            .append(aMapLocation.getCityCode()).append("，")  //城市编码
                            .append(aMapLocation.getAdCode()).append("，")   //地区编码
                            .append(aMapLocation.getAoiName()).append("，")  //获取当前定位点的AOI信息
                            .append(aMapLocation.getBuildingId()).append("，")//获取当前室内定位的建筑物Id
                            .append(aMapLocation.getFloor()).append("，")   //获取当前室内定位的楼层
                            .append(aMapLocation.getGpsAccuracyStatus());  //获取GPS的当前状态
                    Log.d(TAG, "当前定位的信息为：" + sb.toString());
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.d(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                }
            }
        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private MarkerOptions markerOption;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grab_order_map);
        ButterKnife.bind(this);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);

        initMap(); //初始化地图
    }

    @OnClick({R.id.map, R.id.back_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.map:
                break;
            case R.id.back_tv:
                mActivity.finish();
                break;
        }
    }

    public void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            //设置相应事件监听
            setMapListener();
        }
        //定位style
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        // 自定义定位蓝点图标
        /*myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.mipmap.gps_point));*/
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(2);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        aMap.getUiSettings().setMyLocationButtonEnabled(true);  //设置默认定位按钮是否显示，非必需设置
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
        //初始化定位
        mLocationClient = new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    private void setMapListener() {
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        //aMap.setInfoWindowAdapter(null);
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        //文字显示标注，可以设置显示内容，位置，字体大小颜色，背景色旋转角度,Z值等
        /*TextOptions textOptions = new TextOptions().position(Constants.BEIJING)
                .text("Text").fontColor(Color.BLACK)
                .backgroundColor(Color.BLUE).fontSize(30).rotate(20).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
                .zIndex(1.f).typeface(Typeface.DEFAULT_BOLD);
        aMap.addText(textOptions);*/

        AnimationUtil.growMarker(aMap,Constants.DAPIN);
        AnimationUtil.growMarker(aMap,Constants.GUANYINQIAO);
        AnimationUtil.growMarker(aMap,Constants.HONGQIHEGOU);
        AnimationUtil.growMarker(aMap,Constants.WULIDIAN);
        AnimationUtil.growMarker(aMap,Constants.JIEFANGBEI);
        /*aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(Constants.DAPIN).title("抢这单")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker))
                .snippet("大坪有一单").draggable(false));

        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(Constants.GUANYINQIAO).title("抢这单")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker))
                .snippet("观音桥有一单").draggable(false));

        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(Constants.HONGQIHEGOU).title("抢这单")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker))
                .snippet("红旗河沟有一单").draggable(false));

        // 动画效果
       *//* ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        giflist.add(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
        giflist.add(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));*//*
        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(Constants.WULIDIAN).title("抢这单").icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker))
                .draggable(false).period(10));

        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(Constants.JIEFANGBEI).title("抢这单")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker))
                .snippet("解放碑有一单").draggable(false));*/

        //drawMarkers();// 添加10个带有系统默认icon的marker
    }

    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(final Marker marker, final LatLng position) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        Point startPoint = proj.toScreenLocation(Constants.GUANYINQIAO);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1000;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * position.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * position.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    /**
     * 监听自定义infowindow窗口的infowindow事件回调
     */
    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow = getLayoutInflater().inflate(
                R.layout.custom_info_window, null);

        render(marker, infoWindow);
        return infoWindow;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(Marker marker, View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.badge);
        imageView.setImageResource(R.drawable.ic_grab_order_true_48dp);
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            /*SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
                    titleText.length(), 0);
            titleUi.setTextSize(15);*/
            titleUi.setText(title);
        } else {
            titleUi.setText("");
        }
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            /*SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
                    snippetText.length(), 0);
            snippetUi.setTextSize(20);*/
            snippetUi.setText(snippet);
        } else {
            snippetUi.setText("");
        }
    }

    /**
     * 监听自定义infowindow窗口的infocontents事件回调
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * 监听点击infowindow窗口事件回调
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        ToastUtil.show(this, "你点击了infoWindow窗口" + marker.getTitle());
    }

    @Override
    public void onMapLoaded() {
        addMarkersToMap();// 往地图上添加marker
        // 设置所有maker显示在当前可视区域地图中
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(Constants.DAPIN).include(Constants.GUANYINQIAO)
                .include(Constants.HONGQIHEGOU).include(Constants.WULIDIAN).include(Constants.JIEFANGBEI).build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (aMap != null) {
            jumpPoint(marker,marker.getPosition());
        }
        return false;
    }
}
