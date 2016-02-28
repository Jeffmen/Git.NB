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
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.UsersClient;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Organization;
import com.example.gitnb.model.User;
import com.example.gitnb.module.viewholder.HorizontalDividerItemDecoration;
import com.example.gitnb.utils.MessageUtils;

public class OrganizationListActivity  extends BaseSwipeActivity implements RetrofitNetworkAbs.NetworkListener<ArrayList<Organization>>{
	private String TAG = "OrganizationListActivity";
	public static final String ORGANIZATION_TYPE = "organization_type";
	public static final String ORGANIZATION_TYPE_USER = "user";
    private OrganizationListAdapter adapter;
    private RecyclerView recyclerView;
	private boolean isLoadingMore;
	private User user;
	private String type;
	private int page = 1;

	
	@Override
	protected void setTitle(TextView view) {
        if(user != null && !user.getLogin().isEmpty()){
            switch(type){
		        case ORGANIZATION_TYPE_USER:
		        	view.setText(user.getLogin() + " / Organizations");
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
		type = intent.getStringExtra(ORGANIZATION_TYPE);
        switch(type){
	        case ORGANIZATION_TYPE_USER:
	    		user = (User) intent.getParcelableExtra(HotUserFragment.USER);
	        	break;
	    }
        
		this.setContentView(R.layout.activity_list_layout);
		
        adapter = new OrganizationListAdapter(this);
        adapter.setOnItemClickListener(new OrganizationListAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Intent intent = new Intent(OrganizationListActivity.this, OrganizationDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(OrganizationDetailActivity.ORGS, adapter.getItem(position));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
        adapter.setOnLoadMoreClickListener(new OrganizationListAdapter.OnItemClickListener() {
			
			@Override
			public void onItemClick(View view, int position) {
                if(isLoadingMore){
	                Log.d(TAG,"ignore manually update!");
	            } else{
	             	page++;
	                isLoadingMore = true;
					getRefreshHandler().sendEmptyMessage(START_UPDATE);
	            }
			}
		}); 
        
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
	}
	 
    @Override
    protected void startRefresh(){
    	super.startRefresh();
    	page = 1;
        switch(type){
	        case ORGANIZATION_TYPE_USER:
	        	getOrganizations();
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
	public void onOK(ArrayList<Organization> ts) {   	
		if(page == 1){
        	adapter.update(ts);
    	}
    	else{
            isLoadingMore = false;
        	adapter.insertAtBack(ts);
    	}
		getRefreshHandler().sendEmptyMessage(END_UPDATE);
	}

	@Override
	public void onError(String Message) {
		MessageUtils.showErrorMessage(OrganizationListActivity.this, Message);
		getRefreshHandler().sendEmptyMessage(END_ERROR);
	}

	private void getOrganizations(){
		UsersClient.getNewInstance().setNetworkListener(this)
		  .orgsByUser(user.getLogin(), page);
	}
}
