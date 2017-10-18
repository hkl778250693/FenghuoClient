package com.fenghks.business.tools;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Fei on 2017/4/19.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private int dividerWidth = 1;
    private Context mContext;
    private float density;

    public DividerItemDecoration(Context mContext, int dividerWidth) {
        this.dividerWidth = dividerWidth;
        this.mContext = mContext;
        this.density = mContext.getResources().getDisplayMetrics().density;
    }

    /**
     *
     * @param outRect 边界
     * @param view recyclerView ItemView
     * @param parent recyclerView
     * @param state recycler 内部数据管理
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, (int)(dividerWidth * density));
    }
}
