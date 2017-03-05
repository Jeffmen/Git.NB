package com.example.gitnb.module.trending;

import java.util.ArrayList;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.model.Repository;
import com.example.gitnb.module.MainActivity;
import com.example.gitnb.module.MainFragment;
import com.example.gitnb.module.custom.view.TagListView;
import com.example.gitnb.module.repos.ReposListAdapter;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.module.repos.ReposDetailActivity;
import com.example.gitnb.module.search.SearchActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TrendingReposFragment extends BaseFragment implements MainFragment.TabClickListener,MainFragment.UpdateListener {
	private String TAG = TrendingReposFragment.class.getName();
    private boolean isAlreadyLoadData = false;
    private LinearLayoutManager mLayoutManager;
    private TrendingReposAdapter adapter;
    private RecyclerView recyclerView;
	private String languageText, sinceText;

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
		requestTrendingRepos();
    }

	private void updateAdapter(ArrayList<Repository> ts){
		if(adapter == null) {
			adapter = new TrendingReposAdapter(getActivity());
			adapter.setOnItemClickListener(new TrendingReposAdapter.OnItemClickListener() {

				@Override
				public void onItemClick(View view, int position) {
					Intent intent = new Intent(getActivity(), ReposDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(HotReposFragment.REPOS, adapter.getItem(position));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			adapter.setOnTagChangeListener(new TrendingReposAdapter.TagChangeListener() {
				@Override
				public void tagChange(String language, String since) {
					languageText = language;
					sinceText = since;
					startRefresh();
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

	public void onOK(ArrayList<Repository> list) {
    	updateAdapter(list);
		endRefresh();
	}

    private void requestTrendingRepos(){
        getApiService().trendingReposList(languageText, sinceText)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<ArrayList<Repository>>() {
					@Override
					public void onNext(ArrayList<Repository> result) {
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
	public Void update() {
    	startRefresh();
		return null;
	}

	@Override
	public Void moveToUp() {
		recyclerView.smoothScrollToPosition(0);
		return null;
	}
}
