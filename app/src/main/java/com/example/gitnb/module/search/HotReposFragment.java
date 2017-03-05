package com.example.gitnb.module.search;

import java.util.ArrayList;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.search.ReposSearch;
import com.example.gitnb.module.MainFragment;
import com.example.gitnb.module.repos.ReposDetailActivity;
import com.example.gitnb.module.repos.ReposListActivity;
import com.example.gitnb.module.repos.ReposListAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HotReposFragment extends BaseFragment implements MainFragment.TabClickListener,MainFragment.UpdateListener{
	private String TAG = "HotReposFragment";
	public static String REPOS = "repos";
    private boolean isAlreadyLoadData = false;
    private RecyclerView recyclerView;
    private ReposListAdapter adapter;
	private boolean isLoadingMore;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_data, container, false);
        initSwipeRefreshLayout(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		page = 1;
        //recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        startRefresh();
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
    	requestHotRepos();
    }

	private String getKey(){
		if(adapter != null && adapter.getShowSearch()){
			return adapter.getSearchText();
		}
		else{
			return ((SearchActivity)getActivity()).getSearchText();
		}
	}

	private String getLanguage(){
		return ((SearchActivity)getActivity()).getLanguageText();
	}

	private void updateAdapter(ArrayList<Repository> ts){
		if(adapter == null) {
			adapter = new ReposListAdapter(getActivity());
			adapter.update(ts);
			adapter.setOnItemClickListener(new ReposListAdapter.OnItemClickListener() {

				@Override
				public void onItemClick(View view, int position) {
					Intent intent = new Intent(getActivity(), ReposDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(REPOS, adapter.getItem(position));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			adapter.setOnLoadMoreClickListener(new ReposListAdapter.OnItemClickListener() {

				@Override
				public void onItemClick(View view, int position) {
					if(isLoadingMore){
						Log.d(TAG,"ignore manually update!");
					} else{
						page++;
						isLoadingMore = true;
						startRefresh();
					}
				}
			});
			adapter.setOnSearchClickListener(new ReposListAdapter.OnItemClickListener() {

				@Override
				public void onItemClick(View view, int position) {
					page = 1;
					startRefresh();
				}
			});
			ScaleInAnimationAdapter scaleInAdapter = new ScaleInAnimationAdapter(adapter);
			SlideInBottomAnimationAdapter slideInAdapter = new SlideInBottomAnimationAdapter(scaleInAdapter);
			slideInAdapter.setDuration(300);
			slideInAdapter.setInterpolator(new OvershootInterpolator());
			recyclerView.setAdapter(slideInAdapter);
			recyclerView.scheduleLayoutAnimation();
			recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
				@Override
				public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
					super.onScrollStateChanged(recyclerView, newState);
					if (newState == RecyclerView.SCROLL_STATE_DRAGGING){
						((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}
			});
		}
		else{
			if(page == 1){
				adapter.update(ts);
				recyclerView.scrollToPosition(0);
			}
			else{
				isLoadingMore = false;
				adapter.insertAtBack(ts);
			}
		}
	}

	public void onOK(ReposSearch date) {
		updateAdapter((ArrayList<Repository>)date.items);
		endRefresh();
	}
	
    private void requestHotRepos(){
    	String q = "";
		if(!TextUtils.isEmpty(getKey()))
		{
			q += getKey();
		}
		if(!TextUtils.isEmpty(getLanguage()))
		{
			q += "+language:" + getLanguage();
		}
    	//q += "+followers:%3E200";
		if(TextUtils.isEmpty(q)) {
			endRefresh();
			return;
		}
		//searchClient.repos(q, "stars", "desc", page);
        getApiService().reposPaginated(q, "stars", "desc", page)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<ReposSearch>() {
					@Override
					public void onNext(ReposSearch result) {
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
