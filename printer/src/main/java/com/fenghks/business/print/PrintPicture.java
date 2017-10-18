package com.fenghks.business.print;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

import zj.com.customize.sdk.Other;


/**
 * Created by Fei on 2017/4/30.
 */

public class PrintPicture {

    private static final int WIDTH_80 = 576;
    private static final int WIDTH_58 = 384;

    /*
	 * 生成QR图
	 */
    public static byte[] createQRImage(String text,int size ,boolean isPOS80) {
        try {
            // 需要引入zxing包
            QRCodeWriter writer = new QRCodeWriter();

            Log.i("PrintQR", "二维码文本：" + text);

            //int blank_width =  isPOS80 ? (WIDTH_80 - size)/2 : (WIDTH_58 - size)/2;
            int page_width =  isPOS80 ? WIDTH_80 : WIDTH_58;

            // 把输入的文本转为二维码
            BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,size, size);

            System.out.println("w:" + martix.getWidth() + "h:"
                    + martix.getHeight());

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN,"1");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) { //高度
                for (int x = 0; x < size; x++) { //宽度
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);

            byte[] data = PrintPicture.POS_PrintBMP(bitmap, page_width, 0);
            return data;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 打印位图函数
     * 此函数是将一行作为一个图片打印，这样处理不容易出错
     * @param mBitmap
     * @param nWidth
     * @param nMode
     * @return
     */
    public static byte[] POS_PrintBMP(Bitmap mBitmap, int nWidth, int nMode) {
        // 先转黑白，再调用函数缩放位图
        int width = ((nWidth + 7) / 8) * 8;
        int height = mBitmap.getHeight() * width / mBitmap.getWidth();
        height = ((height + 7) / 8) * 8;

        Bitmap rszBitmap = mBitmap;
        if (mBitmap.getWidth() != width){
            rszBitmap = Other.resizeImage(mBitmap, width, height);
        }

        Bitmap grayBitmap = Other.toGrayscale(rszBitmap);

        byte[] dithered = Other.thresholdToBWPic(grayBitmap);

        byte[] data = Other.eachLinePixToCmd(dithered, width, nMode);

        return data;
    }

    /**
     * 使用下传位图打印图片
	 * 先收完再打印
     * @param bmp
     * @return
     */
	/*public static byte[] Print_1D2A(Bitmap bmp){
        *//*
         * 使用下传位图打印图片
         * 先收完再打印
         *//*
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        byte data[]=new byte[1024*10];
        data[0] = 0x1D;
        data[1] = 0x2A;
        data[2] =(byte)( (width - 1)/ 8 + 1);
        data[3] =(byte)( (height - 1)/ 8 + 1);
        byte k = 0;
        int position = 4;
        int i;
        int j;
        byte temp = 0;
        for(i = 0; i <width;  i++){

            System.out.println("进来了...I");
            for(j = 0; j < height; j++){
                System.out.println("进来了...J");
                if(bmp.getPixel(i, j) != -1){
                    temp |= (0x80 >> k);
                } // end if
                k++;
                if(k == 8){
                    data[position++] = temp;
                    temp = 0;
                    k = 0;
                } // end if k
            }// end for j
            if(k % 8 != 0){
                data[position ++] = temp;
                temp = 0;
                k = 0;
            }

        }
        System.out.println("data"+data);

        if( width% 8 != 0){
            i =   height/ 8;
            if(height % 8 != 0) i++;
            j = 8 - (width % 8);
            for(k = 0; k < i*j; k++){
                data[position++] = 0;
            }
        }
        return data;
    }*/

    public static byte[] draw2PxPoint(Bitmap bmp) {
        //用来存储转换后的 bitmap 数据。为什么要再加1000，这是为了应对当图片高度无法
        //整除24时的情况。比如bitmap 分辨率为 240 * 250，占用 7500 byte，5:5455,3,5447,4,5427
        //但是实际上要存储11行数据，每一行需要 24 * 240 / 8 =720byte 的空间。再加上一些指令存储的开销，
        //所以多申请 1000byte 的空间是稳妥的，不然运行时会抛出数组访问越界的异常。
        int size = bmp.getWidth() * bmp.getHeight() / 8 + 1000;
        byte[] data = new byte[size];
        int k = 0;
        //设置行距为0的指令
        data[k++] = 0x1B;
        data[k++] = 0x33;
        data[k++] = 0x00;
        // 逐行打印
        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
            //打印图片的指令
            data[k++] = 0x1B;
            data[k++] = 0x2A;
            data[k++] = 33;
            data[k++] = (byte) (bmp.getWidth() % 256); //nL
            data[k++] = (byte) (bmp.getWidth() / 256); //nH
            //对于每一行，逐列打印
            for (int i = 0; i < bmp.getWidth(); i++) {
                //每一列24个像素点，分为3个字节存储
                for (int m = 0; m < 3; m++) {
                    //每个字节表示8个像素点，0表示白色，1表示黑色
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                        data[k] += data[k] + b;
                    }

                    k++;
                }
            }
            data[k++] = 10;//换行
        }
        //   long a=System.currentTimeMillis();
        byte[] data1 = new byte[k];
        System.arraycopy(data, 0, data1, 0, k);
        // long b=System.currentTimeMillis();
        //  System.out.println("结束字节:"+k+"---"+data.length+"耗时:"+(b-a));
        return data1;
    }

    /**
     * 灰度图片黑白化，黑色是1，白色是0
     *
     * @param x   横坐标
     * @param y   纵坐标
     * @param bit 位图
     * @return
     */
    public static byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth() && y < bit.getHeight()) {
            byte b;
            int pixel = bit.getPixel(x, y);
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }

    /**
     * 图片灰度的转化
     */
    private static int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);  //灰度转化公式
        return gray;
    }

    /*public static Bitmap resizeImage(Bitmap bitmap, int w, int h)
    {
        Bitmap BitmapOrg = bitmap;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);

        return resizedBitmap;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal){
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0F);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0.0F, 0.0F, paint);
        return bmpGrayscale;
    }

    public static byte[] thresholdToBWPic(Bitmap mBitmap){
        int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
        byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];

        mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0,
                mBitmap.getWidth(), mBitmap.getHeight());

        format_K_threshold(pixels, mBitmap.getWidth(),
                mBitmap.getHeight(), data);

        return data;
    }

    private static void format_K_threshold(int[] orgpixels, int xsize, int ysize, byte[] despixels){
        int graytotal = 0;
        int grayave = 128;

        int k = 0;
        for (int i = 0; i < ysize; i++) {
            for (int j = 0; j < xsize; j++)
            {
                int gray = orgpixels[k] & 0xFF;
                graytotal += gray;
                k++;
            }
        }
        grayave = graytotal / ysize / xsize;

        k = 0;
        for (int i = 0; i < ysize; i++) {
            for (int j = 0; j < xsize; j++)
            {
                int gray = orgpixels[k] & 0xFF;
                if (gray > grayave) {
                    despixels[k] = 0;
                } else {
                    despixels[k] = 1;
                }
                k++;
            }
        }
    }

    public static byte[] eachLinePixToCmd(byte[] src, int nWidth, int nMode){
        int[] p0 = { 128 };
        int[] p1 = { 64 };
        int[] p2 = { 32 };
        int[] p3 = { 16 };
        int[] p4 = { 8 };
        int[] p5 = { 4 };
        int[] p6 = { 2 };
        int nHeight = src.length / nWidth;
        int nBytesPerLine = nWidth / 8;
        byte[] data = new byte[nHeight * (8 + nBytesPerLine)];
        int offset = 0;
        int k = 0;
        for (int i = 0; i < nHeight; i++){
            offset = i * (8 + nBytesPerLine);
            data[(offset + 0)] = 29;
            data[(offset + 1)] = 118;
            data[(offset + 2)] = 48;
            data[(offset + 3)] = ((byte)(nMode & 0x1));
            data[(offset + 4)] = ((byte)(nBytesPerLine % 256));
            data[(offset + 5)] = ((byte)(nBytesPerLine / 256));
            data[(offset + 6)] = 1;
            data[(offset + 7)] = 0;
            for (int j = 0; j < nBytesPerLine; j++) {
                data[(offset + 8 + j)] =
                    ((byte)(p0[src[k]] + p1[src[(k + 1)]] + p2[src[(k + 2)]] + p3[src[(k + 3)]] + p4[src[(k + 4)]] + p5[src[(k + 5)]] + p6[src[(k + 6)]] + src[(k + 7)]));
                k += 8;
            }
        }
        return data;
    }*/

}
