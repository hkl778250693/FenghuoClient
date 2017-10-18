package com.fenghks.business.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fenghks.business.AppConstants;
import com.fenghks.business.R;
import com.fenghks.business.bean.Order;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fenghuo on 2017/10/11.
 */

public class SendOrderListAdapter extends RecyclerView.Adapter<SendOrderListAdapter.SendOrderViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    private List<Order> orderList = new ArrayList<>();
    private int tag;    //1：代表订单   2：代表配送订单记录
    private Order order;
    private OnOrderItemClickListener mOnOrderItemClickListener;
    private OnNavigationClickListener mOnNavigationClickListener;
    private OnCallBusinessClickListener mOnCallBusinessClickListener;
    private OnCallCustomerClickListener mOnCallCustomerClickListener;
    private OnSendConfirmClickListener mOnSendConfirmClickListener;
    private OnScanGetOrderClickListener mOnScanGetOrderClickListener;

    public SendOrderListAdapter(Context mContext, List<Order> orderList, int tag) {
        this.mContext = mContext;
        this.orderList = orderList;
        this.tag = tag;
        if (null != mContext) {
            inflater = LayoutInflater.from(mContext);
        }
    }

    @Override
    public SendOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_send_order, parent, false);
        return new SendOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SendOrderViewHolder holder, int position) {
        /*order = orderList.get(position);
        holder.clientFromTv.setText(order.getClientfrom());
        holder.orderTimeTv.setText(order.getStarttime());
        holder.sendDistanceTv.setText(order.getDistance());
        holder.sendAddressTv.setText(order.getSendaddress());
        holder.customNameTv.setText(order.getCustomername());
        holder.phoneNumberTv.setText(order.getCustomerphone());*/
        switch (tag) {
            case AppConstants.TAG_ORDERS:
                holder.functionLayout.setVisibility(View.VISIBLE);
                holder.finishOrderTimeLayout.setVisibility(View.GONE);
                break;
            case AppConstants.TAG_SEND_ORDERS_RECORD:
                holder.functionLayout.setVisibility(View.GONE);
                holder.finishOrderTimeLayout.setVisibility(View.VISIBLE);
                break;
        }
        if (mOnOrderItemClickListener != null) {   //item点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnOrderItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
        if (mOnNavigationClickListener != null) {   //导航按钮点击事件
            holder.navigationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnNavigationClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
        if (mOnCallBusinessClickListener != null) {  //商家电话拨打
            holder.callBusinessLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCallBusinessClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
        if (mOnCallCustomerClickListener != null) {   //客户电话拨打
            holder.callCustomerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCallCustomerClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
        if (mOnSendConfirmClickListener != null) {  //配送确认点击
            holder.sendConfirmLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnSendConfirmClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
        if (mOnScanGetOrderClickListener != null) {  //扫码取餐点击
            holder.scanGetOrderLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnScanGetOrderClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class SendOrderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.business_name_tv)
        TextView businessNameTv;
        @BindView(R.id.client_from_tv)
        TextView clientFromTv;
        @BindView(R.id.send_address_tv)
        TextView sendAddressTv;
        @BindView(R.id.send_distance_tv)
        TextView sendDistanceTv;
        @BindView(R.id.order_time_tv)
        TextView orderTimeTv;
        @BindView(R.id.take_order_time_tv)
        TextView takeOrderTimeTv;
        @BindView(R.id.navigation_layout)
        LinearLayout navigationLayout;
        @BindView(R.id.call_business_layout)
        LinearLayout callBusinessLayout;
        @BindView(R.id.call_customer_layout)
        LinearLayout callCustomerLayout;
        @BindView(R.id.send_confirm_layout)
        LinearLayout sendConfirmLayout;
        @BindView(R.id.scan_get_order_layout)
        LinearLayout scanGetOrderLayout;
        @BindView(R.id.function_layout)
        LinearLayout functionLayout;
        @BindView(R.id.finish_order_time_tv)
        TextView finishOrderTimeTv;
        @BindView(R.id.finish_order_time_layout)
        LinearLayout finishOrderTimeLayout;

        public SendOrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnOrderItemClickListener {
        void onClick(int position);
    }

    public interface OnNavigationClickListener {
        void onClick(int position);
    }

    public interface OnCallBusinessClickListener {
        void onClick(int position);
    }

    public interface OnCallCustomerClickListener {
        void onClick(int position);
    }

    public interface OnSendConfirmClickListener {
        void onClick(int position);
    }

    public interface OnScanGetOrderClickListener {
        void onClick(int position);
    }

    public void setOnOrderItemClickListener(OnOrderItemClickListener onOrderItemClickListener) {
        this.mOnOrderItemClickListener = onOrderItemClickListener;
    }

    public void setOnNavigationClickListener(OnNavigationClickListener onNavigationClickListener) {
        this.mOnNavigationClickListener = onNavigationClickListener;
    }

    public void setOnCallBusinessClickListener(OnCallBusinessClickListener onCallBusinessClickListener) {
        this.mOnCallBusinessClickListener = onCallBusinessClickListener;
    }

    public void setOnCallCustomerClickListener(OnCallCustomerClickListener onCallCustomerClickListener) {
        this.mOnCallCustomerClickListener = onCallCustomerClickListener;
    }

    public void setOnSendConfirmClickListener(OnSendConfirmClickListener onSendConfirmClickListener) {
        this.mOnSendConfirmClickListener = onSendConfirmClickListener;
    }

    public void setOnScanGetOrderClickListener(OnScanGetOrderClickListener onScanGetOrderClickListener) {
        this.mOnScanGetOrderClickListener = onScanGetOrderClickListener;
    }
}
