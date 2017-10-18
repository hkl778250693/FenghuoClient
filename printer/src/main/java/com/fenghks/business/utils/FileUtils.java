package com.fenghks.business.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileUtils {

    public static final int REQUEST_CODE_FOR_FILE_SELECT = 1010;
    private static final String TAG = FileUtils.class.getSimpleName();

    public static String getRealFilePath(Context ctx, Uri uri) {
        Log.d(TAG, uri.getScheme());
        Log.d(TAG, uri.getPath());
        String path = uri.getPath();
        return path;
    }

    public static void openChooseFileSelectActivity(Activity activity) {
        int readExternalStoragePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalStoragePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.d(TAG, "read: " + readExternalStoragePermission);
        Log.d(TAG, "write: " + writeExternalStoragePermission);
        if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED || writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "您没有文件读写权限", Toast.LENGTH_LONG).show();
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                activity.startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), REQUEST_CODE_FOR_FILE_SELECT);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, "请先安装文件管理器", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static String uploadFile(String url, String filePath) {
        try {
            String BOUNDARY = "---------------------------jkhsdgklh12123123123112";
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.connect();
            OutputStream out = conn.getOutputStream();
            File file = new File(filePath);
            StringBuilder builder = new StringBuilder();
            builder.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
            builder.append("Content-Disposition: form-data; name=\"norfile\"; filename=\"" + getFileName(filePath) + "\"\r\n");
            builder.append("Content-Type:" + getContentType(file) + "\r\n\r\n");
            out.write(builder.toString().getBytes());

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();

            out.write(("\r\n--" + BOUNDARY + "--\r\n").getBytes());
            out.flush();
            out.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = null;
            switch (conn.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    StringBuilder resultBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        resultBuilder.append(line);
                    }
                    result = resultBuilder.toString();
                    break;
                default:
                    break;
            }
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getContentType(File file) {
        /*if (file != null) {
            try {
                MagicMatch match = new Magic().getMagicMatch(file, true);
                return match.getMimeType();
            } catch (MagicParseException e) {

            } catch (MagicMatchNotFoundException e) {
                e.printStackTrace();
            } catch (MagicException e) {
                e.printStackTrace();
            }
        }*/
        return null;
    }

    private static String getFileName(String filePath) {
        if (filePath != null) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return null;
    }
}
