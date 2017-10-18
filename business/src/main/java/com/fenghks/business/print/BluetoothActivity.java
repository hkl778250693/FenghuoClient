package com.fenghks.business.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.BaseActivity;
import com.fenghks.business.R;
import com.fenghks.business.bean.Order;
import com.fenghks.business.tools.DividerItemDecoration;
import com.fenghks.business.tools.DragFloatActionButton;
import com.fenghks.business.utils.MessageUtil;
import com.fenghks.business.utils.SPUtil;
import com.fenghks.business.utils.VibratorUtil;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BluetoothActivity extends BaseActivity {

    @BindView(R.id.tl_toolbar)
    Toolbar tl_toolbar;
    @BindView(R.id.rv_bt_device)
    RecyclerView rv_bt_device;
    @BindView(R.id.add_usb_bluetooth)
    DragFloatActionButton addUsbBluetooth;

    private BTDeviceAdapter btDeviceAdapter;  //蓝牙列表适配器

    private BluetoothAdapter btAdapter = null;  //功能：获得本设备的蓝牙实例      返回值：如果设备具备蓝牙功能，返回BluetoothAdapter 实例；否则，返回null对象。
    private BluetoothDevice btPrinter = null;   //蓝牙打印设备实例

    private Order order;
    protected String print_settings;

    public static final int REQUEST_ENABLE_BT = 12321;

    /*Order order;
    String print_settings;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        ButterKnife.bind(this);

        setSwipeBackEnable(true);
        Intent intent = getIntent();
        if (intent.hasExtra(AppConstants.EXTRA_ORDER)) {
            order = (Order) intent.getSerializableExtra(AppConstants.EXTRA_ORDER);
            print_settings = intent.getStringExtra(AppConstants.EXTRA_PRINT_SETTINGS);
        }

        initView();

        // 注册蓝牙广播监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver, filter);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!btAdapter.isEnabled()) {  //蓝牙处于关闭状态，则打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);    //请求用户选择是否打开蓝牙
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();   //获取已配对的蓝牙列表
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    btDeviceAdapter.addDevice(device);
                }
            }
            /*if (btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }*/
            if (btAdapter.isDiscovering()) {    //功能：是否正在处于扫描过程中      注意：如果蓝牙没有开启，该方法会返回false
                btAdapter.cancelDiscovery();
            }
            btAdapter.startDiscovery();
        }
    }

    private void initView() {
        tl_toolbar.setTitle(R.string.connect_bluetooth_device);
        setSupportActionBar(tl_toolbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        rv_bt_device.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        btDeviceAdapter = new BTDeviceAdapter(this, null);
        btDeviceAdapter.setOnBTDeviceClickListener(new BTDeviceAdapter.OnBTDeviceClickListener() {
            @Override
            public void onClick(int position) {
                btAdapter.cancelDiscovery();
                btPrinter = btDeviceAdapter.getItem(position);
                SPUtil.putString(mActivity, AppConstants.EXTRA_PRINTER_ADDRESS, btPrinter.getAddress());  //获取蓝牙设备的硬件地址(MAC地址)
                Intent intent = new Intent(mActivity, BlueToothService.class);   //开启蓝牙服务
                intent.putExtra(AppConstants.EXTRA_PRINTER_ADDRESS, btPrinter);
                startService(intent);
                //PrintService printService = new PrintService(mActivity,btPrinter);
                //printService.connect(order,print_settings);
                finish();
            }
        });
        rv_bt_device.setAdapter(btDeviceAdapter);
        //设置增加或删除条目的动画
        rv_bt_device.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        rv_bt_device.addItemDecoration(new DividerItemDecoration(mActivity, 16));
    }

    @OnClick(R.id.add_usb_bluetooth)
    public void onViewClicked() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        btDeviceAdapter.addDevice(device);
                    }
                }
                /*if (btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(discoverableIntent);
                }*/
                //btAdapter.startDiscovery();
            } else if (resultCode == RESULT_CANCELED) {
                MessageUtil.showAlert(mActivity, R.string.bluetooth_not_opened, R.string.bluetooth_cant_use);
                //finish();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case R.id.menu_search:
            case R.id.search_order:
                VibratorUtil.vibrateOnce(mActivity);
                btAdapter.startDiscovery();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_find, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 监听蓝牙设备配对状态的广播
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 当有设备被发现的时候会收到 action == BluetoothDevice.ACTION_FOUND 的广播
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("BluetoothActivity", "发现蓝牙设备");
                //广播的 intent 里包含了一个 BluetoothDevice 对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //假设我们用一个 ListView 展示发现的设备，那么每收到一个广播，就添加一个设备到 adapter 里
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    btDeviceAdapter.addDevice(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d("BluetoothActivity", "开始搜索蓝牙设备");
            }
        }
    };
}
