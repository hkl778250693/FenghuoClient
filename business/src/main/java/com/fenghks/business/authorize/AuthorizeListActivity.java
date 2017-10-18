package com.fenghks.business.authorize;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fenghks.business.BaseActivity;
import com.fenghks.business.R;
import com.fenghks.business.bean.Business;
import com.fenghks.business.tools.DividerItemDecoration;
import com.fenghks.business.utils.VibratorUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthorizeListActivity extends BaseActivity {
    @BindView(R.id.tl_toolbar)
    Toolbar tl_toolbar;
    @BindView(R.id.rv_authorize)
    RecyclerView rv_authorize;

    private List<Map<String,String>> thirdPartyList;
    private AuthorizeAdapter adapter;
    private Business shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize_list);
        ButterKnife.bind(this);

        shop = (Business) getIntent().getSerializableExtra("shop");
        initAuthorizeList();
    }

    private void initAuthorizeList() {
        tl_toolbar.setTitle(getString(R.string.third_party_authorize)+"-"+shop.getName());
        setSupportActionBar(tl_toolbar);

        thirdPartyList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("id","1");
        map.put("name",getString(R.string.authorize_meituan));
        thirdPartyList.add(map);
        map = new HashMap<>();
        map.put("id","0");
        map.put("name",getString(R.string.authorize_eleme));
        thirdPartyList.add(map);
        /*map = new HashMap<>();
        map.put("id","2");
        map.put("name",getString(R.string.authorize_baidu));
        thirdPartyList.add(map);
        map = new HashMap<>();
        map.put("id","3");
        map.put("name",getString(R.string.authorize_koubei));
        thirdPartyList.add(map);
        map = new HashMap<>();
        map.put("id","4");
        map.put("name",getString(R.string.authorize_dada));
        thirdPartyList.add(map);
        map = new HashMap<>();
        map.put("id","5");
        map.put("name",getString(R.string.authorize_shansong));
        thirdPartyList.add(map);*/

        adapter = new AuthorizeAdapter(this,thirdPartyList);
        adapter.setListener(new AuthorizeAdapter.AuthorizeClickListener() {
            @Override
            public void onClick(View view,int position,boolean isAuth) {
                VibratorUtil.vibrateOnce(mContext);
                Map<String,String> map = thirdPartyList.get(position);
                Intent intent = new Intent(AuthorizeListActivity.this, AuthorizeActivity.class);
                intent.putExtra("title", map.get("name"));
                intent.putExtra("shop", shop);
                intent.putExtra("isAuth", isAuth);
                switch (map.get("id")) {
                    case "0":
                        /*Toast.makeText(mContext,R.string.waiting_dev,Toast.LENGTH_SHORT).show();
                        return;*/
                        intent.putExtra("Platform", AuthorizeActivity.AUTHORIZE_ELEME);
                        break;
                    case "1":
                        intent.putExtra("Platform", AuthorizeActivity.AUTHORIZE_MEITUAN);
                        break;
                    case "2":
                        intent.putExtra("Platform", AuthorizeActivity.AUTHORIZE_BAIDU);
                        break;
                    case "3":
                        intent.putExtra("Platform", AuthorizeActivity.AUTHORIZE_KOUBEI);
                        break;
                    case "4":
                        intent.putExtra("Platform", AuthorizeActivity.AUTHORIZE_DADA);
                        break;
                    case "5":
                        intent.putExtra("Platform", AuthorizeActivity.AUTHORIZE_SHANSONG);
                        break;
                }
                startActivity(intent);
            }
        });
        rv_authorize.setLayoutManager(new LinearLayoutManager(this));
        rv_authorize.setAdapter(adapter);
        rv_authorize.addItemDecoration(new DividerItemDecoration(this,4));
    }

}
