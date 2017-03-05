package com.example.gitnb.module.repos;

import java.util.ArrayList;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Event;
import com.example.gitnb.model.Organization;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.User;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.module.search.HotUserFragment;
import com.example.gitnb.module.user.OrganizationDetailActivity;
import com.example.gitnb.module.viewholder.HorizontalDividerItemDecoration;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EventListActivity  extends BaseSwipeActivity {
	private String TAG = "EventListActivity";
	public static final String EVENT_TYPE = "event_type";
	public static final String EVENT_TYPE_REPOS = "Events_REPOS";
	public static final String EVENT_TYPE_USER = "Events_USER";
	public static final String EVENT_TYPE_ORGS = "Orgs_USER";
    private EventListAdapter adapter;
    private RecyclerView recyclerView;
	private boolean isLoadingMore;
	private Organization orgs;
	private Repository repos;
	private User user;
	private String type;
	private int page = 1;


	private Observer<ArrayList<Event>> observer = new Observer<ArrayList<Event>>() {
		@Override
		public void onNext(ArrayList<Event> result) {
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
	        case EVENT_TYPE_REPOS:
	        	view.setText(repos.getName()+" / Events");    
	        	break;
        	}
			setUserBackground(repos.getOwner().getAvatar_url());
        }else if(user != null && !user.getLogin().isEmpty()){    
        	switch(type){
	        case EVENT_TYPE_USER:
	        	view.setText(user.getLogin()+" / Events");    
	        	break;
        	}
			setUserBackground(user.getAvatar_url());
        }
        else if(orgs != null && !orgs.login.isEmpty()){
            switch(type){
		        case EVENT_TYPE_ORGS:
		        	view.setText(orgs.login + " / Events");
		        	break;
            }
			setUserBackground(orgs.avatar_url);
        }else{
        	view.setText("NULL");
        }
	}
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
        page = 1;
		repos = (Repository) intent.getParcelableExtra(HotReposFragment.REPOS);
		type = intent.getStringExtra(EVENT_TYPE);
        switch(type){
	        case EVENT_TYPE_REPOS:
	    		repos = (Repository) intent.getParcelableExtra(HotReposFragment.REPOS);
	        	break;
	        case EVENT_TYPE_USER:
	    		user = (User) intent.getParcelableExtra(HotUserFragment.USER);
	        	break;
	        case EVENT_TYPE_ORGS:
	    		orgs = (Organization) intent.getParcelableExtra(OrganizationDetailActivity.ORGS);
	        	break;
	    }
		this.setContentView(R.layout.activity_list_layout);
        
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
	}
	 
    @Override
    protected void startRefresh(){
    	super.startRefresh();
        switch(type){
	        case EVENT_TYPE_REPOS:
	        	getReposEvents();
	        	break;
	        case EVENT_TYPE_USER:
	        	getUserEvents();
	        	break;
	        case EVENT_TYPE_ORGS:
	        	getOrgsEvents();
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

	private void updateAdapter(ArrayList<Event> ts){
		if(adapter == null) {
			adapter = new EventListAdapter(this);
			adapter.update(ts);
			adapter.setOnItemClickListener(new EventListAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(View view, int position) {
					Intent intent = new Intent(EventListActivity.this, ReposDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(HotReposFragment.REPOS, adapter.getItem(position).repo);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			adapter.setOnLoadMoreClickListener(new EventListAdapter.OnItemClickListener() {

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
			recyclerView.scheduleLayoutAnimation();
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

	public void onOK(ArrayList<Event> ts) {
		if(ts.size() == 0){
			recyclerView.setVisibility(View.GONE);
			findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
		}
		else {
			updateAdapter(ts);
		}
		endRefresh();
	}
	
	private void getReposEvents(){
        getApiService().events(repos.getOwner().getLogin(), repos.getName(), page)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);

	}

	private void getUserEvents(){
        getApiService().createdEvents(user.getLogin(), page)
                .subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
	}
	
	private void getOrgsEvents(){
		getApiService().eventsByOrgs(orgs.login, page)
                .subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observer);
	}
}
