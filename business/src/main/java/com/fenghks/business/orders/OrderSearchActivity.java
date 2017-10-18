package com.fenghks.business.orders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.fenghks.business.BaseActivity;
import com.fenghks.business.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderSearchActivity extends BaseActivity {

    @BindView(R.id.tl_toolbar)
    Toolbar search_bar;
    @BindView(R.id.rv_search_result)
    RecyclerView search_result;

    private SearchView mSearchView;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_search);
        ButterKnife.bind(this);
        setSupportActionBar(search_bar);

        initView();
    }

    private void initView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        //设置布局管理器
        search_result.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper. VERTICAL);
        //设置Adapter
        orderAdapter = new OrderAdapter(this,0);
        orderAdapter.setOnOrderItemClickListener(new OrderAdapter.OnOrderItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent mIntent = new Intent(mActivity,OrderDetailActivity.class);
                mActivity.startActivity(mIntent);
            }

            @Override
            public void onLongClick(int position) {

            }
        });
        search_result.setAdapter( orderAdapter);
        //增加移除动画
        search_result.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        search_result.addItemDecoration( new com.fenghks.business.tools.DividerItemDecoration(mActivity,16));
        //设置增加或删除条目的动画
        search_result.setItemAnimator( new DefaultItemAnimator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search_order);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                orderAdapter.searchList(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
