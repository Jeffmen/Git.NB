package com.example.gitnb.app;

import com.example.gitnb.R;
import com.example.gitnb.api.rxjava.ApiRxJavaClient;
import com.example.gitnb.api.rxjava.ApiRxJavaService;
import com.example.gitnb.utils.MessageUtils;
import com.example.gitnb.utils.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

public class BaseFragment extends Fragment{
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ApiRxJavaService apiRxJavaService;
	protected int page = 1;

    public static BaseFragment newInstance() {
        BaseFragment mainFragment = new BaseFragment();
        return mainFragment;
    }

    public void initSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(
        		android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            	page = 1;
            	startRefresh();
            }
        });
    }

    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return mSwipeRefreshLayout;
    }

    public ApiRxJavaService getApiService(){
        if(apiRxJavaService == null){
            apiRxJavaService = ApiRxJavaClient.getNewInstance().getService();
        }
        return apiRxJavaService;
    }

    public void startRefresh(){
		Utils.setRefreshing(getSwipeRefreshLayout(), true);
    }

    public void endRefresh(){
		Utils.setRefreshing(getSwipeRefreshLayout(), false);
    }

    public void endError(String errorMessage){
        Utils.setRefreshing(getSwipeRefreshLayout(), false);
        MessageUtils.showErrorMessage(getActivity(), errorMessage);
    }
}
