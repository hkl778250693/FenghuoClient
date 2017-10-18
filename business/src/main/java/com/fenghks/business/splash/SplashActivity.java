package com.fenghks.business.splash;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.business.MainActivity;
import com.fenghks.business.receiver.MessageService;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.ImageUtil;
import com.fenghks.business.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.activity_splash)
    RelativeLayout rl_splash;
    @BindView(R.id.iv_welcome)
    ImageView iv_welcome;
    @BindView(R.id.vp_welcome)
    ViewPager vp_welcome;
    @BindView(R.id.ci_indicator)
    CircleIndicator ci_indicator;

    private Animation anim;

    private final int SPLASH_DISPLAY_LENGTH = 1500; //延迟三秒

    private static final int SYSTEM_ALERT_REQUEST_CODE = 101; // 请求码

    private static final int READ_PHONE_STATE_CODE = 102; // 请求码
    private static boolean hasPermission = false;

    private int[] imageRes = { R.mipmap.welcome1, R.mipmap.welcome2, R.mipmap.welcome3 };
    private List<View> mlist = new ArrayList<View>();
    private ViewPagerAdapter madpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setSwipeBackEnable(false);

        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }

        Intent msgIntent = new Intent(this, MessageService.class);
        startService(msgIntent);

        getPermissions();
        CommonUtil.checkLogin(SplashActivity.this,AppConstants.FROM_SPLASH);
        //启动消息轮询服务
        /*Intent sIntent = new Intent(SplashActivity.this,MessageService.class);
        startService(sIntent);*/
        //检查更新
        CommonUtil.checkUpdate(SplashActivity.this,false);
        /*//启动定位服务
        Intent mIntent = new Intent(SplashActivity.this,LocationService.class);
        startService(mIntent);*/

        if (!SPUtil.getBoolean(this, AppConstants.IS_ACTIVATED)) {
            SPUtil.putBoolean(this, AppConstants.IS_ACTIVATED,true);
            //registerDevice();
            initViewPage();
        } else {
            //loadData();
            initSplash();
        }
    }

    //检查获取权限
    public void getPermissions() {
        if(Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                //为获取权限，检查用户是否被询问过并且拒绝了，如果是这样的话，给予更多解释
                /*if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                        Manifest.permission.READ_PHONE_STATE)){
                    //在界面上展示为什么需要读取联系人
                    Toast.makeText(this, "需要提供读取手机状态的权限才能正常工作", Toast.LENGTH_SHORT).show();
                }*/
                //发起请求获得用户许可,可以在此请求多个权限
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_PHONE_STATE},
                        READ_PHONE_STATE_CODE);
            }
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "需要提供系统通知权限才能正常工作", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,SYSTEM_ALERT_REQUEST_CODE);
            }
        }
    }

    private void initViewPage() {
        // TODO Auto-generated method stub
        for (int i = 0; i < imageRes.length; i++) {
            View mview = getLayoutInflater().inflate(R.layout.layout_welcome, null);
            mlist.add(mview);
        }
        madpter = new ViewPagerAdapter();
        vp_welcome.setAdapter(madpter);
        ci_indicator.setViewPager(vp_welcome);
    }

    void initSplash() {
        anim = new AlphaAnimation(0.6f, 1.0f);
        anim.setDuration(SPLASH_DISPLAY_LENGTH);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                iv_welcome.setImageResource(R.mipmap.splash);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //检查更新
                //UpdateService.checkUpdate(getApplicationContext(),UpdateService.ISAUTO);
                if(FenghuoApplication.needLogin){
                    CommonUtil.startLogin(SplashActivity.this,AppConstants.FROM_SPLASH);
                }else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                SplashActivity.this.finish();
            }
        });
        iv_welcome.startAnimation(anim);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SYSTEM_ALERT_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // SYSTEM_ALERT_WINDOW permission not granted...
                Toast.makeText(getApplicationContext(),
                        R.string.permission_rationale,Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //确保是我们的请求
        switch (requestCode) {
            case SYSTEM_ALERT_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "已获取系统通知权限", Toast.LENGTH_SHORT).show();
                    /*SettingsUtils settings = new SettingsUtils(this);
                    if(settings.getIsFirst()){
                        settings.setIsFirst(false);
                        settings.registerDevice();
                    }*/
                } else {
                    Toast.makeText(this, "获取系统通知权限失败", Toast.LENGTH_SHORT).show();
                    Snackbar.make(rl_splash,R.string.overlay_permission_denied,Snackbar.LENGTH_LONG)
                            .setAction(R.string.grant_permission, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                    startActivityForResult(intent,SYSTEM_ALERT_REQUEST_CODE);
                                }
                            });
                }
                break;
            case READ_PHONE_STATE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "已获取读取手机状态的权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "获取读取手机状态的权限失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View mview = mlist.get(position);

            ImageView imgView = (ImageView) mview.findViewById(R.id.welcome_page_img);
            BitmapDrawable db = new BitmapDrawable(ImageUtil.readBitMap(SplashActivity.this,imageRes[position]));
            imgView.setBackgroundDrawable(db);
            if (position == mlist.size() - 1) {
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.putExtra("from",AppConstants.FROM_SPLASH);
                        SplashActivity.this.startActivity(intent);
                        finish();
                    }
                });
            }
            container.addView(mview);
            return mlist.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mlist.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
