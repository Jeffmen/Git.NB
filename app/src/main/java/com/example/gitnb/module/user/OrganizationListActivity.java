package com.example.gitnb.module.user;

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
import com.example.gitnb.model.Organization;
import com.example.gitnb.model.User;
import com.example.gitnb.module.search.HotUserFragment;
import com.example.gitnb.module.viewholder.HorizontalDividerItemDecoration;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OrganizationListActivity  extends BaseSwipeActivity{
	private String TAG = OrganizationListActivity.class.getName();
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
			setUserBackground(user.getAvatar_url());
        }else{
        	view.setText("NULL");
        }
	}
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		page = 1;
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
	                Log.d(TAG, "ignore manually update!");
	            } else{
	             	page++;
	                isLoadingMore = true;
					startRefresh();
	            }
			}
		}); 
        
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
		ScaleInAnimationAdapter scaleInAdapter = new ScaleInAnimationAdapter(adapter);
		SlideInBottomAnimationAdapter slideInAdapter = new SlideInBottomAnimationAdapter(scaleInAdapter);
		slideInAdapter.setDuration(300);
		slideInAdapter.setInterpolator(new OvershootInterpolator());
		recyclerView.setAdapter(slideInAdapter);
	}
	 
    @Override
    protected void startRefresh(){
    	super.startRefresh();
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
    protected void endError(String errorMessage){
    	super.endError(errorMessage);
        isLoadingMore = false;
    }

	public void onOK(ArrayList<Organization> ts) {
		if(page == 1){
			if(ts.size() == 0){
				recyclerView.setVisibility(View.GONE);
				findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
			}
			else {
				adapter.update(ts);
			}
    	}
    	else{
            isLoadingMore = false;
        	adapter.insertAtBack(ts);
    	}
		endRefresh();
	}

	private void getOrganizations(){
		getApiService().orgsByUser(user.getLogin(), page)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<ArrayList<Organization>>() {
					@Override
					public void onNext(ArrayList<Organization> result) {
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
}
