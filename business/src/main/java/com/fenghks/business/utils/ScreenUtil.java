package com.fenghks.business.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;

/**
 * 屏幕分辨率，尺寸相关
 * 
 * @author ganhx 2012-11-21
 * 
 */
public class ScreenUtil {

	public static int screenHeight = 0;
	public static int screenWidth = 0;
	public static float screenDensity = 0;

	/**
	 * 屏幕高度
	 * 
	 * @param context
	 * @return
	 */

	public static int getScreenHeight(Activity context) {
		if (screenWidth == 0 || screenHeight == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
			ScreenUtil.screenDensity = dm.density;
			ScreenUtil.screenHeight = dm.heightPixels;
			ScreenUtil.screenWidth = dm.widthPixels;
		}
		return screenHeight;
	}

	/**
	 * 屏幕宽度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getScreenWidth(Activity activity) {
		if (screenWidth == 0 || screenHeight == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			ScreenUtil.screenDensity = dm.density;
			ScreenUtil.screenHeight = dm.heightPixels;
			ScreenUtil.screenWidth = dm.widthPixels;
		}
		return screenWidth;
	}

	/**
	 * 当前屏幕高度与设计高度比例
	 * 
	 * @param context
	 * @return
	 */
	public static float getScreenHeightRatio(Activity context) {
		return (float) getScreenHeight(context) / 1245;
	}

	/**
	 * 当前屏幕宽度与设计宽度比例
	 * 
	 * @param context
	 * @return
	 */
	public static float getScreenWidthRatio(Activity context) {
		return (float) getScreenWidth(context) / 720;
	}

	/**
	 * 获取屏幕密度
	 * 
	 * @param activity
	 * @return
	 */
	public static float getScreenDensity(Activity activity) {
		if (screenWidth == 0 || screenHeight == 0 || screenDensity == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			ScreenUtil.screenDensity = dm.density;
			ScreenUtil.screenHeight = dm.heightPixels;
			ScreenUtil.screenWidth = dm.widthPixels;
		}
		return screenDensity;
	}

	/**
	 * 获取屏幕尺寸
	 * 
	 * @param activity
	 * @return
	 */
	public static double getScreenPhysicalSize(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2)
				+ Math.pow(dm.heightPixels, 2));
		return diagonalPixels / (160 * dm.density);
	}

	/**
	 * 获取屏幕状态栏(电池栏高度)
	 * 
	 * @param activity
	 * @return
	 */
	public static int getScreenStatusBarHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}
}