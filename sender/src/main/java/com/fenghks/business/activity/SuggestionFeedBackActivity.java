package com.fenghks.business.activity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.fenghks.business.R;
import com.fenghks.business.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fenghuo on 2017/10/18.
 */

public class SuggestionFeedBackActivity extends BaseActivity {
    public static final String TAG = "SuggestionFeedBackAt";
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.suggestion_edit)
    EditText suggestionEdit;
    @BindView(R.id.commit_cardview)
    CardView commitCardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_feedback);
        ButterKnife.bind(this);

        suggestionEdit.setFocusable(true);
        suggestionEdit.setFocusableInTouchMode(true);
        suggestionEdit.requestFocus();
        Log.d(TAG, "当前软键盘的状态为：" + CommonUtil.isInputMethodOpened(commitCardview,suggestionEdit,mActivity));
        if (!CommonUtil.isInputMethodOpened(commitCardview,suggestionEdit,mActivity)) {
            CommonUtil.showInputMethod(mActivity, suggestionEdit);
            Log.d(TAG, "显示软键盘执行");
        }
    }

    @OnClick({R.id.back_iv, R.id.commit_cardview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                mActivity.finish();
                break;
            case R.id.commit_cardview:

                break;
        }
    }
}
