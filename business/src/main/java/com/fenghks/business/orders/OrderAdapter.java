package com.fenghks.business.orders;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.bean.Business;
import com.fenghks.business.bean.Order;
import com.fenghks.business.bean.Sender;
import com.fenghks.business.print.BlueToothService;
import com.fenghks.business.tools.QRDialog;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.StringUtil;
import com.fenghks.business.utils.VibratorUtil;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fei on 2017/4/21.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private FragmentActivity mContext;
    private LayoutInflater inflater;
    private OnOrderItemClickListener onOrderItemClickListener;
    private List<Order> orders;
    private int pageNo;

    public OrderAdapter(FragmentActivity mContext, int pageNo) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.pageNo = pageNo;
        if (pageNo != 0) {
            orders = new ArrayList<>();
            //updateList();
        } else {
            orders = FenghuoApplication.orders;
        }
    }

    public void updateList() {
        if (pageNo == 0) {
            notifyDataSetChanged();
            return;
        }
        orders.clear();
        for (int i = 0; i < FenghuoApplication.orders.size(); i++) {
            Order order = FenghuoApplication.orders.get(i);
            switch (pageNo) {
                case 1:
                    if (order.getState() == 0) {
                        orders.add(order);
                    }
                    break;
                case 2:
                    if (order.getState() == 1 || order.getState() == 2) {
                        orders.add(order);
                    }
                    break;
                default:
                    if (order.getState() == pageNo) {
                        orders.add(order);
                    }
                    break;
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 响应搜索
     */
    public void searchList(String keyword) {
        int searchType = 0; //0不筛选 1原订单号 2手机号 3地址
        if (StringUtil.isNumric(keyword)) {
            if (keyword.length() <= 3) {
                searchType = 1;
            } else {
                searchType = 2;
            }
        } else if (StringUtil.isNotEmpty(keyword)) {
            searchType = 3;
        }
        if (searchType == 0) {
            orders = FenghuoApplication.orders;
        } else {
            orders = new ArrayList<>();
            for (int i = 0; i < FenghuoApplication.orders.size(); i++) {
                Order order = FenghuoApplication.orders.get(i);
                switch (searchType) {
                    case 1:
                        if (order.getOrdercode().endsWith(keyword)) {
                            orders.add(order);
                        }
                        break;
                    case 2:
                        if (order.getCustomerphone().contains(keyword)) {
                            orders.add(order);
                        }
                        break;
                    case 3:
                        if (order.getSendaddress().contains(keyword)) {
                            orders.add(order);
                        }
                        break;
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderViewHolder holder, int position) {
        final Order order = orders.get(position);
        holder.tv_orderFrom.setText(order.getClientfrom());
        holder.tv_ordercode.setText(order.getOrdercode().replaceAll(".*#", "#"));
        if (order.getOrderType() == 0) {
            holder.tv_order_type.setText(R.string.send_now);
            holder.tv_pre_order_time.setVisibility(View.GONE);
        } else {
            holder.tv_order_type.setText(R.string.pre_order);
            holder.tv_pre_order_time.setVisibility(View.VISIBLE);
            holder.tv_pre_order_time.setText(new DateTime(order.getSendtime()).toString("HH:mm"));
        }
        holder.tv_customName.setText(order.getCustomername());
        holder.tv_customPhone.setText(order.getCustomerphone());
        holder.tv_address.setText(order.getSendaddress());
        holder.tv_distance.setText(order.getDistance() / 1000.0d + mContext.getString(R.string.kilometre));
        DateTime createTime = new DateTime(order.getCreatetime());
        holder.tv_order_time.setText(createTime.toString("MM-dd HH:mm"));
        switch (order.getState()) {
            case 0:
                holder.tv_calcel_order.setVisibility(View.GONE);
                holder.tv_place_order.setVisibility(View.VISIBLE);
                holder.ll_send_time.setVisibility(View.GONE);
                holder.ll_sender.setVisibility(View.GONE);
                holder.iv_qrcode.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.tv_calcel_order.setVisibility(View.VISIBLE);
                holder.tv_place_order.setVisibility(View.GONE);
                holder.ll_send_time.setVisibility(View.GONE);
                holder.ll_sender.setVisibility(View.GONE);
                holder.iv_qrcode.setVisibility(View.VISIBLE);
                break;
            case 2:
                holder.tv_calcel_order.setVisibility(View.VISIBLE);
                holder.tv_place_order.setVisibility(View.GONE);
                holder.iv_qrcode.setVisibility(View.VISIBLE);
                holder.ll_send_time.setVisibility(View.GONE);
                for (int i = 0; i < FenghuoApplication.senders.size(); i++) {
                    Sender sender = FenghuoApplication.senders.get(i);
                    if (sender.getSenderid() == order.getSenderid()) {
                        holder.tv_sender_name.setText(sender.getName());
                        holder.tv_sender_phone.setText(sender.getPhone());
                        holder.ll_sender.setVisibility(View.VISIBLE);
                        break;
                    }
                    if (i == FenghuoApplication.senders.size()) {
                        holder.ll_sender.setVisibility(View.GONE);
                    }
                }
                break;
            case 3:
                for (int i = 0; i < FenghuoApplication.senders.size(); i++) {
                    Sender sender = FenghuoApplication.senders.get(i);
                    if (sender.getSenderid() == order.getSenderid()) {
                        holder.tv_sender_name.setText(sender.getName());
                        holder.tv_sender_phone.setText(sender.getPhone());
                        holder.ll_sender.setVisibility(View.VISIBLE);
                        break;
                    }
                    if (i == FenghuoApplication.senders.size()) {
                        holder.ll_sender.setVisibility(View.GONE);
                    }
                }
                holder.tv_take_time.setText(new DateTime(order.getGettime()).toString("HH:mm"));
                holder.ll_send_time.setVisibility(View.VISIBLE);
                holder.tv_urge.setVisibility(View.VISIBLE);
                holder.tv_calcel_order.setVisibility(View.VISIBLE);
                holder.tv_place_order.setVisibility(View.GONE);
                holder.iv_qrcode.setVisibility(View.VISIBLE);
                break;
            case 4:
                for (int i = 0; i < FenghuoApplication.senders.size(); i++) {
                    Sender sender = FenghuoApplication.senders.get(i);
                    if (sender.getSenderid() == order.getSenderid()) {
                        holder.tv_sender_name.setText(sender.getName());
                        holder.tv_sender_phone.setText(sender.getPhone());
                        holder.ll_sender.setVisibility(View.VISIBLE);
                        break;
                    }
                    if (i == FenghuoApplication.senders.size()) {
                        holder.ll_sender.setVisibility(View.GONE);
                    }
                }
                holder.tv_take_time.setText(new DateTime(order.getGettime()).toString("HH:mm"));
                holder.tv_finish_time.setText(new DateTime(order.getArrivedtime()).toString("HH:mm"));
                holder.ll_send_time.setVisibility(View.VISIBLE);
                holder.tv_urge.setVisibility(View.GONE);
                holder.iv_qrcode.setVisibility(View.GONE);
                holder.tv_place_order.setVisibility(View.GONE);
                holder.tv_calcel_order.setVisibility(View.GONE);
                break;
            case 5:
                for (int i = 0; i < FenghuoApplication.senders.size(); i++) {
                    Sender sender = FenghuoApplication.senders.get(i);
                    if (sender.getSenderid() == order.getSenderid()) {
                        holder.tv_sender_name.setText(sender.getName());
                        holder.tv_sender_phone.setText(sender.getPhone());
                        holder.ll_sender.setVisibility(View.VISIBLE);
                        break;
                    }
                    if (i == FenghuoApplication.senders.size()) {
                        holder.ll_sender.setVisibility(View.GONE);
                    }
                }
                holder.tv_take_time.setText(new DateTime(order.getGettime()).toString("HH:mm"));
                holder.ll_send_time.setVisibility(View.VISIBLE);
                holder.tv_urge.setVisibility(View.GONE);
                holder.iv_qrcode.setVisibility(View.GONE);
                holder.tv_calcel_order.setVisibility(View.GONE);
                holder.tv_place_order.setVisibility(View.GONE);
                break;
            case 6:
                holder.ll_sender.setVisibility(View.GONE);
                holder.ll_send_time.setVisibility(View.GONE);
                holder.iv_qrcode.setVisibility(View.GONE);
                holder.tv_calcel_order.setVisibility(View.GONE);
                holder.tv_place_order.setVisibility(View.GONE);
                break;
        }

        holder.tv_call_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mContext);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + order.getCustomerphone());
                intent.setData(data);
                mContext.startActivity(intent);
            }
        });

        holder.tv_call_sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mContext);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + holder.tv_sender_phone.getText());
                intent.setData(data);
                mContext.startActivity(intent);
            }
        });

        holder.tv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mContext);
                Intent intent = new Intent(mContext, AMapActivity.class);
                intent.putExtra(AppConstants.EXTRA_ORDER, order);
                mContext.startActivity(intent);
                //Toast.makeText(mContext,R.string.waiting_dev,Toast.LENGTH_SHORT).show();
            }
        });

        holder.tv_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mContext);
                if (order.getItems() != null) {
                    Intent intent = new Intent(mContext, BlueToothService.class);
                    intent.putExtra(AppConstants.EXTRA_ORDER, order);
                    intent.putExtra(AppConstants.EXTRA_PRINT_SETTINGS, CommonUtil.getPrintSettings(mContext));
                    mContext.startService(intent);
                } else {
                    final Map map = new HashMap();
                    map.put("orderid", order.getId());
                    x.http().post(CommonUtil.genGetParam(CommonUtil.GET_ORDERS_ITEMS_URL, map), new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            System.out.println(result);
                            JSONObject obj = ParseUtils.parseDataString(mContext,result);
                            if (null != obj) {
                                Order tmp = ParseUtils.parseOrderDetails(obj);
                                order.setItems(tmp.getItems());
                                order.setExtras(tmp.getExtras());
                                Intent intent = new Intent(mContext, BlueToothService.class);
                                intent.putExtra(AppConstants.EXTRA_ORDER, order);
                                intent.putExtra(AppConstants.EXTRA_PRINT_SETTINGS, CommonUtil.getPrintSettings(mContext));
                                mContext.startService(intent);
                            } else {
                                //CommonUtil.handleErr(mContext, result);
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            ex.printStackTrace();
                            CommonUtil.networkErr(ex, isOnCallback);
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                }
                //Toast.makeText(mContext,R.string.waiting_dev,Toast.LENGTH_SHORT).show();
            }
        });

        holder.tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mContext);
                final Map map = new HashMap();
                map.put("orderid", order.getId());
                Business shop = FenghuoApplication.shops.get(FenghuoApplication.currentShop);
                map.put("start_lon", shop.getLon());
                map.put("start_lat", shop.getLat());
                x.http().post(CommonUtil.genGetParam(CommonUtil.START_ORDERS_URL, map), new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println(result);
                        JSONArray objs = ParseUtils.parseDataArray(mContext,result);
                        if (null != objs) {
                            Toast.makeText(mContext, R.string.start_order_ok, Toast.LENGTH_SHORT).show();
                            mContext.sendBroadcast(new Intent(AppConstants.ACTION_ORDER_STATE_CHANGED));
                        } else {
                            //CommonUtil.handleErr(mContext, result);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ex.printStackTrace();
                        CommonUtil.networkErr(ex, isOnCallback);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });

        holder.tv_calcel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mContext);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.confirm_cancel_title)
                        .setMessage(R.string.confirm_cancel_txt)
                        .setPositiveButton(R.string.confirm_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Map map = new HashMap();
                                map.put("orderid", order.getId());
                                x.http().post(CommonUtil.genGetParam(CommonUtil.CHARGEBACK_ORDERS_URL, map),
                                        new Callback.CommonCallback<String>() {
                                            @Override
                                            public void onSuccess(String result) {
                                                System.out.println(result);
                                                JSONArray objs = ParseUtils.parseDataArray(mContext,result);
                                                if (null != objs) {
                                                    Toast.makeText(mContext, ParseUtils.parseMsg(result), Toast.LENGTH_SHORT).show();
                                                    mContext.sendBroadcast(new Intent(AppConstants.ACTION_ORDER_STATE_CHANGED));
                                                } else {
                                                    //CommonUtil.handleErr(mContext, result);
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable ex, boolean isOnCallback) {
                                                ex.printStackTrace();
                                                CommonUtil.networkErr(ex, isOnCallback);
                                            }

                                            @Override
                                            public void onCancelled(CancelledException cex) {

                                            }

                                            @Override
                                            public void onFinished() {

                                            }
                                        });
                            }
                        })
                        .setNegativeButton(R.string.giveup, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();


            }
        });

        holder.iv_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mContext);
                //QRCodeUtil.showQR(mContext,CommonUtil.QR_BASE+order.getId(),480);
                QRDialog dialog = QRDialog.newInstance(order);
                dialog.show(mContext.getSupportFragmentManager(), "qrdialog");
            }
        });

        holder.tv_urge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibratorUtil.vibrateOnce(mContext);
                //Toast.makeText(mContext,R.string.waiting_dev,Toast.LENGTH_SHORT).show();
                Map map = new HashMap();
                map.put("orderid", order.getId());
                map.put("businessid", order.getBusinessid());
                map.put("senderid", order.getSenderid());
                map.put("messagetype", "2");
                map.put("content", mContext.getString(R.string.urge_order) + " : " +
                        FenghuoApplication.shops.get(FenghuoApplication.currentShop).getName()
                        + "-" + order.getOrdercode() + "," + order.getSendaddress()
                        + "," + order.getCustomername() + "," + order.getCustomerphone());
                x.http().post(CommonUtil.genGetParam(CommonUtil.PUSH_TO_SENDER_URL, map), new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println(result);
                        JSONArray objs = ParseUtils.parseDataArray(mContext,result);
                        if (objs != null && objs.length() > 0) {
                            Toast.makeText(mContext, R.string.urge_order_success, Toast.LENGTH_SHORT).show();
                        } else {
                            //CommonUtil.handleErr(mContext, result);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CommonUtil.networkErr(ex, isOnCallback);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public interface OnOrderItemClickListener {
        void onClick(int position);

        void onLongClick(int position);
    }

    public void setOnOrderItemClickListener(OnOrderItemClickListener listener) {
        this.onOrderItemClickListener = listener;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_sender)
        LinearLayout ll_sender;
        @BindView(R.id.ll_send_time)
        LinearLayout ll_send_time;

        @BindView(R.id.tv_orderFrom)
        TextView tv_orderFrom;
        @BindView(R.id.tv_ordercode)
        TextView tv_ordercode;
        @BindView(R.id.tv_order_type)
        TextView tv_order_type;
        @BindView(R.id.tv_pre_order_time)
        TextView tv_pre_order_time;

        @BindView(R.id.tv_customName)
        TextView tv_customName;
        @BindView(R.id.tv_customPhone)
        TextView tv_customPhone;
        @BindView(R.id.tv_call_customer)
        TextView tv_call_customer;

        @BindView(R.id.tv_address)
        TextView tv_address;
        @BindView(R.id.tv_distance)
        TextView tv_distance;
        @BindView(R.id.tv_order_time)
        TextView tv_order_time;

        @BindView(R.id.tv_sender_name)
        TextView tv_sender_name;
        @BindView(R.id.tv_sender_phone)
        TextView tv_sender_phone;
        @BindView(R.id.tv_call_sender)
        TextView tv_call_sender;

        @BindView(R.id.tv_take_time)
        TextView tv_take_time;
        @BindView(R.id.tv_finish_time)
        TextView tv_finish_time;
        @BindView(R.id.tv_urge)
        TextView tv_urge;

        @BindView(R.id.tv_map)
        TextView tv_map;
        @BindView(R.id.tv_print)
        TextView tv_print;
        @BindView(R.id.tv_place_order)
        TextView tv_place_order;
        @BindView(R.id.tv_calcel_order)
        TextView tv_calcel_order;
        @BindView(R.id.iv_qrcode)
        ImageView iv_qrcode;


        public OrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
