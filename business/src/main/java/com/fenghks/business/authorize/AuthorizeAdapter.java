package com.fenghks.business.authorize;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fenghks.business.R;
import com.fenghks.business.utils.CommonUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fei on 2017/5/24.
 */

public class AuthorizeAdapter extends RecyclerView.Adapter<AuthorizeAdapter.AuthorizeViewHolder> {

    private Context context;
    private List<Map<String,String>> thirdPartyList;
    private LayoutInflater inflater;

    private AuthorizeClickListener listener;

    public AuthorizeAdapter(Context context, List<Map<String,String>> thirdPartyList) {
        this.context = context;
        this.thirdPartyList = thirdPartyList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public AuthorizeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_authorize,parent,false);
        return new AuthorizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AuthorizeViewHolder holder, final int position) {
        Map<String,String> map = thirdPartyList.get(position);
        holder.tv_auth_name.setText(map.get("name"));
        if(!CommonUtil.isApkInDebug(context)){
            holder.tv_unauth.setVisibility(View.GONE);
        }
        holder.tv_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v,position,true);
            }
        });
        holder.tv_unauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v,position,false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return thirdPartyList.size();
    }

    public void setListener(AuthorizeClickListener listener) {
        this.listener = listener;
    }

    interface AuthorizeClickListener{
        void onClick(View view,int position,boolean isAuth);
    }

    public class AuthorizeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_auth_name)
        TextView tv_auth_name;
        @BindView(R.id.tv_auth)
        TextView tv_auth;
        @BindView(R.id.tv_unauth)
        TextView tv_unauth;

        public AuthorizeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
