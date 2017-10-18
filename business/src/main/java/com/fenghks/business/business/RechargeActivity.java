package com.fenghks.business.business;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fenghks.business.BaseActivity;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.StringUtil;
import com.fenghks.business.wxapi.WechatTool;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RechargeActivity extends BaseActivity {

    @BindView(R.id.tl_toolbar)
    Toolbar tl_toolbar;
    @BindView(R.id.et_recharge_value)
    EditText et_recharge_value;
    @BindView(R.id.tv_hint_limit)
    TextView tv_hint_limit;
    @BindView(R.id.iv_wechat_pay)
    ImageView iv_wechat_pay;
    @BindView(R.id.iv_alipay)
    ImageView iv_alipay;
    @BindView(R.id.tv_next)
    TextView tv_next;

    private int rechargeChannel = 0;//0微信支付，1支付宝

    private IWXAPI wxapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);

        wxapi = WXAPIFactory.createWXAPI(this, WechatTool.APP_ID);

        initView();
    }

    private void initView() {
        tl_toolbar.setTitle(R.string.recharge_title);
        setSupportActionBar(tl_toolbar);
    }

    @OnClick({R.id.iv_wechat_pay, R.id.iv_alipay, R.id.tv_next})
    public void onRechargeClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wechat_pay:
                rechargeChannel = 0;
                iv_alipay.setBackground(null);
                et_recharge_value.setHint(R.string.hint_recharge_value_wechat);
                tv_hint_limit.setText(R.string.hint_recharge_limit_wechat);
                view.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.shape));
                break;
            case R.id.iv_alipay:
                Toast.makeText(mContext, R.string.waiting_dev, Toast.LENGTH_SHORT).show();
                /*rechargeChannel = 1;
                iv_wechat_pay.setBackground(null);
                et_recharge_value.setHint(R.string.hint_recharge_value_alipay);
                tv_hint_limit.setText(R.string.hint_recharge_limit_alipay);
                view.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.shape));*/
                break;
            case R.id.tv_next:
                requestPay();
                break;
        }
    }

    private void requestPay() {
        String amount = et_recharge_value.getText().toString().trim();
        if (!StringUtil.isNumric(amount)) {
            Toast.makeText(mContext, R.string.charge_value_err, Toast.LENGTH_SHORT).show();
            et_recharge_value.requestFocus();
            return;
        }
        switch (rechargeChannel) {
            case 0:
                requestWxPayOrder(Double.parseDouble(amount));
                break;
            case 1:
                Toast.makeText(mContext, R.string.waiting_dev, Toast.LENGTH_SHORT).show();
                //requestAlipayPayOrder(Double.parseDouble(amount));
                break;
        }
    }

    private void requestWxPayOrder(double amount) {
        final Map map = new HashMap();
        map.put("businessid", FenghuoApplication.business.getId());
        map.put("attach", FenghuoApplication.business.getCode());
        map.put("amount", amount);
        map.put("xg_token", FenghuoApplication.xg_token);
        x.http().post(CommonUtil.genGetParam(CommonUtil.WX_PREORDER_URL, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                JSONObject json = ParseUtils.parseDataString(mContext, result);
                requestWxPay(json);
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

    private void requestWxPay(JSONObject json) {
        PayReq req = new PayReq();
        req.appId = json.optString("appid");
        req.partnerId = json.optString("partnerid");
        req.prepayId = json.optString("prepayid");
        req.nonceStr = json.optString("noncestr");
        req.timeStamp = json.optString("timestamp");
        req.packageValue = json.optString("package");
        req.sign = json.optString("sign");
        //req.extData			= "app data"; // optional
        Toast.makeText(RechargeActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        wxapi.sendReq(req);
        finish();
    }
}
