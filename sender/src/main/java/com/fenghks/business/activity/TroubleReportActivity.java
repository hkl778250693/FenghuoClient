package com.fenghks.business.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.R;
import com.fenghks.business.custom_view.CommonPopupWindow;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.MyPhotoUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by fenghuo on 2017/10/9.
 */

public class TroubleReportActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView backIv;
    @BindView(R.id.choose_trouble_reason)
    LinearLayout chooseTroubleReason;
    @BindView(R.id.add_picture)
    ImageView addPicture;
    @BindView(R.id.report_cardview)
    CardView reportCardview;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.trouble_reason_tv)
    TextView troubleReasonTv;
    private CommonPopupWindow window;
    private TextView trafficTv, vehicleBreakTv, mealBreakTv, lawEnforcementStopTv, bodyDiscomfortTv, takePhotoTv, albumTv;
    private CardView cancleCv;
    protected static final int TAKE_PHOTO = 100;//拍照
    protected static final int CROP_SMALL_PICTURE = 300;  //裁剪成小图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trouble_report);
        ButterKnife.bind(this);

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }

    @OnClick({R.id.back_iv, R.id.choose_trouble_reason, R.id.add_picture, R.id.report_cardview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                mActivity.finish();
                break;
            case R.id.choose_trouble_reason:
                ChooseReasonWindow();
                break;
            case R.id.add_picture:
                choosePictureWindow();
                break;
            case R.id.report_cardview:
                break;
        }
    }

    /**
     * 弹出选择故障原因
     */
    private void ChooseReasonWindow() {
        window = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popupwindow_trouble_reason)
                .setWidthAndHeight(chooseTroubleReason.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.AnimDown)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        //绑定控件ID
                        trafficTv = (TextView) view.findViewById(R.id.traffic_tv);
                        vehicleBreakTv = (TextView) view.findViewById(R.id.vehicle_break_tv);
                        mealBreakTv = (TextView) view.findViewById(R.id.meal_break_tv);
                        lawEnforcementStopTv = (TextView) view.findViewById(R.id.law_enforcement_stop_tv);
                        bodyDiscomfortTv = (TextView) view.findViewById(R.id.body_discomfort);
                    }
                })
                .setOutsideTouchable(true)
                .create();
        window.showAsDropDown(chooseTroubleReason, 0, 0);
        //设置点击事件
        trafficTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troubleReasonTv.setText(trafficTv.getText().toString().trim());
                window.dismiss();
            }
        });
        vehicleBreakTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troubleReasonTv.setText(vehicleBreakTv.getText().toString().trim());
                window.dismiss();
            }
        });
        mealBreakTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troubleReasonTv.setText(mealBreakTv.getText().toString().trim());
                window.dismiss();
            }
        });
        lawEnforcementStopTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troubleReasonTv.setText(lawEnforcementStopTv.getText().toString().trim());
                window.dismiss();
            }
        });
        bodyDiscomfortTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troubleReasonTv.setText(bodyDiscomfortTv.getText().toString().trim());
                window.dismiss();
            }
        });
    }

    /**
     * 选择照片、拍照弹出框
     */
    private void choosePictureWindow() {
        window = new CommonPopupWindow.Builder(this)
                .setView(R.layout.popupwindow_choose_picture)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.AnimUp)
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        takePhotoTv = (TextView) view.findViewById(R.id.take_photo_tv);
                        albumTv = (TextView) view.findViewById(R.id.album_tv);
                        cancleCv = (CardView) view.findViewById(R.id.cancle_cv);
                    }
                })
                .setOutsideTouchable(true)
                .create();
        window.showAtLocation(coordinatorLayout, Gravity.BOTTOM, 0, 0);
        takePhotoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoTv.setEnabled(false);
                //拍照
                if (CommonUtil.hasSdcard()) {
                    openCamera();//拍照
                } else {
                    Toast.makeText(mContext, "没有SD卡哦，不能拍照！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        albumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumTv.setEnabled(false);
                //相册
            }
        });
        cancleCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
    }

    //调用相机
    public void openCamera() {
        File file = new MyPhotoUtils(mContext).createTempFile();
        Uri tempUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tempUri = FileProvider.getUriForFile(mContext, "com.example.administrator.myapplicationsienke.fileprovider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            // 指定照片保存路径（SD卡），temp.jpg为一个临时文件，每次拍照后这个图片都会被替换
            tempUri = Uri.fromFile(file);
        }
        Intent openCameraIntent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        openCameraIntent.putExtra("autofocus", true); // 自动对焦
        openCameraIntent.putExtra("fullScreen", false); // 全屏
        openCameraIntent.putExtra("showActionIcons", false);
        openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        Log.i("openCamera===>", "临时保存的地址为" + tempUri.getPath());
        startActivityForResult(openCameraIntent, TAKE_PHOTO);
    }
}
