package com.example.gitnb.module.user;

import java.util.ArrayList;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AccelerateInterpolator;
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
import com.example.gitnb.model.User;
import com.example.gitnb.module.repos.ReposDetailActivity;
import com.example.gitnb.module.repos.ReposListActivity;
import com.example.gitnb.module.repos.ReposListAdapter;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.module.search.HotUserFragment;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserListActivity  extends BaseSwipeActivity{
	private String TAG = "UserListActivity";
	public static final String USER_TYPE = "user_type";
	public static final String USER_TYPE_STARGZER = "Stargzer";
	public static final String USER_TYPE_CONTRIBUTOR = "Contributor";
	public static final String USER_TYPE_FOLLOWER = "Follower";
	public static final String USER_TYPE_FOLLOWING = "Following";
	public static final String USER_TYPE_MEMBER = "Member";
    private UserListAdapter adapter;
    private RecyclerView recyclerView;
	private boolean isLoadingMore;
	private Organization orgs;
	private Repository repos;
	private User user;
	private String type;
	private int page = 1;


	private Observer<ArrayList<User>> observer = new Observer<ArrayList<User>>() {
		@Override
		public void onNext(ArrayList<User> result) {
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
        if(repos != null && !repos.getName().isEmpty()){
            switch(type){
		        case USER_TYPE_STARGZER:
		        	view.setText(repos.getName() + " / " + USER_TYPE_STARGZER);
		        	break;
		        case USER_TYPE_CONTRIBUTOR:
		        	view.setText(repos.getName() + " / " + USER_TYPE_CONTRIBUTOR);
		        	break;
            }
			setUserBackground(repos.getOwner().getAvatar_url());
        }
        else if(user != null && !user.getLogin().isEmpty()){
            switch(type){
		        case USER_TYPE_FOLLOWER:
		        	view.setText(user.getLogin() + " / " + USER_TYPE_FOLLOWER);
		        	break;
		        case USER_TYPE_FOLLOWING:
		        	view.setText(user.getLogin() + " / " + USER_TYPE_FOLLOWING);
		        	break;
            }
			setUserBackground(user.getAvatar_url());
        }
        else if(orgs != null && !orgs.login.isEmpty()){
            switch(type){
		        case USER_TYPE_MEMBER:
		        	view.setText(orgs.login + " / " + USER_TYPE_MEMBER);
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
		type = intent.getStringExtra(USER_TYPE);
		page = 1;
        switch(type){
	        case USER_TYPE_STARGZER:
	        case USER_TYPE_CONTRIBUTOR:
	    		repos = (Repository) intent.getParcelableExtra(HotReposFragment.REPOS);
	        	break;
	        case USER_TYPE_FOLLOWER:
	        case USER_TYPE_FOLLOWING:
	    		user = (User) intent.getParcelableExtra(HotUserFragment.USER);
	        	break;
	        case USER_TYPE_MEMBER:
	    		orgs = (Organization) intent.getParcelableExtra(OrganizationDetailActivity.ORGS);
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
	        case USER_TYPE_STARGZER:
	        	getStargazers();
	        	break;
	        case USER_TYPE_CONTRIBUTOR:
	            getContributors();
	        	break;
	        case USER_TYPE_FOLLOWER:
	        	getFollowers();
	        	break;
	        case USER_TYPE_FOLLOWING:
	        	getFollowing();
	        	break;
	        case USER_TYPE_MEMBER:
	        	getMembers();
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
		if(Message.equals("Not Found")){
			recyclerView.setVisibility(View.GONE);
			findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
		}
    }

	private void updateAdapter(ArrayList<User> ts){
		if(adapter == null) {
			adapter = new UserListAdapter(this);
			adapter.update(ts);
			adapter.setOnItemClickListener(new UserListAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(View view, int position) {
					Intent intent = new Intent(UserListActivity.this, UserDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(HotUserFragment.USER, adapter.getItem(position));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			adapter.setOnLoadMoreClickListener(new UserListAdapter.OnItemClickListener() {

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

			ScaleInAnimationAdapter scaleInAdapter = new ScaleInAnimationAdapter(adapter);
			SlideInBottomAnimationAdapter slideInAdapter = new SlideInBottomAnimationAdapter(scaleInAdapter);
			slideInAdapter.setDuration(300);
			slideInAdapter.setInterpolator(new OvershootInterpolator());
			recyclerView.setAdapter(slideInAdapter);

			LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);
			layoutAnimationController.setInterpolator(new AccelerateInterpolator());
			layoutAnimationController.setDelay(0.5f);
			layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
			recyclerView.setLayoutAnimation(layoutAnimationController);
			recyclerView.scheduleLayoutAnimation();
			recyclerView.startLayoutAnimation();
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

	private void onOK(ArrayList<User> ts) {
		if(ts.size() == 0){
			recyclerView.setVisibility(View.GONE);
			findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
		}
		else {
			updateAdapter(ts);
		}
		endRefresh();
	}
	
	private void getContributors(){
		getApiService().contributors(repos.getOwner().getLogin(), repos.getName(), page)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
	}
	
	private void getStargazers(){
		getApiService().stargazers(repos.getOwner().getLogin(), repos.getName(), page)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
	}

	private void getFollowers(){
		getApiService().followers(user.getLogin(), page)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
	}
	
	private void getFollowing(){
		getApiService().following(user.getLogin(), page)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
	}
	
	private void getMembers(){
		getApiService().members(orgs.login, page)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
	}
}
