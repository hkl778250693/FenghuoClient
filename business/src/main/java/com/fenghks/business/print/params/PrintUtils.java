package com.fenghks.business.print.params;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Fei on 2017/2/4.
 */

public class PrintUtils {

    // 复位打印机
    public static final byte[] RESET = {0x1b, 0x40};
    // 左对齐
    public static final byte[] ALIGN_LEFT = {0x1b, 0x61, 0x00};
    // 中间对齐
    public static final byte[] ALIGN_CENTER = {0x1b, 0x61, 0x01};
    // 右对齐
    public static final byte[] ALIGN_RIGHT = {0x1b, 0x61, 0x02};
    // 选择加粗模式
    public static final byte[] BOLD = {0x1b, 0x45, 0x01};
    // 取消加粗模式
    public static final byte[] BOLD_CANCEL = {0x1b, 0x45, 0x00};
    // 宽高加倍
    public static final byte[] DOUBLE_HEIGHT_WIDTH = {0x1d, 0x21, 0x11};
    // 宽加倍
    public static final byte[] DOUBLE_WIDTH = {0x1d, 0x21, 0x10};
    // 高加倍
    public static final byte[] DOUBLE_HEIGHT = {0x1d, 0x21, 0x01};
    // 字体不放大
    public static final byte[] NORMAL = {0x1d, 0x21, 0x00};
    // 设置默认行间距
    public static final byte[] LINE_SPACING_DEFAULT = {0x1b, 0x32};

    private static OutputStream outputStream;

    /**
   * 设置打印格式
   * @param command 格式指令
   */
    public static void selectCommand(byte[] command) {
        try {
            outputStream.write(command);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印文字
     * @param text 要打印的文字
     */
    public static void printText(String text) {
        try {
            byte[] data = text.getBytes("gbk");
            outputStream.write(data, 0, data.length);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印二维码
     * @param qrData 二维码的内容
     * @throws IOException
     */
    protected void printQRCode(String qrData){
        int moduleSize = 8;
        try {
            int length = qrData.getBytes("gbk").length;
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            //打印二维码矩阵
            writer.write(0x1D);// init
            writer.write("(k");// adjust height of barcode
            writer.write(length + 3); // pl
            writer.write(0); // ph
            writer.write(49); // cn
            writer.write(80); // fn
            writer.write(48); //
            writer.write(qrData);

            writer.write(0x1D);
            writer.write("(k");
            writer.write(3);
            writer.write(0);
            writer.write(49);
            writer.write(69);
            writer.write(48);

            writer.write(0x1D);
            writer.write("(k");
            writer.write(3);
            writer.write(0);
            writer.write(49);
            writer.write(67);
            writer.write(moduleSize);

            writer.write(0x1D);
            writer.write("(k");
            writer.write(3); // pl
            writer.write(0); // ph
            writer.write(49); // cn
            writer.write(81); // fn
            writer.write(48); // m

            writer.flush();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
