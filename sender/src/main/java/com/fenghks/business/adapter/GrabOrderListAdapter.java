package com.fenghks.business.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fenghks.business.R;
import com.fenghks.business.bean.Order;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fenghuo on 2017/10/11.
 */

public class GrabOrderListAdapter extends RecyclerView.Adapter<GrabOrderListAdapter.GrabOrderViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    private List<Order> orderList = new ArrayList<>();
    private OnOrderItemClickListener mOnOrderItemClickListener;
    private OnGrabClickListener mOnGrabClickListener;

    public GrabOrderListAdapter(Context mContext, List<Order> orderList) {
        this.mContext = mContext;
        this.orderList = orderList;
        if (null != mContext) {
            inflater = LayoutInflater.from(mContext);
        }
    }

    @Override
    public GrabOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_grab_order, parent, false);
        return new GrabOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GrabOrderViewHolder holder, int position) {
        if (mOnOrderItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnOrderItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
        if (mOnGrabClickListener != null) {
            holder.grabOrderIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnGrabClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class GrabOrderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.business_name)
        TextView businessName;
        @BindView(R.id.predict_send_time)
        TextView predictSendTime;
        @BindView(R.id.predict_order_layout)
        LinearLayout predictOrderLayout;
        @BindView(R.id.business_address_iv)
        ImageView businessAddressIv;
        @BindView(R.id.business_address_tv)
        TextView businessAddressTv;
        @BindView(R.id.business_distance_tv)
        TextView businessDistanceTv;
        @BindView(R.id.send_address_tv)
        TextView sendAddressTv;
        @BindView(R.id.send_distance_tv)
        TextView sendDistanceTv;
        @BindView(R.id.grab_order_iv)
        ImageView grabOrderIv;

        public GrabOrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnOrderItemClickListener {
        void onClick(int position);
    }

    public interface OnGrabClickListener {
        void onClick(int position);
    }

    public void setOnOrderItemClickListener(OnOrderItemClickListener onOrderItemClickListener) {
        this.mOnOrderItemClickListener = onOrderItemClickListener;
    }

    public void setOnGrabClickListener(OnGrabClickListener onGrabClickListener) {
        this.mOnGrabClickListener = onGrabClickListener;
    }

}
