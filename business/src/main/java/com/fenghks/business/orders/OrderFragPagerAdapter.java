package com.fenghks.business.orders;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fei on 2017/1/20.
 */

public class OrderFragPagerAdapter extends FragmentPagerAdapter {

    private final List<OrderFragment> listFragments = new ArrayList<>();//添加的Fragment的集合
    private final List<String> listFragTitles = new ArrayList<>();//每个Fragment对应的title的集合

    public OrderFragPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * @param fragment      添加Fragment
     * @param fragTitle Fragment的标题，即TabLayout中对应Tab的标题
     */
    public void addFragment(OrderFragment fragment, String fragTitle) {
        listFragments.add(fragment);
        listFragTitles.add(fragTitle);
    }

    @Override
    public OrderFragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getCount() {
        return listFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listFragTitles.get(position);
    }
}
