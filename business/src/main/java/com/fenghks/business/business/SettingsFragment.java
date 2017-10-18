package com.fenghks.business.business;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.fenghks.business.R;
import com.fenghks.business.utils.CommonUtil;

/**
 * Created by Fei on 2017/5/2.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,SharedPreferences.OnSharedPreferenceChangeListener{
    private Preference quitPref;
    private Preference servicePref;
    private Preference businessFormPref;
    private Preference kitchenFormPref;
    private Preference customerFormPref;
    private Preference senderFormPref;

    SharedPreferences defaultsp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        quitPref = findPreference(getString(R.string.pref_quit));
        servicePref = findPreference(getString(R.string.pref_contace_service));
        businessFormPref = findPreference(getString(R.string.pref_print_business));
        kitchenFormPref = findPreference(getString(R.string.pref_print_kitchen));
        customerFormPref = findPreference(getString(R.string.pref_print_customer));
        senderFormPref = findPreference(getString(R.string.pref_print_sender));

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        defaultsp = getPreferenceManager().getDefaultSharedPreferences(getActivity());

        businessFormPref.setSummary(sp.getString(getString(R.string.pref_print_business),
                defaultsp.getString(getString(R.string.pref_print_business),"1")));
        kitchenFormPref.setSummary(sp.getString(getString(R.string.pref_print_kitchen),
                defaultsp.getString(getString(R.string.pref_print_kitchen),"1")));
        customerFormPref.setSummary(sp.getString(getString(R.string.pref_print_customer),
                defaultsp.getString(getString(R.string.pref_print_customer),"1")));
        senderFormPref.setSummary(sp.getString(getString(R.string.pref_print_sender),
                defaultsp.getString(getString(R.string.pref_print_sender),"1")));
        quitPref.setOnPreferenceClickListener(this);
        servicePref.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference == quitPref){
            CommonUtil.logout(getActivity());
            getActivity().finish();
        }
        if(preference == servicePref){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse(getString(R.string.dial_service_phone));
            intent.setData(data);
            getActivity().startActivity(intent);
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_print_business))
                || key.equals(getString(R.string.pref_print_kitchen))
                || key.equals(getString(R.string.pref_print_customer))
                || key.equals(getString(R.string.pref_print_sender))) {
            findPreference(key).setSummary(sharedPreferences.getString(key,defaultsp.getString(key,"0")));
        }
        if(key.equals(getString(R.string.pref_receive_notice))){
            if(sharedPreferences.getBoolean(key,false)){
                CommonUtil.registerPushService(getActivity());
            }else {
                CommonUtil.unregisterPushService(getActivity());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    //Preference.OnPreferenceClickListener
}
