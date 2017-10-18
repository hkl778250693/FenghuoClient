package com.fenghks.business.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.business.MainActivity;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.SPUtil;
import com.fenghks.business.utils.VibratorUtil;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    public static final int RETURN_CODE = 1001;
    @BindView(R.id.login_account)
    EditText et_account;
    @BindView(R.id.login_password)
    EditText et_password;
    @BindView(R.id.tv_choice_business)
    TextView tv_choice_business;
    @BindView(R.id.tv_choice_shop)
    TextView tv_choice_shop;

    private int from = 0;
    private int accountType = 1;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mActivity = this;
        from = getIntent().getIntExtra("from", 0);
        et_account.setText(SPUtil.getString(mActivity, "account", ""));
        et_password.setText(SPUtil.getString(mActivity, "password", ""));
        et_account.setFocusable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        accountType = SPUtil.getInt(mActivity, "accountType", 1);
        if (accountType == 0) {
            tv_choice_business.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            tv_choice_shop.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            et_account.setHint(R.string.hint_bisiness_account);
        }
    }

    @OnClick({R.id.btn_login, R.id.tv_choice_business, R.id.tv_choice_shop, R.id.tv_forget})
    public void onTestButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                VibratorUtil.vibrateOnce(mActivity);
                startLogin();
                break;
            case R.id.tv_forget:
                VibratorUtil.vibrateOnce(mActivity);
                break;
            case R.id.tv_choice_business:
                VibratorUtil.vibrateOnce(mActivity);
                tv_choice_business.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                tv_choice_shop.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                et_account.setHint(R.string.hint_bisiness_account);
                accountType = 0;
                break;
            case R.id.tv_choice_shop:
                VibratorUtil.vibrateOnce(mActivity);
                tv_choice_business.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_choice_shop.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                et_account.setHint(R.string.hint_shop_account);
                accountType = 1;
                break;
        }
    }

    private void startLogin() {
        final String username = et_account.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        if ("".equals(username)) {
            Toast.makeText(mActivity, R.string.error_invalid_account, Toast.LENGTH_SHORT).show();
            et_account.setFocusable(true);
            return;
        }
        if ("".equals(password)) {
            Toast.makeText(mActivity, R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
            et_password.setFocusable(true);
            return;
        }
        Map map = new HashMap();
        map.put("businessname", username);
        map.put("businesspwd", password);
        map.put("accountType", accountType);
        x.http().post(CommonUtil.genGetParam(CommonUtil.LOGIN_URL, map), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
                JSONObject obj = ParseUtils.parseDataString(mActivity, result);
                if (null != obj) {
                    FenghuoApplication.business = ParseUtils.parseBusiness(obj);
                    FenghuoApplication.token = obj.optString("token");
                    FenghuoApplication.needLogin = false;
                    CommonUtil.saveLogin(mActivity, FenghuoApplication.token, username, password, accountType);

                    CommonUtil.registerDevice(mActivity);

                    Intent intent = new Intent();
                    if (AppConstants.FROM_SPLASH == from) {
                        intent.setClass(mActivity, MainActivity.class);
                        startActivity(intent);
                    }
                    setResult(RETURN_CODE, intent);
                    LoginActivity.this.finish();
                } else {
                    FenghuoApplication.needLogin = true;
                    Toast.makeText(mActivity, R.string.fail_login, Toast.LENGTH_SHORT).show();
                    et_account.setFocusable(true);
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
