package com.example.gitnb.module.trending;

import java.util.ArrayList;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.ShowCase;
import com.example.gitnb.module.MainFragment;
import com.example.gitnb.module.repos.ReposDetailActivity;
import com.example.gitnb.module.repos.ReposListActivity;
import com.example.gitnb.module.search.HotReposFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShowCaseFragment extends BaseFragment implements MainFragment.TabClickListener{
	private String TAG = "TrendingReposFragment";
    private boolean isAlreadyLoadData = false;
	public static String SHOWCASE = "showcase_key";
    private LinearLayoutManager mLayoutManager;
    private ShowCaseAdapter adapter;
    private RecyclerView recyclerView;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_data, container, false);
        initSwipeRefreshLayout(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        return view;
    }
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && !isAlreadyLoadData) {
			isAlreadyLoadData = true;
			startRefresh();
		} else {
	
		}
	}
	
	@Override
	public void startRefresh(){
		super.startRefresh();
		requestShowCase();
    }
	private void updateAdapter(ArrayList<ShowCase> ts){
		if(adapter == null) {
			adapter = new ShowCaseAdapter(getActivity());
			adapter.SetOnItemClickListener(new ShowCaseAdapter.OnItemClickListener() {

				@Override
				public void onItemClick(View view, int position) {
					Intent intent = new Intent(getActivity(), ReposListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(SHOWCASE, adapter.getItem(position));
					intent.putExtras(bundle);
					intent.putExtra(ReposListActivity.REPOS_TYPE, ReposListActivity.REPOS_TYPE_SHOWCASE);
					startActivity(intent);
				}
			});
			ScaleInAnimationAdapter scaleInAdapter = new ScaleInAnimationAdapter(adapter);
			SlideInBottomAnimationAdapter slideInAdapter = new SlideInBottomAnimationAdapter(scaleInAdapter);
			slideInAdapter.setDuration(300);
			slideInAdapter.setInterpolator(new OvershootInterpolator());
			recyclerView.setAdapter(slideInAdapter);
			recyclerView.scheduleLayoutAnimation();
		}
		adapter.update(ts);
	}

	public void onOK(ArrayList<ShowCase> list) {
		updateAdapter(list);
		endRefresh();
	}

    private void requestShowCase(){
        getApiService().trendingShowCase()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<ArrayList<ShowCase>>() {
					@Override
					public void onNext(ArrayList<ShowCase> result) {
						onOK(result);
					}

					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable error) {
						endError(error.getMessage());
					}
				});
    }

	@Override
	public Void moveToUp() {
		recyclerView.smoothScrollToPosition(0);
		return null;
	}
}
