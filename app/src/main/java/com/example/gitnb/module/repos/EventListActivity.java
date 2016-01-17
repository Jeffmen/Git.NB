package com.example.gitnb.module.repos;

import java.util.ArrayList;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.example.gitnb.R;
import src.com.example.gitnb.api.RepoClient;
import src.com.example.gitnb.api.RetrofitNetworkAbs;
import src.com.example.gitnb.api.UsersClient;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Event;
import com.example.gitnb.model.Organization;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.User;
import com.example.gitnb.module.user.HotUserFragment;
import com.example.gitnb.module.user.OrganizationDetailActivity;
import com.example.gitnb.module.viewholder.HorizontalDividerItemDecoration;
import com.example.gitnb.utils.MessageUtils;

public class EventListActivity  extends BaseSwipeActivity implements RetrofitNetworkAbs.NetworkListener<ArrayList<Event>>{
	private String TAG = "ReposEventsActivity";
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

	
	@Override
	protected void setTitle(TextView view) {
        if(repos != null && !repos.getName().isEmpty()){    
        	switch(type){
	        case EVENT_TYPE_REPOS:
	        	view.setText(repos.getName()+" / Events");    
	        	break;
        	}
        }else if(user != null && !user.getLogin().isEmpty()){    
        	switch(type){
	        case EVENT_TYPE_USER:
	        	view.setText(user.getLogin()+" / Events");    
	        	break;
        	}
        }
        else if(orgs != null && !orgs.login.isEmpty()){
            switch(type){
		        case EVENT_TYPE_ORGS:
		        	view.setText(orgs.login + " / Events");
		        	break;
            }
        }else{
        	view.setText("NULL");
        }
	}
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
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
		
        adapter = new EventListAdapter(this);
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
	                getRefreshdler().sendEmptyMessage(START_UPDATE);
	            }
			}
		}); 
        
        recyclerView = (RecyclerView) findViewById(R.id.recylerView);  
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
	}
	 
    @Override
    protected void startRefresh(){
    	super.startRefresh();
    	page = 1;
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
    protected void endError(){
    	super.endError();
        isLoadingMore = false;
    }
    
	@Override
	public void onOK(ArrayList<Event> ts) {   	
		if(page == 1){
        	adapter.update(ts);
    	}
    	else{
            isLoadingMore = false;
        	adapter.insertAtBack(ts);
    	}
		getRefreshdler().sendEmptyMessage(END_UPDATE);
	}

	@Override
	public void onError(String Message) {
		MessageUtils.showErrorMessage(EventListActivity.this, Message);
		getRefreshdler().sendEmptyMessage(END_ERROR);
	}
	
	private void getReposEvents(){
		RepoClient.getNewInstance().setNetworkListener(this)
		  .events(repos.getOwner().getLogin(), repos.getName(), page);
	}
	
	private void getUserEvents(){
		UsersClient.getNewInstance().setNetworkListener(this)
		  .createdEvents(user.getLogin(), page);
	}
	
	private void getOrgsEvents(){
		UsersClient.getNewInstance().setNetworkListener(this)
		  .eventsByOrgs(orgs.login, page);
	}
}
