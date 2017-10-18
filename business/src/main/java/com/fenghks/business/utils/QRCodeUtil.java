package com.fenghks.business.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fenghks.business.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

import butterknife.ButterKnife;

/**
 * Created by Fei on 2017/4/24.
 */

public class QRCodeUtil {

    public static void showQR(Context context,String content,int size){
        Bitmap qrimage = encodeAsBitmap(content,size);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrimage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes=baos.toByteArray();

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_qr_image,null);
        ImageView imageView = ButterKnife.findById(view,R.id.item_img);
        Glide.with(context).load(baos.toByteArray()).into(imageView);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view).show();
    }

    public static Bitmap encodeAsBitmap(String str,int size){
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.MARGIN,"0");
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, size, size,hints);
            // 使用 ZXing Android Embedded 要写的代码
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (WriterException e){
            e.printStackTrace();
        } catch (IllegalArgumentException iae){ // ?
            return null;
        }

        // 如果不使用 ZXing Android Embedded 的话，要写的代码
        /*int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels,0,100,0,0,w,h);*/

        return bitmap;
    }
}
