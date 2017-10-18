package com.fenghks.business.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fenghks.business.AppConstants;

/**
 * Created by Fei on 2017/1/19.
 */

public class SPUtil {
    //private static final String spFileName = "app";
    public static final String TAG = "SPUtil";

    public static String getString(Context context, String strKey) {
        return getString(context, strKey, "");
    }

    public static String getString(Context context, String strKey,
                                   String strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                AppConstants.SP_APP, Context.MODE_PRIVATE);
        String result = setPreferences.getString(strKey, strDefault);
        return result;
    }

    public static void putString(Context context, String strKey, String strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                AppConstants.SP_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putString(strKey, strData);
        editor.apply();
    }

    public static Boolean getBoolean(Context context, String strKey) {
        return getBoolean(context, strKey, false);
    }

    public static Boolean getBoolean(Context context, String strKey,
                                     Boolean strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(
                AppConstants.SP_APP, Context.MODE_PRIVATE);
        Boolean result = setPreferences.getBoolean(strKey, strDefault);
        return result;
    }


    public static void putBoolean(Context context, String strKey, Boolean strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(AppConstants.SP_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(strKey, strData);
        editor.apply();
    }

    public static int getInt(Context context, String strKey) {
        return getInt(context, strKey, -1);
    }

    public static int getInt(Context context, String strKey, int strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(AppConstants.SP_APP, Context.MODE_PRIVATE);
        int result = setPreferences.getInt(strKey, strDefault);
        return result;
    }

    public static void putInt(Context context, String strKey, int strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(AppConstants.SP_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putInt(strKey, strData);
        editor.apply();
    }

    public static long getLong(Context context, String strKey) {
        return getLong(context, strKey, -1);
    }

    public static long getLong(Context context, String strKey, long strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(AppConstants.SP_APP, Context.MODE_PRIVATE);
        long result = setPreferences.getLong(strKey, strDefault);
        return result;
    }

    public static void putLong(Context context, String strKey, long strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(AppConstants.SP_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putLong(strKey, strData);
        editor.apply();
    }

    public static String getString(Context context, String spFileName, String strKey, String strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        String result = setPreferences.getString(strKey, strDefault);
        return result;
    }

    public static void putString(Context context, String spFileName, String strKey, String strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putString(strKey, strData);
        editor.apply();
    }

    public static Boolean getBoolean(Context context, String spFileName, String strKey, Boolean strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        Boolean result = setPreferences.getBoolean(strKey, strDefault);
        return result;
    }

    public static void putBoolean(Context context, String spFileName, String strKey, Boolean strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(strKey, strData);
        editor.apply();
    }

    public static int getInt(Context context, String spFileName, String strKey, int strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        int result = setPreferences.getInt(strKey, strDefault);
        return result;
    }

    public static void putInt(Context context, String spFileName, String strKey, int strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putInt(strKey, strData);
        editor.apply();
    }

    public static long getLong(Context context, String spFileName, String strKey, long strDefault) {
        SharedPreferences setPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        long result = setPreferences.getLong(strKey, strDefault);
        return result;
    }

    public static void putLong(Context context, String spFileName, String strKey, long strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putLong(strKey, strData);
        editor.apply();
    }

    public static void removeKey(Context context, String strKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SP_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(strKey).apply();
        Log.d(TAG, "已移除本地token！");
    }
}
