package com.fenghks.business.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fenghks.business.AppConstants;
import com.fenghks.business.R;
import com.fenghks.business.bean.Order;
import com.fenghks.business.print.BlueToothService;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.QRCodeUtil;
import com.fenghks.business.utils.VibratorUtil;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;

/**
 * Created by Fei on 2017/4/30.
 */

public class QRDialog extends DialogFragment {
    private static final String ARG_ORDER = "order";
    private Order order;

    public static QRDialog newInstance(Order order){
        QRDialog qrDialog = new QRDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_ORDER, order);
        qrDialog.setArguments(bundle);
        return qrDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable(ARG_ORDER);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        final Bitmap qrimage = QRCodeUtil.encodeAsBitmap(CommonUtil.QR_BASE+order.getId(),320);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrimage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final byte[] bytes=baos.toByteArray();

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_qr_image,null);
        ImageView imageView = ButterKnife.findById(view,R.id.item_img);
        TextView print_qr = ButterKnife.findById(view,R.id.tv_print_qr);
        print_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(context);
                Intent intent = new Intent(context, BlueToothService.class);
                intent.putExtra(AppConstants.EXTRA_ORDER,order);
                intent.putExtra(AppConstants.EXTRA_PRINT_SETTINGS, "0,0,0,0,1");
                context.startService(intent);
            }
        });
        Glide.with(context).load(bytes).into(imageView);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        /*builder.setNeutralButton(R.string.print_qr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VibratorUtil.vibrateOnce(context);
                Intent intent = new Intent(context, BlueToothService.class);
                intent.putExtra(AppConstants.EXTRA_QR,bytes);
                intent.putExtra(AppConstants.EXTRA_PRINT_SETTINGS, "0,0,0,0,1");
                context.startService(intent);
                dialog.dismiss();
            }
        });*/
        return builder.create();
    }
}
