package com.fenghks.business.authorize;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.BaseActivity;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.bean.Business;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.ParseUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fenghks.business.AppConstants.MEITUAN_BASE_URL;
import static com.fenghks.business.utils.CommonUtil.MEITUAN_BIND_CALLBACK_URL;

public class AuthorizeActivity extends BaseActivity {
    @BindView(R.id.tl_toolbar)
    Toolbar tl_toolbar;
    @BindView(R.id.wv_authorize)
    WebView wv_authorize;

    public static final int AUTHORIZE_ELEME = 0;
    public static final int AUTHORIZE_MEITUAN = 1;
    public static final int AUTHORIZE_BAIDU = 2;
    public static final int AUTHORIZE_KOUBEI = 3;
    public static final int AUTHORIZE_DADA = 4;
    public static final int AUTHORIZE_SHANSONG = 5;

    private Intent initialize_intent;
    private int authorize_platform;
    private Business shop;
    //private String auth_url;
    private boolean isAuth;
    private Map<String, String> getParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);
        ButterKnife.bind(this);

        getParams = new HashMap();

        initialize_intent = getIntent();
        if (null != initialize_intent) {
            authorize_platform = initialize_intent.getIntExtra("Platform", AUTHORIZE_MEITUAN);
            isAuth = initialize_intent.getBooleanExtra("isAuth", true);
            shop = (Business) initialize_intent.getSerializableExtra("shop");
        }
        initView();
    }

    private void initView() {
        if (isAuth) {
            tl_toolbar.setTitle(initialize_intent.getStringExtra("title") + "-" + getString(R.string.auth_bind));
        } else {
            tl_toolbar.setTitle(initialize_intent.getStringExtra("title") + "-" + getString(R.string.unauth_unbind));
        }
        setSupportActionBar(tl_toolbar);

        WebSettings webSettings = wv_authorize.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true);
        //webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        wv_authorize.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                Log.d("AuthorizeActivity","网址是："+url);
                view.loadUrl(url);
                return true;
            }

            /*@Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                //handler.cancel(); 默认的处理方式，WebView变成空白页
                handler.proceed();//接受证书
            }*/
        });

        wv_authorize.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        if (isAuth) {
            switch (authorize_platform) {
                case AUTHORIZE_ELEME:
                    elemeAuth();
                    return;
                case AUTHORIZE_MEITUAN:
                    meituanAuth();
                    return;
                default:

                    break;
            }
        } else {
            switch (authorize_platform) {
                case AUTHORIZE_ELEME:
                    elemeUnAuth();
                    return;
                case AUTHORIZE_MEITUAN:
                    meituanUnAuth();
                    return;
                default:

                    break;
            }
        }

    }

    private void meituanAuth() {
        Map<String, String> map = new HashMap<>();
        map.put("businessid", "" + shop.getId());
        map.put("parentid", "" + FenghuoApplication.business.getId());
        map.put("isbind", "1");
        x.http().post(CommonUtil.genGetThirdParam(CommonUtil.GET_MEITUAN_ID_URL, map, AUTHORIZE_MEITUAN),
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println(result);
                        JSONObject obj = ParseUtils.parseDataString(mContext,result);
                        if (obj != null) {
                            try {
                                byte[] b = Base64.decode(obj.optString("str"), Base64.DEFAULT);
                                String s = new String(b);
                                JSONObject id = new JSONObject(s);
                                getParams.put("developerId", id.optString("id"));
                                getParams.put("signKey", id.optString("key"));
                                getParams.put("businessId", "2");
                                getParams.put("ePoiId", id.optString("id") + "_" + shop.getId());
                                getParams.put("ePoiName", shop.getName());
                                getParams.put("callbackUrl",
                                        URLEncoder.encode(MEITUAN_BASE_URL + MEITUAN_BIND_CALLBACK_URL, "UTF-8"));
                                wv_authorize.loadUrl(CommonUtil.genGetUrl(AppConstants.MEITUAN_AUTH_URL, getParams));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(mContext, R.string.cannot_authorize, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CommonUtil.networkErr(ex, isOnCallback);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }

    private void elemeAuth() {
        Map<String, String> map = new HashMap<>();
        map.put("businessid", "" + shop.getId());
        map.put("parentid", "" + FenghuoApplication.business.getId());
        map.put("isbind", "1");
        x.http().post(CommonUtil.genGetThirdParam(CommonUtil.GET_ELEME_AUTH_URL, map, AUTHORIZE_ELEME),
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println(result);
                        JSONObject obj = ParseUtils.parseDataString(mContext,result);
                        if (obj != null) {
                            String url = obj.optString("oauthUrl");
                            wv_authorize.loadUrl(url);
                        } else {
                            Toast.makeText(mContext, R.string.cannot_authorize, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CommonUtil.networkErr(ex, isOnCallback);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }


    private void meituanUnAuth() {
        Map<String, String> map = new HashMap<>();
        map.put("businessid", "" + shop.getId());
        map.put("parentid", "" + FenghuoApplication.business.getId());
        map.put("isbind", "0");
        x.http().post(CommonUtil.genGetThirdParam(CommonUtil.GET_MEITUAN_ID_URL, map, AUTHORIZE_MEITUAN),
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println(result);
                        JSONObject obj = ParseUtils.parseDataString(mContext,result);
                        if (obj != null) {
                            try {
                                byte[] b = Base64.decode(obj.optString("str"), Base64.DEFAULT);
                                String s = new String(b);
                                JSONObject id = new JSONObject(s);
                                //getParams.put("developerId", id.optString("id"));
                                getParams.put("appAuthToken", id.optString("token"));
                                getParams.put("signKey", id.optString("key"));
                                getParams.put("businessId", "2");
                                //getParams.put("ePoiId", id.optString("id") + "_" + shop.getId());
                                //getParams.put("ePoiName", shop.getName());
                                //getParams.put("callbackUrl",URLEncoder.encode("http://service.fenghks.com:8080/v1/meituan/callback/unbindcallback", "UTF-8"));
                                wv_authorize.loadUrl(CommonUtil.genGetUrl(AppConstants.MEITUAN_UNAUTH_URL, getParams));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } /*catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }*/
                        } else {
                            Toast.makeText(mContext, R.string.cannot_authorize, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CommonUtil.networkErr(ex, isOnCallback);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }

    private void elemeUnAuth() {
        Map<String, String> map = new HashMap<>();
        map.put("businessid", "" + shop.getId());
        map.put("parentid", "" + FenghuoApplication.business.getId());
        map.put("isbind", "0");
        x.http().post(CommonUtil.genGetThirdParam(CommonUtil.GET_MEITUAN_ID_URL, map, AUTHORIZE_ELEME),
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println(result);
                        JSONObject obj = ParseUtils.parseDataString(mContext,result);
                        if (obj != null) {
                            try {
                                byte[] b = Base64.decode(obj.optString("str"), Base64.DEFAULT);
                                String s = new String(b);
                                JSONObject id = new JSONObject(s);
                                getParams.put("developerId", id.optString("id"));
                                //getParams.put("appAuthToken", id.optString("token"));
                                //getParams.put("signKey", id.optString("key"));
                                //getParams.put("businessId", "2");
                                //getParams.put("ePoiId", id.optString("id") + "_" + shop.getId());
                                //getParams.put("ePoiName", shop.getName());
                                //getParams.put("callbackUrl",URLEncoder.encode("http://service.fenghks.com:8080/v1/meituan/callback/unbindcallback", "UTF-8"));
                                wv_authorize.loadUrl(CommonUtil.genGetUrl(AppConstants.MEITUAN_ZIRUZHU_URL, getParams));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } /*catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }*/
                        } else {
                            Toast.makeText(mContext, R.string.cannot_authorize, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CommonUtil.networkErr(ex, isOnCallback);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }
}
