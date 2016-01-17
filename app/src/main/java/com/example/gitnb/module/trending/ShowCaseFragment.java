package com.example.gitnb.module.trending;

import java.util.ArrayList;

import com.example.gitnb.R;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.TrendingClient;
import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.model.ShowCase;
import com.example.gitnb.module.MainActivity.UpdateLanguageListener;
import com.example.gitnb.module.repos.ReposListActivity;
import com.example.gitnb.module.viewholder.HorizontalDividerItemDecoration;
import com.example.gitnb.utils.MessageUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShowCaseFragment extends BaseFragment implements RetrofitNetworkAbs.NetworkListener<ArrayList<ShowCase>>, UpdateLanguageListener{
	private String TAG = "TrendingReposFragment";
    private boolean isAlreadyLoadData = false;
	public static String SHOWCASE = "showcase_key";
    private LinearLayoutManager mLayoutManager;
    private ShowCaseAdapter adapter;
    private RecyclerView recyclerView;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_data_fragment, container, false);
        initSwipeRefreshLayout(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recylerView);
        adapter = new ShowCaseAdapter(getActivity());
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        adapter.SetOnItemClickListener(new ShowCaseAdapter.OnItemClickListener() {
			
			@Override
			public void onItemClick(View view, int position) {

				Intent intent = new Intent(getActivity(), ReposListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(SHOWCASE, adapter.getItem(position));
				intent.putExtras(bundle);
				intent.putExtra(ReposListActivity.REPOS_TYPE, ReposListActivity.REPOS_TYPE_SHOWCASE);
				startActivity(intent);
			}
		});
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
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
        requeseShowCase();
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
	public void onOK(ArrayList<ShowCase> list) {  
    	adapter.update(list);
    	endRefresh();
	}

	@Override
	public void onError(String Message) {
		endError();
		MessageUtils.showErrorMessage(getActivity(), Message);
	}
	
    private void requeseShowCase(){
    	TrendingClient.getNewInstance().setNetworkListener(this).trendingShowCase();
    }

	@Override
	public Void updateLanguage(String language) {
		return null;
	}
}
