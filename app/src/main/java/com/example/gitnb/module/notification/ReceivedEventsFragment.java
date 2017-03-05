package com.example.gitnb.module.notification;

import java.util.ArrayList;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseFragment;
import com.example.gitnb.model.Event;
import com.example.gitnb.model.User;
import com.example.gitnb.module.MainFragment;
import com.example.gitnb.module.repos.EventListAdapter;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.module.repos.ReposDetailActivity;
import com.example.gitnb.module.search.HotUserFragment;
import com.example.gitnb.module.user.UserDetailActivity;
import com.example.gitnb.module.user.UserListActivity;
import com.example.gitnb.module.user.UserListAdapter;
import com.example.gitnb.utils.CurrentUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReceivedEventsFragment extends BaseFragment implements MainFragment.TabClickListener{
	private String TAG = "HotUserFragment";
	public static String USER = "user_key";
    private RecyclerView recyclerView;
    private EventListAdapter adapter;
	private boolean isLoadingMore;
    private User me;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		me = CurrentUser.getInstance().getMe();
        View view = inflater.inflate(R.layout.fragment_list_data, container, false);
        initSwipeRefreshLayout(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        startRefresh();
        return view;
    }

	@Override
	public void startRefresh(){
		super.startRefresh();
    	receivedEvents();
    }

	private void updateAdapter(ArrayList<Event> ts){
		if(adapter == null) {
			adapter = new EventListAdapter(getActivity());
			adapter.update(ts);
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
					if (isLoadingMore) {
						Log.d(TAG, "ignore manually update!");
					} else {
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
			}
			else{
				isLoadingMore = false;
				adapter.insertAtBack(ts);
			}
		}
	}

	public void onOK(ArrayList<Event> list) {
		updateAdapter(list);
		endRefresh();
	}

	public void receivedEvents(){
        getApiService().events(me.getLogin(), page)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<ArrayList<Event>>() {
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
				});
	}

	@Override
	public Void moveToUp() {
		recyclerView.smoothScrollToPosition(0);
		return null;
	}
}
