package com.fenghks.business.orders;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fenghks.business.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ORDER_TYPE = "orderType";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int orderType;
    private String mParam2;

    @BindView(R.id.layout_order_swipe_refresh)
    SwipeRefreshLayout order_swipe_refresh;
    @BindView(R.id.rv_orders)
    RecyclerView rv_orders;

    private FragmentActivity mActivity;

    private Unbinder unbinder;

    private OrderAdapter orderAdapter;
    private OnFragmentInteractionListener mListener;
    private long lastUpdated;

    private boolean hasStarted = false;    //辅助标识，用于判断用户是否离开该fragment

    public OrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param orderType Parameter 1.
     * @param param2    Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(int orderType, String param2) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ORDER_TYPE, orderType);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderType = getArguments().getInt(ARG_ORDER_TYPE);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_order, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        //设置布局管理器
        rv_orders.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        int pageNo = 1;
        switch (orderType) {
            case -1:
                pageNo = 0;
                break;
            case 0:
                pageNo = 1;
                break;
            case -2:
                pageNo = 2;
                break;
            default:
                pageNo = orderType;
                break;
        }
        orderAdapter = new OrderAdapter(mActivity, pageNo);
        rv_orders.setAdapter(orderAdapter);

        //设置增加或删除条目的动画
        rv_orders.setItemAnimator(new DefaultItemAnimator());
        //设置分隔线
        rv_orders.addItemDecoration(new com.fenghks.business.tools.DividerItemDecoration(mActivity, 16));
        return view;
        //return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        order_swipe_refresh.setOnRefreshListener(this);
        refreshData(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //开始界面
            hasStarted = true;
            if (orderAdapter != null) {
                refreshData(true);
            }
        } else {
            if (order_swipe_refresh != null) {
                order_swipe_refresh.setRefreshing(false);
            }
            if (hasStarted) {
                hasStarted = false;
                //结束界面
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void refreshData(boolean hasNew) {
        if (hasNew) {
            orderAdapter.updateList();
        }
        if (order_swipe_refresh != null) {
            order_swipe_refresh.setRefreshing(false);
        }
        /*switch (orderType) {
            case AppConstants.WAITING_PLACE_ORDER:
                break;
            case AppConstants.WAITING_ORDER:
                break;
            case AppConstants.TRANSPORTING_ORDER:
                break;
            case AppConstants.COMPLETE_ORDER:
                break;
            case AppConstants.ABNORMAL_ORDER:
                break;
            case AppConstants.CHARGEBACK_ORDER:
                break;
            default:
                break;
        }
     */
    }

    /*public void searchOrder(String query){

    }*/

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * onAttach中连接监听接口 确保Activity支持该接口
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * onDetach中注销接口
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        mListener.onReqDataUpdate(-1);
    }

    /**
     * 这个互动接口必须被含有Master_Fragment 的Activity继承
     * This interface must be implemented by activities that contain this
     * 来实现Fragment与Activity直接的互通
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void onReqDataUpdate(int orderType);

        void onLoadMore(int orderType);
    }

    public abstract class EndLessOnScrollListener extends RecyclerView.OnScrollListener {
        //声明一个LinearLayoutManager
        private LinearLayoutManager mLinearLayoutManager;
        //当前页，从0开始
        private int currentPage = 0;
        //已经加载出来的Item的数量
        private int totalItemCount;
        //主要用来存储上一个totalItemCount
        private int previousTotal = 0;
        //在屏幕上可见的item数量
        private int visibleItemCount;
        //在屏幕可见的Item中的第一个
        private int firstVisibleItem;
        //是否正在上拉数据
        private boolean loading = true;

        public EndLessOnScrollListener(LinearLayoutManager linearLayoutManager) {
            this.mLinearLayoutManager = linearLayoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            if (loading) {
                //Log.d("onScrolled","firstVisibleItem: " +firstVisibleItem);
                //Log.d("onScrolled","totalPageCount:" +totalItemCount);
                //Log.d("onScrolled", "visibleItemCount:" + visibleItemCount)；
                if (totalItemCount > previousTotal) {
                    //说明数据已经加载结束
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            //这里需要好好理解
            if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem) {
                currentPage++;
                mListener.onLoadMore(currentPage);
                loading = true;
            }
        }

        //提供一个抽象方法，在Activity中监听到这个EndLessOnScrollListener 并且实现这个方法
        //public abstract void onLoadMore(int currentPage);
    }
}
