package com.fenghks.business.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fenghks.business.R;
import com.tencent.android.tpush.horse.Tools;

/**
 * Created by fenghuo on 2017/9/12.
 */

public class WindowUtil {
    //设置背景透明度
    public static void backgroundAlpha(Activity mActivity, float bgAlpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        if (bgAlpha == 1) {
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        mActivity.getWindow().setAttributes(lp);
    }

    //开始加载帧动画
    public static void startFrameAnimation(ImageView imgView) {
        imgView.setBackgroundResource(R.drawable.frame_animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) imgView.getDrawable();
        animationDrawable.start();
    }
}
