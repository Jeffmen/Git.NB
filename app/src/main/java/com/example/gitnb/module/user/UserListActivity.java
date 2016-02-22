package com.example.gitnb.module.user;

import java.util.ArrayList;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.example.gitnb.R;
import com.example.gitnb.api.RepoClient;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.UsersClient;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Organization;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.User;
import com.example.gitnb.module.repos.HotReposFragment;
import com.example.gitnb.module.viewholder.HorizontalDividerItemDecoration;
import com.example.gitnb.utils.MessageUtils;

public class UserListActivity  extends BaseSwipeActivity implements RetrofitNetworkAbs.NetworkListener<ArrayList<User>>{
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
        }
        else if(orgs != null && !orgs.login.isEmpty()){
            switch(type){
		        case USER_TYPE_MEMBER:
		        	view.setText(orgs.login + " / " + USER_TYPE_MEMBER);
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
		type = intent.getStringExtra(USER_TYPE);
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
		
        adapter = new UserListAdapter(this);
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
	                getRefreshdler().sendEmptyMessage(START_UPDATE);
	            }
			}
		}); 
        
        recyclerView = (RecyclerView) findViewById(R.id.recylerView);  
        //recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
	}
	 
    @Override
    protected void startRefresh(){
    	super.startRefresh();
    	page = 1;
        switch(type){
	        case USER_TYPE_STARGZER:
	        	getStargzers();
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
    protected void endError(){
    	super.endError();
        isLoadingMore = false;
    }
    
	@Override
	public void onOK(ArrayList<User> ts) {   	
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
		MessageUtils.showErrorMessage(UserListActivity.this, Message);
		getRefreshdler().sendEmptyMessage(END_ERROR);
	}
	
	private void getContributors(){
		RepoClient.getNewInstance().setNetworkListener(this)
		  .contributors(repos.getOwner().getLogin(), repos.getName(), page);
	}
	
	private void getStargzers(){
		RepoClient.getNewInstance().setNetworkListener(this)
		  .stargazers(repos.getOwner().getLogin(), repos.getName(), page);
	}

	private void getFollowers(){
		UsersClient.getNewInstance().setNetworkListener(this)
		  .followers(user.getLogin(), page);
	}
	
	private void getFollowing(){
		UsersClient.getNewInstance().setNetworkListener(this)
		  .following(user.getLogin(), page);
	}
	
	private void getMembers(){
		UsersClient.getNewInstance().setNetworkListener(this)
		  .members(orgs.login, page);
	}
}
