package com.fenghks.business.print;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fenghks.business.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fei on 2017/4/25.
 */

public class BTDeviceAdapter extends RecyclerView.Adapter<BTDeviceAdapter.BTViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<BluetoothDevice> list;

    public BTDeviceAdapter(Context context,List<BluetoothDevice> list){
        this.context = context;
        this.list = list;
        if(this.list == null){
            this.list = new ArrayList<>();
        }
        inflater = LayoutInflater.from(context);
    }

    public void addDevice(BluetoothDevice device){
        this.list.add(device);
        notifyDataSetChanged();
    }

    @Override
    public BTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_bt_device,parent,false);
        return new BTDeviceAdapter.BTViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BTViewHolder holder, final int position) {
        BluetoothDevice device = list.get(position);
        String preffix = "";
        switch (device.getBondState()) {
            case BluetoothDevice.BOND_BONDED:
                holder.tv_bt_device_bond.setText(R.string.bluetooth_bonded);
                break;
            case BluetoothDevice.BOND_BONDING:
                holder.tv_bt_device_bond.setText(R.string.bluetooth_bonding);
                break;
            case BluetoothDevice.BOND_NONE:
                holder.tv_bt_device_bond.setText(R.string.bluetooth_unbonded);
                break;
        }
        holder.tv_bt_device_name.setText(device.getName());
        holder.tv_bt_device_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(position);
            }
        });
    }

    public interface OnBTDeviceClickListener{
        public void onClick(int position);
    }

    private OnBTDeviceClickListener listener;

    public void setOnBTDeviceClickListener(OnBTDeviceClickListener listener){
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public BluetoothDevice getItem(int position){
        return list.get(position);
    }

    public class BTViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_bt_device_name)
        TextView tv_bt_device_name;
        @BindView(R.id.tv_bt_device_bond)
        TextView tv_bt_device_bond;

        public BTViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
