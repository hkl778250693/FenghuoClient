package com.fenghks.business.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.fenghks.business.R;
import com.fenghks.business.utils.SPUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fenghuo on 2017/10/16.
 */

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.bg_iv)
    ImageView bgIv;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_splash);

        mContext = SplashActivity.this;
        ButterKnife.bind(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startAnimation();
                            //bgIv.startAnimation(AnimationUtils.loadAnimation(bgIv.getContext(), R.anim.splash_bg_rotate));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (SPUtil.getBoolean(mContext, "login_state", false)) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                super.handleMessage(msg);
            }
        }.sendEmptyMessageDelayed(0, 2000);
    }

    //开始旋转动画
    public void startAnimation() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(bgIv, "alpha", 1.0F, 0.0F);
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(bgIv, "rotationY", 0F, 90F);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(alphaAnimation, rotateAnimation);
        animatorSet.start();
    }
}
