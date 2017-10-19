package com.fenghks.business.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.fenghks.business.R;
import com.fenghks.business.custom_view.CommonPopupWindow;
import com.fenghks.business.offline_map.OfflineDownloadedAdapter;
import com.fenghks.business.offline_map.OfflineListAdapter;
import com.fenghks.business.offline_map.OfflinePagerAdapter;
import com.fenghks.business.utils_amap.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by fenghuo on 2017/10/18.
 */

public class OfflineMapActivity extends BaseActivity implements OfflineMapManager.OfflineMapDownloadListener, OfflineMapManager.OfflineLoadedListener, ViewPager.OnPageChangeListener {
    public static final String TAG = "OfflineMapActivity";
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.content_viewpager)
    ViewPager mContentViewPage;
    @BindView(R.id.download_list_rb)
    RadioButton downloadListRb;
    @BindView(R.id.downloaded_list_rb)
    RadioButton downloadedListRb;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    private OfflineMapManager amapManager = null;// 离线地图下载控制器
    private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市
    private OfflineListAdapter adapter;
    private OfflineDownloadedAdapter mDownloadedAdapter;
    private PagerAdapter mPageAdapter;
    // 刚进入该页面时初始化弹出的dialog
    private ProgressDialog initDialog;
    private ExpandableListView mAllOfflineMapList;
    private ListView mDownLoadedList;
    private OfflineMapCity mMapCity;// 离线下载城市
    private List<OfflineMapCity> cities = new ArrayList<OfflineMapCity>();
    private boolean cityDownLoadState = false;

    /**
     * 更新所有列表
     */
    private final static int UPDATE_LIST = 0;
    /**
     * 显示toast log
     */
    private final static int SHOW_MSG = 1;
    private final static int DISMISS_INIT_DIALOG = 2;
    private final static int SHOW_INIT_DIALOG = 3;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LIST:
                    if (mContentViewPage.getCurrentItem() == 0) {
                        ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
                    } else {
                        mDownloadedAdapter.notifyDataChange();
                    }
                    break;
                case SHOW_MSG:
//				Toast.makeText(OfflineMapActivity.this, (String) msg.obj,
//						Toast.LENGTH_SHORT).show()
                    ToastUtil.showShortToast(OfflineMapActivity.this, (String) msg.obj);
                    break;
                case DISMISS_INIT_DIALOG:
                    initDialog.dismiss();
                    handler.sendEmptyMessage(UPDATE_LIST);
                    break;
                case SHOW_INIT_DIALOG:
                    if (initDialog != null) {
                        initDialog.show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 声明AMapLocationClient类对象
     */
    public AMapLocationClient mLocationClient = null;
    /**
     * 声明AMapLocationClientOption对象
     */
    public AMapLocationClientOption mLocationOption = null;
    private CommonPopupWindow window;
    private RadioButton cancleRb, doRb;
    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_map);
        ButterKnife.bind(this);

        init();
    }

    /**
     * 定位当前城市
     */
    public void startLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        /**
                         *  判断当前城市离线地图是否已下载
                         */
                        cities = mDownloadedAdapter.getCitiesList();
                        for (int i = 0; i < cities.size(); i++) {
                            if (cities.get(i).getState() == OfflineMapStatus.SUCCESS) {
                                cityDownLoadState = true;
                                break;
                            }
                        }
                        if (cityDownLoadState) {
                            Snackbar.make(coordinatorLayout, "当前定位城市 '" + aMapLocation.getCity() + "' 离线地图已下载", Snackbar.LENGTH_LONG).show();  //当前城市离线地图已下载
                        } else {
                            //可在其中解析aMapLocation获取相应内容。
                            downloadMapWindow(aMapLocation.getCity(), "当前定位到 ‘" + aMapLocation.getProvince() + "' '" + aMapLocation.getCity() + "' ，是否下载当前城市的离线地图数据？");
                        }
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
        });
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
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

    /**
     * 弹出是否下载当前城市离线地图框
     */
    private void downloadMapWindow(final String cityName, String describeContent) {
        window = new CommonPopupWindow.Builder(mActivity)
                .setView(R.layout.popupwindow_contact_service)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .setAnimationStyle(R.style.AnimDown)
                .setBackGroundLevel(0.5F)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        content = (TextView) view.findViewById(R.id.content);
                        cancleRb = (RadioButton) view.findViewById(R.id.cancel_rb);
                        doRb = (RadioButton) view.findViewById(R.id.do_rb);
                    }
                })
                .setOutsideTouchable(false)
                .create();
        content.setText(describeContent);
        doRb.setText(R.string.download);
        window.showAtLocation(coordinatorLayout, Gravity.CENTER, 0, 0);
        cancleRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        doRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                mContentViewPage.setCurrentItem(1);
                try {
                    amapManager.downloadByCityName(cityName);
                } catch (AMapException e) {
                    e.printStackTrace();
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

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 初始化如果已下载的城市多的话，会比较耗时
     */
    private void initDialog() {
        if (initDialog == null)
            initDialog = new ProgressDialog(this);
        initDialog.setMessage("正在获取离线城市列表");
        initDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        initDialog.setIndeterminate(false);
        initDialog.setCancelable(false);
        initDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissDialog() {
        if (initDialog != null) {
            initDialog.dismiss();
        }
    }

    /**
     * 初始化UI布局文件
     */
    private void init() {
        //构造离线地图类
        amapManager = new OfflineMapManager(this, this);
        //离线地图初始化完成监听
        amapManager.setOnOfflineLoadedListener(this);
        initDialog();
        downloadListRb.setChecked(true);
        startLocation(); //开始定位
    }

    private void initViewpage() {
        mPageAdapter = new OfflinePagerAdapter(mContentViewPage,
                mAllOfflineMapList, mDownLoadedList);
        mContentViewPage.setAdapter(mPageAdapter);
        mContentViewPage.setCurrentItem(0);
        mContentViewPage.addOnPageChangeListener(this);
    }

    /**
     * 初始化所有城市列表
     */
    public void initAllCityList() {
        // 扩展列表
        View provinceContainer = LayoutInflater.from(OfflineMapActivity.this)
                .inflate(R.layout.offline_province_listview, null);
        mAllOfflineMapList = (ExpandableListView) provinceContainer
                .findViewById(R.id.province_download_list);
        initProvinceListAndCityMap();
        // adapter = new OfflineListAdapter(provinceList, cityMap, amapManager,
        // OfflineMapActivity.this);
        adapter = new OfflineListAdapter(provinceList, amapManager,
                OfflineMapActivity.this);
        // 为列表绑定数据源
        mAllOfflineMapList.setAdapter(adapter);
        // adapter实现了扩展列表的展开与合并监听
        mAllOfflineMapList.setOnGroupCollapseListener(adapter);
        mAllOfflineMapList.setOnGroupExpandListener(adapter);
        mAllOfflineMapList.setGroupIndicator(null);
        OverScrollDecoratorHelper.setUpOverScroll(mAllOfflineMapList);
    }

    /**
     * sdk内部存放形式为<br>
     * 省份 - 各自子城市<br>
     * 北京-北京<br>
     * ...<br>
     * 澳门-澳门<br>
     * 概要图-概要图<br>
     * <br>
     * 修改一下存放结构:<br>
     * 概要图-概要图<br>
     * 直辖市-四个直辖市<br>
     * 港澳-澳门香港<br>
     * 省份-各自子城市<br>
     */
    private void initProvinceListAndCityMap() {
        List<OfflineMapProvince> lists = amapManager.getOfflineMapProvinceList();
        provinceList.add(null);
        provinceList.add(null);
        provinceList.add(null);
        // 添加3个null 以防后面添加出现 index out of bounds
        ArrayList<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// 以市格式保存直辖市、港澳、全国概要图
        ArrayList<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// 保存港澳城市
        ArrayList<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// 保存概要图

        for (int i = 0; i < lists.size(); i++) {
            OfflineMapProvince province = lists.get(i);
            if (province.getCityList().size() != 1) {
                // 普通省份
                provinceList.add(i + 3, province);
                // cityMap.put(i + 3, cities);
            } else {
                String name = province.getProvinceName();
                if (name.contains("香港")) {
                    gangaoList.addAll(province.getCityList());
                } else if (name.contains("澳门")) {
                    gangaoList.addAll(province.getCityList());
                } else if (name.contains("全国概要图")) {
                    gaiyaotuList.addAll(province.getCityList());
                } else {
                    // 直辖市
                    cityList.addAll(province.getCityList());
                }
            }
        }

        // 添加，概要图，直辖市，港口
        OfflineMapProvince gaiyaotu = new OfflineMapProvince();
        gaiyaotu.setProvinceName("概要图");
        gaiyaotu.setCityList(gaiyaotuList);
        provinceList.set(0, gaiyaotu);// 使用set替换掉刚开始的null

        OfflineMapProvince zhixiashi = new OfflineMapProvince();
        zhixiashi.setProvinceName("直辖市");
        zhixiashi.setCityList(cityList);
        provinceList.set(1, zhixiashi);

        OfflineMapProvince gaogao = new OfflineMapProvince();
        gaogao.setProvinceName("港澳");
        gaogao.setCityList(gangaoList);
        provinceList.set(2, gaogao);

        // cityMap.put(0, gaiyaotuList);// 在HashMap中第0位置添加全国概要图
        // cityMap.put(1, cityList);// 在HashMap中第1位置添加直辖市
        // cityMap.put(2, gangaoList);// 在HashMap中第2位置添加港澳

    }

    /**
     * 初始化已下载列表
     */
    public void initDownloadedList() {
        mDownLoadedList = (ListView) LayoutInflater.from(OfflineMapActivity.this).inflate(R.layout.offline_downloaded_list, null);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        mDownLoadedList.setLayoutParams(params);
        mDownloadedAdapter = new OfflineDownloadedAdapter(this, amapManager);
        mDownLoadedList.setAdapter(mDownloadedAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(mDownLoadedList);
    }

    /**
     * 暂停所有下载和等待
     */
    private void stopAll() {
        if (amapManager != null) {
            amapManager.stop();
        }
    }

    /**
     * 继续下载所有暂停中
     */
    private void startAllInPause() {
        if (amapManager == null) {
            return;
        }
        for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
            if (mapCity.getState() == OfflineMapStatus.PAUSE) {
                try {
                    amapManager.downloadByCityName(mapCity.getCity());
                } catch (AMapException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 取消所有<br>
     * 即：删除下载列表中除了已完成的所有<br>
     * 会在OfflineMapDownloadListener.onRemove接口中回调是否取消（删除）成功
     */
    private void cancelAll() {
        if (amapManager == null) {
            return;
        }
        for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
            if (mapCity.getState() == OfflineMapStatus.PAUSE) {
                amapManager.remove(mapCity.getCity());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (amapManager != null) {
            amapManager.destroy();
        }

        if (initDialog != null) {
            initDialog.dismiss();
            initDialog.cancel();
        }
    }

    private void logList() {
        ArrayList<OfflineMapCity> list = amapManager.getDownloadingCityList();

        for (OfflineMapCity offlineMapCity : list) {
            Log.i("amap-city-loading: ", offlineMapCity.getCity() + ","
                    + offlineMapCity.getState());
        }

        ArrayList<OfflineMapCity> list1 = amapManager
                .getDownloadOfflineMapCityList();

        for (OfflineMapCity offlineMapCity : list1) {
            Log.i("amap-city-loaded: ", offlineMapCity.getCity() + ","
                    + offlineMapCity.getState());
        }
    }

    /**
     * 离线地图下载回调方法
     */
    @Override
    public void onDownload(int status, int completeCode, String downName) {
        switch (status) {
            case OfflineMapStatus.SUCCESS:
                // changeOfflineMapTitle(OfflineMapStatus.SUCCESS, downName);
                break;
            case OfflineMapStatus.LOADING:
                Log.d(TAG, "download: " + completeCode + "%" + ","
                        + downName);
                // changeOfflineMapTitle(OfflineMapStatus.LOADING, downName);
                break;
            case OfflineMapStatus.UNZIP:
                Log.d(TAG, "unzip: " + completeCode + "%" + "," + downName);
                // changeOfflineMapTitle(OfflineMapStatus.UNZIP);
                // changeOfflineMapTitle(OfflineMapStatus.UNZIP, downName);
                break;
            case OfflineMapStatus.WAITING:
                Log.d(TAG, "WAITING: " + completeCode + "%" + ","
                        + downName);
                break;
            case OfflineMapStatus.PAUSE:
                Log.d(TAG, "pause: " + completeCode + "%" + "," + downName);
                break;
            case OfflineMapStatus.STOP:
                break;
            case OfflineMapStatus.ERROR:
                Log.e(TAG, "download: " + " ERROR " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_AMAP:
                Log.e(TAG, "download: " + " EXCEPTION_AMAP " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
                Log.e(TAG, "download: " + " EXCEPTION_NETWORK_LOADING "
                        + downName);
                Toast.makeText(OfflineMapActivity.this, "网络异常", Toast.LENGTH_SHORT)
                        .show();
                amapManager.pause();
                break;
            case OfflineMapStatus.EXCEPTION_SDCARD:
                Log.e(TAG, "download: " + " EXCEPTION_SDCARD "
                        + downName);
                break;
            default:
                break;
        }

        // changeOfflineMapTitle(status, downName);
        handler.sendEmptyMessage(UPDATE_LIST);

    }

    @Override
    public void onCheckUpdate(boolean hasNew, String name) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onCheckUpdate " + name + " : " + hasNew);
        Message message = new Message();
        message.what = SHOW_MSG;
        message.obj = "CheckUpdate " + name + " : " + hasNew;
        handler.sendMessage(message);
    }

    @Override
    public void onRemove(boolean success, String name, String describe) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onRemove " + name + " : " + success + " , "
                + describe);
        handler.sendEmptyMessage(UPDATE_LIST);

        Message message = new Message();
        message.what = SHOW_MSG;
        message.obj = "onRemove " + name + " : " + success + " , " + describe;
        handler.sendMessage(message);

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        switch (arg0) {
            case 0:
                downloadListRb.setChecked(true);
                break;
            case 1:
                downloadedListRb.setChecked(true);
                break;
        }
        handler.sendEmptyMessage(UPDATE_LIST);

    }

    @Override
    public void onVerifyComplete() {
        initAllCityList();
        initDownloadedList();
        initViewpage();
        dissmissDialog();
    }

    @OnClick({R.id.back_iv, R.id.download_list_rb, R.id.downloaded_list_rb})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                mActivity.finish();
                break;
            case R.id.download_list_rb:
                mContentViewPage.setCurrentItem(0);
                mDownloadedAdapter.notifyDataChange();
                break;
            case R.id.downloaded_list_rb:
                mContentViewPage.setCurrentItem(1);
                mDownloadedAdapter.notifyDataChange();
                break;
        }
    }
}
