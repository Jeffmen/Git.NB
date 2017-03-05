package com.example.gitnb.module.issue;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.model.Issue;
import com.example.gitnb.model.User;
import com.example.gitnb.module.MainFragment;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.module.repos.ReposDetailActivity;
import com.example.gitnb.utils.CurrentUser;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssuesFragment extends BaseFragment implements MainFragment.TabClickListener {
	private String TAG = IssuesFragment.class.getName();
	public static String USER = "user_key";
	private boolean isAlreadyLoadData = false;
    private RecyclerView recyclerView;
    private IssueListAdapter adapter;
	private boolean isLoadingMore;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_data, container, false);
        initSwipeRefreshLayout(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        adapter = new IssueListAdapter(getActivity());
        adapter.setOnItemClickListener(new IssueListAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				Intent intent = new Intent(getActivity(), ReposDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotReposFragment.REPOS, adapter.getItem(position).repository);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
        adapter.setOnLoadMoreClickListener(new IssueListAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				if (isLoadingMore) {
					Log.d(TAG, "ignore manually update!");
				} else {
					page++;
					isLoadingMore = true;
					startRefresh();
				}
			}
		});        
        //recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		//recyclerView.setItemAnimator(new SlideInLeftAnimator());
		ScaleInAnimationAdapter scaleInAdapter = new ScaleInAnimationAdapter(adapter);
		SlideInBottomAnimationAdapter slideInAdapter = new SlideInBottomAnimationAdapter(scaleInAdapter);
		slideInAdapter.setDuration(300);
		slideInAdapter.setInterpolator(new OvershootInterpolator());
        recyclerView.setAdapter(slideInAdapter);
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
        getIssues();
    }

	public void onOK(ArrayList<Issue> list) {
		if(page == 1){
        	adapter.update(list);
    	}
    	else{
            isLoadingMore = false;
        	adapter.insertAtBack(list);
    	}
		endRefresh();
	}

	public void getIssues(){
		HashMap<String, String> filter =  new HashMap<>();
		filter.put("state", "all");
        getApiService().allIssues(filter, page)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<ArrayList<Issue>>() {
					@Override
					public void onNext(ArrayList<Issue> result) {
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
