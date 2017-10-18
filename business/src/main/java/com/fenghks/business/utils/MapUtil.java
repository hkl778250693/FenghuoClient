package com.fenghks.business.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fenghks.business.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fenghuo on 2017/9/20.
 */

public class MapUtil {
    /**
     *  保存上次网络请求的 map 参数
     *
     * @param context
     * @param params
     */
    public static void saveRequestParams(Context context, Map<String, String> params) {
        JSONObject object = new JSONObject();
        for (String key : params.keySet()) {
            try {
                object.put(key, params.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SPUtil.putString(context, AppConstants.KEY_PREVIOUS_OPERATION_PARAMS, object.toString());
    }

    /**
     *  获取上次网络请求的 map 参数
     *
     * @param context
     * @return
     */
    public static Map<String, String> getRequestParams(Context context) {
        String result = SPUtil.getString(context, AppConstants.KEY_PREVIOUS_OPERATION_PARAMS);
        Map<String,String> params = new HashMap<>();
        try {
            JSONObject object = new JSONObject(result);
            JSONArray names = object.names();
            if(null != names){
                for (int i = 0; i < names.length(); i++) {
                    String name = names.getString(i);
                    String value = object.optString(name);
                    params.put(name,value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }
}
