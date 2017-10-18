package com.fenghks.business.print;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.R;
import com.fenghks.business.bean.Order;
import com.fenghks.business.utils.SPUtil;
import com.fenghks.business.utils.StringUtil;

import java.util.UUID;

public class BlueToothService extends Service {
    private static final UUID uuid = UUID.fromString(AppConstants.SPP_UUID);

    private PrintService printService;
    private BluetoothAdapter btAdapter = null;
    private BluetoothDevice btPrinter = null;

    Order order = null;
    String print_settings = null;
    private boolean connected = false;
    private boolean needPrint = false;

    public static final String MSG_TOAST = "toast";
    public static final String MSG_DEVICENAME= "device_name";

    public BlueToothService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(btAdapter == null){
            btAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (btAdapter == null) {
            // Device does not support Bluetooth 设备不支持蓝牙功能
            Toast.makeText(this, R.string.bluetooth_not_supported,Toast.LENGTH_SHORT).show();
            //SPUtil.putBoolean(this,"isBluetoothSupported",false);
            stopSelf();  //关闭服务
        }
        printService = new PrintService(this,mHandler);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);  //蓝牙状态值发生改变
        registerReceiver(mReceiver,filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(null == printService){
            printService = new PrintService(this,mHandler);
        }
        //获取订单参数
        if(null != intent && intent.hasExtra(AppConstants.EXTRA_ORDER)){
            order = (Order)intent.getSerializableExtra(AppConstants.EXTRA_ORDER);
            print_settings = intent.getStringExtra(AppConstants.EXTRA_PRINT_SETTINGS);
            needPrint = true;
        }
        //获取打印机MAC地址
        if(null != intent && intent.hasExtra(AppConstants.EXTRA_PRINTER_ADDRESS)){
            BluetoothDevice device = intent.getParcelableExtra(AppConstants.EXTRA_PRINTER_ADDRESS);
            if(connected && device.getAddress().equals(btPrinter.getAddress())){
                return super.onStartCommand(intent, flags, startId);
            }
            if(null != printService) {
                printService.stop();
                connected = false;
                btPrinter = device;
                printService.connect(btPrinter);
            }
            return super.onStartCommand(intent, flags, startId);
        }
        if (!btAdapter.isEnabled()) {   //蓝牙处于关闭状态，则打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        }else {
            startConnect();
        }
        return Service.START_STICKY;
    }

    private void startConnect(){
        if(connected){
            if(needPrint){
                printService.print(order,print_settings);
                needPrint = false;
            }
        }else {
            printService.stop();
            String mac = SPUtil.getString(this,AppConstants.EXTRA_PRINTER_ADDRESS);
            if(StringUtil.isEmpty(mac)){
                Intent mIntent = new Intent(this, BluetoothActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mIntent);
            }else {
                btPrinter = btAdapter.getRemoteDevice(mac);
                printService.connect(btPrinter);
                /*if(order != null){
                    printService.print(order,print_settings);
                }*/
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (printService != null){
            printService.stop();
            printService = null;
        }
        unregisterReceiver(mReceiver);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //FragmentActivity activity = getActivity();
            switch (msg.what) {
                case AppConstants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case PrintService.STATE_CONNECTED:
                            connected = true;
                            if(needPrint){
                                printService.print(order,print_settings);
                                needPrint = false;
                            }
                            break;
                        case PrintService.STATE_CONNECTING:
                            break;
                        case PrintService.STATE_LISTEN:
                        case PrintService.STATE_NONE:
                            connected = false;
                            break;
                    }
                    break;
                case AppConstants.MESSAGE_WRITE:
                    System.out.println();
                    break;
                case AppConstants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    System.out.println(readMessage);
                    break;
                case AppConstants.MESSAGE_DEVICE_NAME:
                    Toast.makeText(BlueToothService.this, getString(R.string.connect_to_bt_printer)
                            + msg.getData().getString(MSG_DEVICENAME), Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.MESSAGE_TOAST:
                    Toast.makeText(BlueToothService.this,msg.getData().getString(MSG_TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 当有设备被发现的时候会收到 action == BluetoothDevice.ACTION_FOUND 的广播
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.STATE_OFF) == BluetoothAdapter.STATE_ON){
                    startConnect();  //如果是开启状态则开始连接
                }
            }
        }
    };

}
