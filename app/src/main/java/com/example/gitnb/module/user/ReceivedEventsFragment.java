package com.example.gitnb.module.user;

import java.util.ArrayList;

import com.example.gitnb.R;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.UsersClient;
import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.model.Event;
import com.example.gitnb.model.User;
import com.example.gitnb.module.MainActivity.UpdateLanguageListener;
import com.example.gitnb.module.repos.EventListAdapter;
import com.example.gitnb.module.repos.HotReposFragment;
import com.example.gitnb.module.repos.ReposDetailActivity;
import com.example.gitnb.module.viewholder.HorizontalDividerItemDecoration;
import com.example.gitnb.utils.CurrentUser;
import com.example.gitnb.utils.MessageUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReceivedEventsFragment extends BaseFragment implements RetrofitNetworkAbs.NetworkListener<ArrayList<Event>>, UpdateLanguageListener{
	private String TAG = "HotUserFragment";
	public static String USER = "user_key";
    private RecyclerView recyclerView;
    private EventListAdapter adapter;
	private boolean isLoadingMore;
    private User me;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		me = CurrentUser.get(this.getActivity());
        View view = inflater.inflate(R.layout.list_data_fragment, container, false);
        initSwipeRefreshLayout(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recylerView);
        adapter = new EventListAdapter(getActivity());
        adapter.setOnItemClickListener(new EventListAdapter.OnItemClickListener() {
			
			@Override
			public void onItemClick(View view, int position) {
				Intent intent = new Intent(getActivity(), ReposDetailActivity.class);
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
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        startRefresh();
        return view;
    }

	@Override
    protected void startRefresh(){
		super.startRefresh();
    	receivedEvents();
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
	public void onOK(ArrayList<Event> list) {   	
		if(page == 1){
        	adapter.update(list);
    	}
    	else{
            isLoadingMore = false;
        	adapter.insertAtBack(list);
    	}
		endRefresh();
	}

	@Override
	public void onError(String Message) {
		endError();
		MessageUtils.showErrorMessage(getActivity(), Message);
	}
	
	public void receivedEvents(){
		UsersClient.getNewInstance().setNetworkListener(this).events(me.getLogin(), page);
	}

	@Override
	public Void updateLanguage(String language) {
		return null;
	}
}
