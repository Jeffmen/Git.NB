package com.example.gitnb.module.user;

import java.util.ArrayList;

import com.example.gitnb.R;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.SearchClient;
import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.model.User;
import com.example.gitnb.model.search.UsersSearch;
import com.example.gitnb.module.MainActivity.UpdateLanguageListener;
import com.example.gitnb.module.viewholder.HorizontalDividerItemDecoration;
import com.example.gitnb.utils.MessageUtils;

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

public class HotUserFragment extends BaseFragment implements RetrofitNetworkAbs.NetworkListener<UsersSearch>, UpdateLanguageListener{
	private String TAG = "HotUserFragment";
	public static String USER = "user_key";
    private boolean isAlreadyLoadData = false;
    private RecyclerView recyclerView;
    private UserListAdapter adapter;
	private boolean isLoadingMore;
    private String language = "java";
    private String location;
    private String key;
	private int page;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_data_fragment, container, false);
        initSwipeRefreshLayout(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recylerView);
        page = 1;
        language = "java";
        adapter = new UserListAdapter(getActivity());
        adapter.setShowSearch(true);
        adapter.setOnItemClickListener(new UserListAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				Intent intent = new Intent(getActivity(), UserDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(USER, adapter.getItem(position));
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
        adapter.setOnSearchClickListener(new UserListAdapter.OnItemClickListener() {
			
			@Override
			public void onItemClick(View view, int position) {
	        	page = 1;
	        	key = adapter.getSearchText();
	        	startRefresh();
			}
		});
        //recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		ScaleInAnimationAdapter scaleInAdapter = new ScaleInAnimationAdapter(adapter);
		SlideInBottomAnimationAdapter slideInAdapter = new SlideInBottomAnimationAdapter(scaleInAdapter);
		slideInAdapter.setDuration(300);
		slideInAdapter.setInterpolator(new OvershootInterpolator());
		recyclerView.setAdapter(slideInAdapter);
        /*
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                int totalItemCount = adapter.getItemCount();

                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if(isLoadingMore){
                        Log.d(TAG,"ignore manually update!");
                    } else{
	                   	page++;
	                   	requestHotUser(true);
                        isLoadingMore = true;
                    }
                }
            }
        });*/

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
    protected void startRefresh(){
		super.startRefresh();
    	requestHotUser();
    }

	@Override
    protected void endRefresh(){
    	super.endRefresh();
    }

	@Override
    protected void endError(){
    	super.endError();
    }
	
	@Override
	public void onOK(UsersSearch data) {   	
		if(page == 1){
        	adapter.update((ArrayList<User>)data.items);
			recyclerView.scrollToPosition(0);
    	}
    	else{
            isLoadingMore = false;
        	adapter.insertAtBack((ArrayList<User>)data.items);
    	}
		endRefresh();
	}

	@Override
	public void onError(String Message) {
		endError();
		MessageUtils.showErrorMessage(getActivity(), Message);
	}
    
    private void requestHotUser(){
    	String q = "";
		if(key != null && !key.isEmpty())
		{
			q += key;
		}
		if(location != null && !location.isEmpty())
		{
			q += "+location:" + location;
		}
		if(language != null && !language.isEmpty())
		{
			q += "+language:" + language;
		}
    	//q += "+followers:%3E200";
    	
    	SearchClient.getNewInstance().setNetworkListener(this).users(q, "followers", "desc", page);
    }

	@Override
	public Void updateLanguage(String language) {
		if(language != null && !language.isEmpty()){
			this.language = language;
	    	startRefresh();
		}
		return null;
	}
}
