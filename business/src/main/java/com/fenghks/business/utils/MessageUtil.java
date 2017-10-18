package com.fenghks.business.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.fenghks.business.R;

/**
 * Created by Fei on 2017/4/25.
 */

public class MessageUtil {

    public static void showToast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int msgId){
        Toast.makeText(context,msgId,Toast.LENGTH_SHORT).show();
    }

    public static void showAlert(Context context,int titleId,int contentId){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleId).setMessage(contentId).
                setNeutralButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}
