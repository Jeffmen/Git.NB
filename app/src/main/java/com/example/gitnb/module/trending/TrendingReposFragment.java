package com.example.gitnb.module.trending;

import java.util.ArrayList;

import com.example.gitnb.R;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.TrendingClient;
import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.model.Repository;
import com.example.gitnb.module.MainActivity.UpdateLanguageListener;
import com.example.gitnb.module.repos.HotReposFragment;
import com.example.gitnb.module.repos.ReposDetailActivity;
import com.example.gitnb.module.viewholder.HorizontalDividerItemDecoration;
import com.example.gitnb.utils.MessageUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

public class TrendingReposFragment extends BaseFragment implements RetrofitNetworkAbs.NetworkListener<ArrayList<Repository>>, UpdateLanguageListener{
	private String TAG = "TrendingReposFragment";
    private boolean isAlreadyLoadData = false;
    private LinearLayoutManager mLayoutManager;
    private TrendingReposAdapter adapter;
    private RecyclerView recyclerView;
    private String language;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_data_fragment, container, false);
        initSwipeRefreshLayout(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        adapter = new TrendingReposAdapter(getActivity());
        //recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        adapter.SetOnItemClickListener(new TrendingReposAdapter.OnItemClickListener() {
			
			@Override
			public void onItemClick(View view, int position) {
				Intent intent = new Intent(getActivity(), ReposDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotReposFragment.REPOS, adapter.getItem(position));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
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
    protected void startRefresh(){
		super.startRefresh();
		requestTrendingRepos();
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
	public void onOK(ArrayList<Repository> list) {  
    	adapter.update(list);
    	endRefresh();
	}

	@Override
	public void onError(String Message) {
		endError();
		MessageUtils.showErrorMessage(getActivity(), Message);
	}
	
    private void requestTrendingRepos(){
    	TrendingClient.getNewInstance().setNetworkListener(this).trendingReposList(language, "daily");
    }

	@Override
	public Void updateLanguage(String language) {
		this.language = language;
    	startRefresh();
		return null;
	}

	@Override
	public Void moveToUp() {
		recyclerView.smoothScrollToPosition(0);
		return null;
	}
}
