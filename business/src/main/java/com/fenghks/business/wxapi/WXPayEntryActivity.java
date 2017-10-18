package com.fenghks.business.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fenghks.business.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	@BindView(R.id.tv_return)
	TextView tv_return;
	@BindView(R.id.tv_wx_pay_result)
	TextView tv_wx_pay_result;

	private static final String TAG = "WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_pay_result);
        ButterKnife.bind(this);
    	api = WXAPIFactory.createWXAPI(this, WechatTool.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@OnClick(R.id.tv_return)
	void onBtnClick(View v){
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			Log.d(TAG,"onPayFinish,errCode="+resp.errCode);
			switch (resp.errCode) {
				case 0:
					tv_wx_pay_result.setText(R.string.recharge_success);
					break;
				case -1:
					tv_wx_pay_result.setText(R.string.recharge_failed);
					break;
				case -2:
					tv_wx_pay_result.setText(R.string.recharge_canceled);
					break;
			}
		}
	}
}