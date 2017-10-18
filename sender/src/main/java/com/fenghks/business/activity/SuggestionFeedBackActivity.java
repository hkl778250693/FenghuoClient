package com.fenghks.business.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.fenghks.business.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fenghuo on 2017/10/18.
 */

public class SuggestionFeedBackActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.suggestion_edit)
    EditText suggestionEdit;
    @BindView(R.id.commit_btn)
    Button commitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_feedback);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back_iv, R.id.commit_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                mActivity.finish();
                break;
            case R.id.commit_btn:
                break;
        }
    }
}
