package com.fenghks.business.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.authorize.AuthorizeListActivity;
import com.fenghks.business.bean.Business;
import com.fenghks.business.tools.GlideCircleTransform;
import com.fenghks.business.utils.VibratorUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fei on 2017/4/18.
 */

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    private OnShopItemClickListener onShopItemClickListener;

    public ShopAdapter(Context mContext) {
        this.mContext = mContext;
        if(null != mContext){
            inflater = LayoutInflater.from(mContext);
        }
    }

    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_shop,parent,false);
        Log.d("ShopAdapter", "适配器进来了");
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShopViewHolder holder, final int position) {
        final Business shop = FenghuoApplication.shops.get(position);
        Glide.with(mContext).load(shop.getLogimg()).
                transform(new GlideCircleTransform(mContext)).into(holder.shop_image);
        holder.shop_name.setText(shop.getName());
        holder.shop_address.setText(shop.getAddress());
        //holder.tv_authorize.setVisibility(View.GONE);
        holder.tv_authorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mContext);
                Intent intent = new Intent(mContext, AuthorizeListActivity.class);
                intent.putExtra("shop",shop);
                mContext.startActivity(intent);
            }
        });
        if(onShopItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShopItemClickListener.onClick(position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onShopItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        Log.d("ShopAdapter", "getItemCount（）进来了");
        return FenghuoApplication.shops.size();
    }


    public interface OnShopItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }
    public void setOnShopItemClickListener(OnShopItemClickListener onShopItemClickListener ){
        this.onShopItemClickListener=onShopItemClickListener;
    }


    class ShopViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.shop_image)
        ImageView shop_image;
        @BindView(R.id.shop_name)
        TextView shop_name;
        @BindView(R.id.shop_address)
        TextView shop_address;
        @BindView(R.id.tv_authorize)
        TextView tv_authorize;

        public ShopViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
