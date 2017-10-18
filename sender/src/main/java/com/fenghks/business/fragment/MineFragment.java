package com.fenghks.business.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fenghks.business.R;
import com.fenghks.business.activity.BluetoothActivity;
import com.fenghks.business.activity.CheckScheduleActivity;
import com.fenghks.business.activity.SendDataActivity;
import com.fenghks.business.activity.SendRecordActivity;
import com.fenghks.business.activity.SettingsActivity;
import com.fenghks.business.activity.TroubleReportActivity;
import com.fenghks.business.custom_view.CommonPopupWindow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by fenghuo on 2017/9/30.
 */

public class MineFragment extends Fragment {
    Unbinder unbinder;
    @BindView(R.id.clock_in)
    TextView clockIn;
    @BindView(R.id.check_schedule)
    TextView checkSchedule;
    @BindView(R.id.trouble_report)
    TextView troubleReport;
    @BindView(R.id.send_record)
    LinearLayout sendRecord;
    @BindView(R.id.send_data)
    LinearLayout sendData;
    @BindView(R.id.bluetooth)
    LinearLayout bluetooth;
    @BindView(R.id.contact_service)
    LinearLayout contactService;
    @BindView(R.id.settings)
    LinearLayout settings;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.user_name_tv)
    TextView userNameTv;
    @BindView(R.id.send_num_tv)
    TextView sendNumTv;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    private View view;
    private CommonPopupWindow window;
    private RadioButton cancleRb, doRb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, null);
        unbinder = ButterKnife.bind(this, view);

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.clock_in, R.id.check_schedule, R.id.trouble_report, R.id.send_record, R.id.send_data, R.id.bluetooth, R.id.contact_service, R.id.settings})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.clock_in:
                //检测当前位置是否在500米内
                break;
            case R.id.check_schedule:
                intent = new Intent(getActivity(), CheckScheduleActivity.class);
                startActivity(intent);
                break;
            case R.id.trouble_report:
                intent = new Intent(getActivity(), TroubleReportActivity.class);
                startActivity(intent);
                break;
            case R.id.send_record:
                intent = new Intent(getActivity(), SendRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.send_data:
                intent = new Intent(getActivity(), SendDataActivity.class);
                startActivity(intent);
                break;
            case R.id.bluetooth:
                intent = new Intent(getActivity(), BluetoothActivity.class);
                startActivity(intent);
                break;
            case R.id.contact_service:
                contactServiceWindow();
                break;
            case R.id.settings:
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 弹出联系客服提示
     */
    private void contactServiceWindow() {
        window = new CommonPopupWindow.Builder(getActivity())
                .setView(R.layout.popupwindow_contact_service)
                .setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.AnimDown)
                .setBackGroundLevel(0.5F)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        TextView tips = (TextView) view.findViewById(R.id.content);
                        tips.setText(R.string.call_content);
                        cancleRb = (RadioButton) view.findViewById(R.id.cancel_rb);
                        doRb = (RadioButton) view.findViewById(R.id.do_rb);
                    }
                })
                .setOutsideTouchable(true)
                .create();
        window.showAtLocation(coordinatorLayout, Gravity.CENTER, 0, 0);
        cancleRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        doRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "023-67750215"));//跳转到拨号界面，同时传递电话号码
                startActivity(dialIntent);
                window.dismiss();
            }
        });
    }
}
