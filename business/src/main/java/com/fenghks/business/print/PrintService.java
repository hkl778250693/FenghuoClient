package com.fenghks.business.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.R;
import com.fenghks.business.bean.Order;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.StringUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Fei on 2017/4/26.
 */

public class PrintService {
    private Context context;

    BluetoothAdapter btAdapter;
    private static InputStream mmInStream;
    private static OutputStream mmOutStream;
    private static Handler mHandler;
    private ConnectThread mConnectThread;
    private PrintThread printThread;

    private int mState;

    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public PrintService(Context context, Handler handler) {
        this.context = context;
        this.mState = STATE_NONE;
        this.mHandler = handler;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(context, R.string.bluetooth_not_supported,Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private synchronized void setState(int state) {
        mState = state;
        mHandler.obtainMessage(AppConstants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    public void connect(BluetoothDevice btPrinter) {

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (printThread != null) {
            printThread.cancel();
            printThread = null;
        }

        mConnectThread = new ConnectThread(btPrinter);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public void connected(BluetoothSocket socket, BluetoothDevice device) {

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (printThread != null) {
            printThread.cancel();
            printThread = null;
        }

        printThread = new PrintThread(socket);
        printThread.start();
        setState(STATE_CONNECTED);

        Message msg = mHandler.obtainMessage(AppConstants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BlueToothService.MSG_DEVICENAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    public synchronized void print(Order order, String print_settings) {
        // Create temporary object
        PrintThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = printThread;
        }
        if(null != order){
            if(StringUtil.isEmpty(print_settings)){
                r.printOrder(order, "1,0,0,0,0");
            }else {
                r.printOrder(order, print_settings);
            }
        }
    }

    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (printThread != null) {
            printThread.cancel();
            printThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(STATE_NONE);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(AppConstants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BlueToothService.MSG_TOAST, context.getString(R.string.connect_to_print_failed));
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        setState(STATE_NONE);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(AppConstants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BlueToothService.MSG_TOAST, context.getString(R.string.connect_to_print_lost));
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private String getTemplateName(int print_type){
        switch (print_type) {
            case 0:
                return "business.json";
            case 1:
                return "kitchen.json";
            case 2:
                return "customer.json";
            case 3:
                return "sender.json";
            case 4:
                return "qrcode.json";
        }
        return "business.json";
    }

    private String readTemplate(Context context, String name){
        try {
            InputStreamReader isr = new InputStreamReader(context.getAssets().open(name),"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = br.readLine()) != null){
                builder.append(line);
            }
            br.close();
            isr.close();
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        public ConnectThread(BluetoothDevice btPrinter) {
            mmDevice = btPrinter;
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            BluetoothSocket tmp = null;
            try {
                tmp = btPrinter.createRfcommSocketToServiceRecord(UUID.fromString(AppConstants.SPP_UUID));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            btAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                connectException.printStackTrace();
                // Unable to connect; close the socket and get out
                connectionFailed();
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                /*try {
                    Log.e("","trying fallback...");

                    mmSocket =(BluetoothSocket) btPrinter.getClass().getMethod("createRfcommSocket",
                            new Class[] {int.class}).invoke(btPrinter,1);
                    mmSocket.connect();
                    Log.e("","Connected");
                }
                catch (Exception e2) {
                    Log.e("", "Couldn't establish Bluetooth connection!");
                }*/
                return;
            }

            synchronized (PrintService.this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class PrintThread extends Thread{
        private final BluetoothSocket mmSocket;

        public PrintThread(BluetoothSocket socket) {
            mmSocket = socket;

            OutputStream tmpOut = null;
            InputStream tmpInt = null;
            // Get the input and output streams, using temp objects because member streams are final
            try {
                tmpInt = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpInt;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            Log.i("PrintThread", "BEGIN PrintThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(AppConstants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e("PrintThread", "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void printOrder(Order order, String print_settings){
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                String[] nums = print_settings.split(",");
                EscPos escPos = EscPos.getInstance(baos);
                String template = null;
                for(int i = 0;i<nums.length;i++){
                    int count = Integer.parseInt(nums[i]);
                    if(count >= 1){
                        if(i == 4){
                            String[] nos = order.getOrdercode().split("#");
                            String qr_str = "";
                            if(nos.length>1 && nos[nos.length-1].matches("[0-9]+")){
                                qr_str += "#" + nos[nos.length-1]+ " : ";
                            }
                            escPos.printQR(CommonUtil.QR_BASE+order.getId(),qr_str + order.getGetcode());
                        }else {
                            template = readTemplate(context,getTemplateName(i));
                            String content = PrintContent.gen(context,order,i);
                            for(int j = 0;j<count;j++){
                                escPos.print(template,content);
                            }
                        }
                    }
                }
                baos.flush();
                byte[] tmp =baos.toByteArray();
                mmOutStream.write(tmp);
                mmOutStream.flush();
                mHandler.obtainMessage(AppConstants.MESSAGE_WRITE).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    mmOutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
