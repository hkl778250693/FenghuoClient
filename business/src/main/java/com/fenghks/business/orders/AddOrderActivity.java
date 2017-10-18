package com.fenghks.business.orders;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fenghks.business.AppConstants;
import com.fenghks.business.BaseActivity;
import com.fenghks.business.FenghuoApplication;
import com.fenghks.business.R;
import com.fenghks.business.bean.Business;
import com.fenghks.business.utils.CommonUtil;
import com.fenghks.business.utils.ParseUtils;
import com.fenghks.business.utils.StringUtil;
import com.fenghks.business.utils.VibratorUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOrderActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.tl_toolbar)
    Toolbar tl_toolbar;
    @BindView(R.id.et_orderno)
    EditText et_orderno;
    @BindView(R.id.et_custom_name)
    EditText et_custom_name;
    @BindView(R.id.et_custom_phone)
    EditText et_custom_phone;
    @BindView(R.id.et_custom_address)
    EditText et_custom_address;
    @BindView(R.id.tv_send_time)
    TextView tv_send_time;
    @BindView(R.id.rg_order_from)
    RadioGroup rg_order_from;
    @BindView(R.id.tv_add_item)
    TextView iv_add_item;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.ll_food_area)
    LinearLayout ll_food_area;
    /*@BindView(R.id.et_food_name1)
    EditText et_food_name1;
    @BindView(R.id.et_food_amount1)
    EditText et_food_amount1;
    @BindView(R.id.et_food_price1)
    EditText et_food_price1;*/

    private String from;

    List<FoodItem> foodItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        ButterKnife.bind(this);

        tl_toolbar.setTitle(R.string.add_order);
        setSupportActionBar(tl_toolbar);

        foodItems = new ArrayList<>();
        initView();
    }

    private void initView() {

        et_orderno.setSelection(et_orderno.getText().length());

        FoodItem foodItem = new FoodItem(mActivity, ll_food_area);
        foodItem.hideDeleteView();
        ll_food_area.addView(foodItem);
        foodItems.add(foodItem);

        //map.put("clientfrom",getString(R.string.from_other));
        from = getString(R.string.from_other);
        rg_order_from.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_from_eleme:
                        from = getString(R.string.from_eleme);
                        break;
                    case R.id.rb_from_meituan:
                        from = getString(R.string.from_meituan);
                        break;
                    case R.id.rb_from_baidu:
                        from = getString(R.string.from_baidu);
                        break;
                    case R.id.rb_from_koubei:
                        from = getString(R.string.from_koubei);
                        break;
                    case R.id.rb_from_other:
                        from = getString(R.string.from_other);
                        break;
                }
            }
        });

        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        iv_add_item.setOnClickListener(this);
        tv_send_time.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                VibratorUtil.vibrateOnce(mContext);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.confirm_giveup_title)
                        .setMessage(R.string.confirm_giveup_txt)
                        .setPositiveButton(R.string.confirm_giveup, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AddOrderActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

                break;
            case R.id.tv_send_time:
                VibratorUtil.vibrateOnce(mContext);
                DateTime now = new DateTime();
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tv_send_time.setText(hourOfDay+":"+minute);
                    }
                },now.getHourOfDay(),now.getMinuteOfHour(),true).show();
                break;
            case R.id.tv_add_item:
                VibratorUtil.vibrateOnce(mContext);
                //LayoutInflater inflater = LayoutInflater.from(this);
                //View view = inflater.inflate(R.layout.item_add_food,null);
                //ll_food_area.addView(view);
                FoodItem foodItem = new FoodItem(mActivity, ll_food_area);
                ll_food_area.addView(foodItem);
                foodItems.add(foodItem);
                break;
            case R.id.tv_confirm:
                VibratorUtil.vibrateOnce(mContext);

                final Map<String,String> map = new HashMap<>();
                map.put("clientfrom",from);
                String customerName = et_custom_name.getText().toString().trim();
                String customerPhone = et_custom_phone.getText().toString().trim();
                String customerAddress = et_custom_address.getText().toString().trim();
                String sendTime = tv_send_time.getText().toString().trim();
                if(StringUtil.isEmpty(customerName)){
                    Toast.makeText(mActivity, R.string.need_customer_name, Toast.LENGTH_SHORT).show();
                    break;
                }
                if(StringUtil.isEmpty(customerAddress)){
                    Toast.makeText(mActivity, R.string.need_customer_address, Toast.LENGTH_SHORT).show();
                    break;
                }
                if(StringUtil.isEmpty(customerPhone) || customerPhone.length() < 7){
                    Toast.makeText(mActivity, R.string.need_customer_phone, Toast.LENGTH_SHORT).show();
                    break;
                }
                map.put("customername",customerName);
                map.put("customerphone",customerPhone);
                map.put("sendaddress",customerAddress);

                JSONArray items = new JSONArray();
                Double totalprice = 0.0;
                try {
                    for (int i=0;i<foodItems.size();i++) {
                        JSONObject obj = foodItems.get(i).getFoodItem();
                        if (obj == null) {
                            Toast.makeText(mActivity, R.string.need_price, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        totalprice += obj.optDouble("price") * obj.optInt("amount");
                        items.put(i, obj);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    break;
                }
                map.put("detail",items.toString());
                map.put("totalprice","" + totalprice);

                if(StringUtil.isEmpty(sendTime)){
                    sendTime = new DateTime().plusMinutes(2).toString("YYYY-MM-DD HH:mm:ss.SSS");
                }else {
                    LocalTime send = LocalTime.parse(sendTime);
                    if(send.isBefore(new LocalTime())){
                        sendTime = new DateTime().plusDays(1).toString("yyyy-MM-dd")+
                                " " + send.toString("HH:mm:ss.SSS");
                    }else {
                        sendTime = new DateTime().toString("yyyy-MM-dd")+" "+send.toString("HH:mm:ss.SSS");
                    }
                }
                map.put("sendtime",sendTime);

                Business shop = FenghuoApplication.shops.get(FenghuoApplication.currentShop);
                map.put("businessid", ""+shop.getId());
                map.put("ordercode",et_orderno.getText().toString().trim());

                map.put("note","");
                System.out.println(map.toString());
                x.http().post(CommonUtil.genGetParam(CommonUtil.CREATE_ORDERS_URL, map), new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println(result);
                        JSONObject obj = ParseUtils.parseDataString(mContext,result);
                        if(obj != null && obj.has("orderid")){
                            Toast.makeText(mActivity,R.string.add_order_success,Toast.LENGTH_SHORT).show();
                            sendBroadcast(new Intent(AppConstants.ACTION_ORDER_CREATED));
                            AddOrderActivity.this.finish();
                        }else {
                            //CommonUtil.handleErr(mActivity,result);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CommonUtil.networkErr(ex,isOnCallback);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
                break;
        }
    }

    class FoodItem extends LinearLayout{
        @BindView(R.id.et_food_name)
        EditText et_food_name;
        @BindView(R.id.et_food_price)
        EditText et_food_price;
        @BindView(R.id.et_food_amount)
        EditText et_food_amount;
        @BindView(R.id.iv_delete_food)
        ImageView iv_delete_food;

        private View rootView;
        public ViewGroup parent;

        public FoodItem(Context context, ViewGroup parent) {
            super(context);
            this.parent = parent;
            rootView = LayoutInflater.from(context).inflate(R.layout.item_add_food,null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            setOrientation(HORIZONTAL);
            rootView.setLayoutParams(lp);
            ButterKnife.bind(this, rootView);
            addView(rootView);
        }

        @OnClick(R.id.iv_delete_food)
        public void deleteFoodItem(){
            parent.removeView(this);
            foodItems.remove(this);
        }

        public void hideDeleteView(){
            iv_delete_food.setVisibility(INVISIBLE);
        }

        public JSONObject getFoodItem() throws JSONException {
            String name = et_food_name.getText().toString().trim();
            name = StringUtil.isEmpty(name)?getString(R.string.food_other):name;
            String txt_price = et_food_price.getText().toString().trim();
            String txt_amount = et_food_amount.getText().toString().trim();
            txt_amount = StringUtil.isEmpty(txt_amount)?"1":txt_amount;
            int amount = Integer.parseInt(txt_amount);
            if(StringUtil.isEmpty(txt_price)){
                return null;
            }
            Float price = Float.parseFloat(txt_price);
            if(price < 0){
                return null;
            }
            if(amount <= 0){
                amount = 1;
            }
            JSONObject obj = new JSONObject();
            obj.put("name",name);
            obj.put("price",price);
            obj.put("amount",amount);
            return obj;
        }

    }

}
