package com.fenghks.business.business;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.fenghks.business.BaseActivity;
import com.fenghks.business.R;
import com.fenghks.business.business.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.tl_toolbar)
    Toolbar tl_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        initView();

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_content, new SettingsFragment())
                .commit();
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    private void initView() {
        tl_toolbar.setTitle(R.string.settings);
        setSupportActionBar(tl_toolbar);
    }
}
