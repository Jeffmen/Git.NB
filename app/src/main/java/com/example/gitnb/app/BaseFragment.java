package com.example.gitnb.app;

import com.example.gitnb.R;
import com.example.gitnb.utils.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

public class BaseFragment extends Fragment{
    private SwipeRefreshLayout mSwipeRefreshLayout;
	protected int page = 1;

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

    public SwipeRefreshLayout getSwiperRefreshLayout(){
        return mSwipeRefreshLayout;
    }

    protected void startRefresh(){
		Utils.setRefreshing(getSwiperRefreshLayout(), true);
    }
    
    protected void endRefresh(){
		Utils.setRefreshing(getSwiperRefreshLayout(), false);
    }
    
    protected void endError(){
		Utils.setRefreshing(getSwiperRefreshLayout(), false);
    }
}
