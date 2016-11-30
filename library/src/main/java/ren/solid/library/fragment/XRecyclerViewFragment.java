package ren.solid.library.fragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

import ren.solid.library.R;
import ren.solid.library.fragment.base.BaseListFragment;
import ren.solid.library.utils.StringUtils;
import ren.solid.library.widget.StatusViewLayout;


/**
 * Created by _SOLID
 * Date:2016/4/18
 * Time:17:36
 * <p/>
 * common fragment for list data display ,and you can extends this fragment for everywhere you want to display list data
 */
public abstract class XRecyclerViewFragment<T> extends BaseListFragment {

    private static String TAG = "XRecyclerViewFragment";

    private XRecyclerView mRecyclerView;
    private StatusViewLayout mStatusView;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_xrecyclerview;
    }

    @Override
    protected void setUpView() {

        mStatusView = $(R.id.status_view);
        mRecyclerView = $(R.id.recyclerview);

        mRecyclerView.setLayoutManager(setLayoutManager());
        customConfig();
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallClipRotatePulse);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                switchActionAndLoadData(ACTION_REFRESH);
            }

            @Override
            public void onLoadMore() {
                switchActionAndLoadData(ACTION_LOAD_MORE);
            }
        });

        mStatusView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActionAndLoadData(ACTION_REFRESH);
            }
        });
        mRecyclerView.setLoadingMoreEnabled(isHaveLoadMore());
    }

    protected void customConfig() {

    }

    protected void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        if (mRecyclerView != null)
            mRecyclerView.addItemDecoration(itemDecoration);
    }


    public boolean isHaveLoadMore() {
        return true;
    }


    protected void addHeaderView(View v) {
        mRecyclerView.addHeaderView(v);
    }

    @Override
    protected void setUpData() {
        mStatusView.showLoading();
        loadData();
    }

    @Override
    protected void onDataErrorReceived() {
        Log.i(TAG, "onDataErrorReceived");
        mStatusView.showError();
        loadComplete();
    }

    @Override
    protected void onDataSuccessReceived(final String result) {
        Log.i(TAG, "onDataSuccessReceived");

        if (!StringUtils.isNullOrEmpty(result)) {
            List<T> list = parseData(result);
            mAdapter.addAll(list, mCurrentAction == ACTION_REFRESH);
            if (mCurrentAction != ACTION_PRE_LOAD) loadComplete();
            mStatusView.showContent();
        } else {
            if (!(mCurrentAction == ACTION_PRE_LOAD))
                onDataErrorReceived();
        }
    }

    @Override
    protected void loadComplete() {
        if (mCurrentAction == ACTION_REFRESH)
            mRecyclerView.refreshComplete();
        if (mCurrentAction == ACTION_LOAD_MORE)
            mRecyclerView.loadMoreComplete();
    }

    protected int getHeadViewCount() {
        return 3;
    }


}
