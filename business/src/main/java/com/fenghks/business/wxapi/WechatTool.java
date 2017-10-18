package com.fenghks.business.wxapi;

import android.content.Context;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by Fei on 2017/5/4.
 */

public class WechatTool {
    private Context context;

    public static final String MCH_ID = "1265622001";
    public static final String APP_ID = "wx77910b4c56adab3c";
    public static final String AppSecret = "";
    public static String unifiedorder = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    public static String notify_url = "https://api.fenghks.com/v1/business/wechatpay";

    public static void unifiedOrder(){
        Map<String,String> map = new HashMap<>();
        map.put("appid", APP_ID);
        map.put("mch_id",MCH_ID);
        map.put("device_info","WEB");
        map.put("nonce_str","");
    }
}
