package com.example.gitnb.module.repos;

import java.util.ArrayList;

import com.example.gitnb.R;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.SearchClient;
import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.search.ReposSearch;
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

public class HotReposFragment extends BaseFragment implements RetrofitNetworkAbs.NetworkListener<ReposSearch>, UpdateLanguageListener{
	private String TAG = "HotReposFragment";
	public static String REPOS = "repos";
    private boolean isAlreadyLoadData = false;
    private RecyclerView recyclerView;
    private ReposListAdapter adapter;
	private boolean isLoadingMore;
    private String language;
    private String key;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_data_fragment, container, false);
        initSwipeRefreshLayout(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recylerView);
        language = "java";
        adapter = new ReposListAdapter(getActivity());
        adapter.setShowSearch(true);
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
	        	key = adapter.getSearchText();
				startRefresh();
			}
		});
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

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
    	page = 1;
    	requestHotRepos();
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
	public void onOK(ReposSearch date) {   	
		if(page == 1){
        	adapter.update((ArrayList<Repository>)date.items);
    	}
    	else{
            isLoadingMore = false;
        	adapter.insertAtBack((ArrayList<Repository>)date.items);
    	}
		endRefresh();
	}

	@Override
	public void onError(String Message) {
		endError();
		MessageUtils.showErrorMessage(getActivity(), Message);
	}
	
    private void requestHotRepos(){
    	String q = "";
		if(key != null && !key.isEmpty())
		{
			q += key;
		}
		if(language != null && !language.isEmpty())
		{
			q += "+language:" + language;
		}
    	//q += "+followers:%3E200";
    	
    	SearchClient.getNewInstance().setNetworkListener(this).repos(q, "stars", "desc", page);
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
