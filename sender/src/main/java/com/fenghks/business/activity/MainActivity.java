package com.fenghks.business.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.adapter.HomePageViewPagerAdapter;
import com.fenghks.business.adapter.SendOrderListAdapter;
import com.fenghks.business.custom_view.CommonPopupWindow;
import com.fenghks.business.fragment.GrabOrderFragment;
import com.fenghks.business.fragment.MineFragment;
import com.fenghks.business.fragment.OrdersFragment;
import com.fenghks.business.fragment.OthersFragment;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.SPUtil;
import com.fenghks.business.utils.VibratorUtil;
import com.fenghks.business.zxing.android.CaptureActivity;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.sender_view_pager)
    ViewPager senderViewPager;
    @BindView(R.id.grabOrder_rb)
    RadioButton grabOrderRb;
    @BindView(R.id.orders_rb)
    RadioButton ordersRb;
    @BindView(R.id.other_rb)
    RadioButton otherRb;
    @BindView(R.id.mine_rb)
    RadioButton mineRb;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.merge_order)
    TextView mergeOrder;
    @BindView(R.id.tl_toolbar)
    Toolbar tlToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;

    private Activity mActivity;
    private long exitTime = 0;//退出程序
    private CommonPopupWindow window;
    private RadioButton cancleRb, doRb;
    private static final int REQUEST_CODE_SCAN = 0x0000;
    private static final int PERMISSION_REQUEST_CODE = 1;  //6.0之后需要动态申请权限，   请求码
    private List<String> permissionList = new ArrayList<>();  //权限集合
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION}; //权限数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mActivity = MainActivity.this;
        String sha1 = CommonUtil.sHA1(getApplicationContext());
        Log.d(TAG, "sha1值为：" + sha1);

        registerPush();
        setViewPager();
        requestPermissions();
        setViewClickListener();
    }

    //设置viewPager
    private void setViewPager() {
        List<Fragment> fragmentList = new ArrayList<>();
        //添加fragment到list
        fragmentList.add(new GrabOrderFragment());
        fragmentList.add(new OrdersFragment());
        fragmentList.add(new OthersFragment());
        fragmentList.add(new MineFragment());
        HomePageViewPagerAdapter adapter = new HomePageViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        senderViewPager.setAdapter(adapter);
        grabOrderRb.setChecked(true);
        tlToolbar.setTitle(R.string.crazy_fire_grab_order);
        setSupportActionBar(tlToolbar);
    }


    //点击事件
    public void setViewClickListener() {
        senderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        appbarLayout.setVisibility(View.VISIBLE);
                        tlToolbar.setTitle(R.string.crazy_fire_grab_order);
                        grabOrderRb.setChecked(true);
                        ordersRb.setChecked(false);
                        otherRb.setChecked(false);
                        mineRb.setChecked(false);
                        break;
                    case 1:
                        appbarLayout.setVisibility(View.VISIBLE);
                        tlToolbar.setTitle(R.string.crazy_fire_orders);
                        grabOrderRb.setChecked(false);
                        ordersRb.setChecked(true);
                        otherRb.setChecked(false);
                        mineRb.setChecked(false);
                        break;
                    case 2:
                        appbarLayout.setVisibility(View.VISIBLE);
                        tlToolbar.setTitle(R.string.other);
                        grabOrderRb.setChecked(false);
                        ordersRb.setChecked(false);
                        otherRb.setChecked(true);
                        mineRb.setChecked(false);
                        break;
                    case 3:
                        appbarLayout.setVisibility(View.VISIBLE);
                        tlToolbar.setTitle("");
                        grabOrderRb.setChecked(false);
                        ordersRb.setChecked(false);
                        otherRb.setChecked(false);
                        mineRb.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void registerPush() {
        // 开启logcat输出，方便debug，发布时请关闭
        //XGPushConfig.enableDebug(this, true);
        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
        // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
        // 具体可参考详细的开发指南
        // 传递的参数为ApplicationContext
        //XGPushManager.registerPush(mContext);
        XGPushManager.registerPush(mActivity, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object obj, int flag) {
                Log.d(TAG, "XGPush注册成功,Token值为：" + obj);
                FenghuoApplication.xg_token = obj.toString();
                SPUtil.putString(mActivity, "xg_token", FenghuoApplication.xg_token);
                /*if (receivePush) {
                    CommonUtil.registerPushService(mActivity);
                }*/
            }

            @Override
            public void onFail(Object obj, int errCode, String msg) {
                Log.d(TAG, "TPush注册失败,错误码为：" + errCode + ",错误信息：" + msg);
            }
        });
    }


    @OnClick({R.id.grabOrder_rb, R.id.orders_rb, R.id.other_rb, R.id.mine_rb, R.id.merge_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.grabOrder_rb:
                appbarLayout.setVisibility(View.VISIBLE);
                VibratorUtil.vibrateOnce(mActivity);
                tlToolbar.setTitle(R.string.crazy_fire_grab_order);
                senderViewPager.setCurrentItem(0);
                break;
            case R.id.orders_rb:
                appbarLayout.setVisibility(View.VISIBLE);
                VibratorUtil.vibrateOnce(mActivity);
                tlToolbar.setTitle(R.string.crazy_fire_orders);
                senderViewPager.setCurrentItem(1);
                break;
            case R.id.other_rb:
                appbarLayout.setVisibility(View.VISIBLE);
                VibratorUtil.vibrateOnce(mActivity);
                tlToolbar.setTitle(R.string.other);
                senderViewPager.setCurrentItem(2);
                break;
            case R.id.mine_rb:
                appbarLayout.setVisibility(View.GONE);
                VibratorUtil.vibrateOnce(mActivity);
                tlToolbar.setTitle("");
                senderViewPager.setCurrentItem(3);
                break;
            case R.id.merge_order:
                //并单提示
                VibratorUtil.vibrateOnce(mActivity);
                MergeOrderWindow();
                break;
        }
    }

    /**
     * 并单操作提示
     */
    private void MergeOrderWindow() {
        window = new CommonPopupWindow.Builder(mActivity)
                .setView(R.layout.popupwindow_contact_service)
                .setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.AnimUp)
                .setBackGroundLevel(0.5F)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        TextView tips = (TextView) view.findViewById(R.id.content);
                        tips.setText(R.string.merge_order_tips);
                        cancleRb = (RadioButton) view.findViewById(R.id.cancel_rb);
                        doRb = (RadioButton) view.findViewById(R.id.do_rb);
                        doRb.setText(R.string.confirm);
                    }
                })
                .setOutsideTouchable(true)
                .create();
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
                //跳转扫码界面
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                window.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String codeResult = data.getStringExtra("codedContent");
                Bitmap bitmap = data.getParcelableExtra("codedBitmap");
                Log.d(TAG, "codeResult = " + codeResult);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //View view = MenuItemCompat.getActionView(item);
        switch (item.getItemId()) {
            case R.id.menu_add:
                VibratorUtil.vibrateOnce(mActivity);
                break;
            case R.id.map_nav:
                VibratorUtil.vibrateOnce(mActivity);
                Intent intent = new Intent(mActivity, GrabOrderMapActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 动态申请权限，如果6.0以上则弹出需要的权限选择框，以下则直接运行
     */
    private void requestPermissions() {
        //检查权限(6.0以上做权限判断)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                /*if(ActivityCompat.shouldShowRequestPermissionRationale(UserDetailInfoActivity.this,Manifest.permission.CAMERA)){
                    //已经禁止提示了
                    Toast.makeText(UserDetailInfoActivity.this, "您已禁止该权限，需要重新开启！", Toast.LENGTH_SHORT).show();
                }else {
                    ActivityCompat.requestPermissions(UserDetailInfoActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                }*/
                permissionList.add(permissions[0]);
            }
            if (ContextCompat.checkSelfPermission(mActivity, permissions[1]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[1]);
            }
            if (ContextCompat.checkSelfPermission(mActivity, permissions[2]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[2]);
            }

            Log.i("requestPermissions", "权限集合的长度为：" + permissionList.size());
            if (!permissionList.isEmpty()) {  //判断权限集合是否为空
                String[] permissionArray = permissionList.toArray(new String[permissionList.size()]);
                ActivityCompat.requestPermissions(mActivity, permissionArray, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(mActivity, "必须同意所有权限才能操作哦！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    //用户同意授权
                } else {
                    //用户拒绝授权
                    Toast.makeText(mActivity, "您拒绝了授权！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 捕捉返回事件按钮
     * 如果 Activity继承 TabActivity,用onKeyDown无响应，
     * 可以改用 dispatchKeyEvent
     * <p/>
     * 一般的 Activity 用 onKeyDown就可以了
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    /**
     * 退出程序
     */
    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Log.d("exitTime==========>", System.currentTimeMillis() - exitTime + "");
            //-------------Activity.this的context 返回当前activity的上下文，属于activity，activity 摧毁他就摧毁
            //-------------getApplicationContext() 返回应用的上下文，生命周期是整个应用，应用摧毁它才摧毁
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
