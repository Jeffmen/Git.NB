package com.example.gitnb.module.repos;

import java.util.ArrayList;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Organization;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.ShowCase;
import com.example.gitnb.model.User;
import com.example.gitnb.model.search.ShowCaseSearch;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.module.trending.ShowCaseFragment;
import com.example.gitnb.module.search.HotUserFragment;
import com.example.gitnb.module.user.OrganizationDetailActivity;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReposListActivity  extends BaseSwipeActivity{
	private String TAG = ReposListActivity.class.getName();
	public static final String REPOS_TYPE = "repos_type";
	public static final String REPOS_TYPE_USER_REPOS = "user_repository";
	public static final String REPOS_TYPE_SHOWCASE = "showcase_repository";
	public static final String REPOS_TYPE_ORGS = "orgs_repository";
	public static final String REPOS_TYPE_USER_STARRED = "user_starred_repository";
    private ReposListAdapter adapter;
    private RecyclerView recyclerView;
	private boolean isLoadingMore;
	private Organization orgs;
	private ShowCase showCase;
	private User user;
	private String type;
	private int page = 1;

	private Observer<ArrayList<Repository>> observer = new Observer<ArrayList<Repository>>() {
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
	};
	
	@Override
	protected void setTitle(TextView view) {
        if(user != null && !user.getLogin().isEmpty()){    
        	switch(type){
	        case REPOS_TYPE_USER_REPOS:
	        	view.setText(user.getLogin()+" / Repositories");
	        	break;
			case REPOS_TYPE_USER_STARRED:
				view.setText(user.getLogin()+" / Starred");
				break;
            }
			setUserBackground(user.getAvatar_url());
        }
        else if(showCase != null && !showCase.name.isEmpty()){    
        	switch(type){
	        case REPOS_TYPE_SHOWCASE:
	        	view.setText(showCase.name +" / Repositories");
	        	break;
            }
			setUserBackground(showCase.image_url);
        }
        else if(orgs != null && !orgs.login.isEmpty()){
            switch(type){
		        case REPOS_TYPE_ORGS:
		        	view.setText(orgs.login + " / Repositories");
		        	break;
            }
			setUserBackground(orgs.avatar_url);
        }else{
        	view.setText("NULL");
        }
		view.setSelected(true);
	}
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		type = intent.getStringExtra(REPOS_TYPE);
		page = 1;
        switch(type){
		case REPOS_TYPE_USER_STARRED:
        case REPOS_TYPE_USER_REPOS:
    		user = intent.getParcelableExtra(HotUserFragment.USER);
        	break;
        case REPOS_TYPE_SHOWCASE:
    		showCase = intent.getParcelableExtra(ShowCaseFragment.SHOWCASE);
        	break;
        case REPOS_TYPE_ORGS:
    		orgs = intent.getParcelableExtra(OrganizationDetailActivity.ORGS);
        	break;
        }
		this.setContentView(R.layout.activity_list_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
		 recyclerView.setLayoutManager(new LinearLayoutManager(this));
	}

	@Override
	public void onEnterAnimationComplete() {
		super.onEnterAnimationComplete();
	}

    @Override
    protected void startRefresh(){
    	super.startRefresh();
        switch(type){
	        case REPOS_TYPE_USER_REPOS:
	        	userReposList();
	        	break;
	        case REPOS_TYPE_SHOWCASE:
	        	showCaseReposList();
	        	break;
	        case REPOS_TYPE_ORGS:
	        	orgsReposList();
	        	break;
			case REPOS_TYPE_USER_STARRED:
				userStarredReposList();
				break;
        }
    }

    @Override
    protected void endRefresh(){
    	super.endRefresh();
		isLoadingMore = false;
    }

    @Override
    protected void endError(String Message){
    	super.endError(Message);
        isLoadingMore = false;
    }

	private void updateAdapter(ArrayList<Repository> ts){
        if(adapter == null) {
			adapter = new ReposListAdapter(this);
			adapter.update(ts);
			adapter.setOnItemClickListener(new ReposListAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(View view, int position) {
					Intent intent = new Intent(ReposListActivity.this, ReposDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(HotReposFragment.REPOS, adapter.getItem(position));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			adapter.setOnLoadMoreClickListener(new ReposListAdapter.OnItemClickListener() {

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
			ScaleInAnimationAdapter scaleInAdapter = new ScaleInAnimationAdapter(adapter);
			SlideInBottomAnimationAdapter slideInAdapter = new SlideInBottomAnimationAdapter(scaleInAdapter);
			slideInAdapter.setDuration(300);
			slideInAdapter.setInterpolator(new OvershootInterpolator());
			recyclerView.setAdapter(slideInAdapter);
			recyclerView.scheduleLayoutAnimation();
		}
		else{
			if(page == 1){
				adapter.update(ts);
			}
			else{
				isLoadingMore = false;
				adapter.insertAtBack(ts);
			}
		}
	}

	private void onOK(ArrayList<Repository> ts) {
		if(ts.size() == 0){
			recyclerView.setVisibility(View.GONE);
			findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
		}
		else {
			updateAdapter(ts);
		}
		endRefresh();
	}
	
	private void userReposList(){
        getApiService().userReposList(user.getLogin(), page, "updated")
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
	}

	private void userStarredReposList(){
		getApiService().userStarredReposList(user.getLogin(), page, "")
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
	}

	private void orgsReposList(){
        getApiService().reposByOrgs(orgs.login, "updated", page)
                .subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
	}

	private void showCaseReposList(){
        getApiService().trendingShowCase(showCase.slug)
                .subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<ShowCaseSearch>() {
					@Override
					public void onNext(ShowCaseSearch result) {
						if(result != null) {
							onOK((ArrayList<Repository>) result.repositories);
						}
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
}
