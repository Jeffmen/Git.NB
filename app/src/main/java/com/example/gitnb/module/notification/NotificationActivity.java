package com.example.gitnb.module.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Notification;
import com.example.gitnb.module.issue.IssueDetailActivity;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.utils.CurrentUser;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import retrofit.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NotificationActivity extends BaseSwipeActivity{
	private String TAG = NotificationActivity.class.getName();
	private static int FOR_NOTIFICATION = 300;
	public static String USER = "user_key";
	private RecyclerView recyclerView;
	private NotificationListAdapter adapter;
	private boolean isLoadingMore;
	private int toRemovePosition = -1;
	private int page = 1;
	
	@Override
	protected void setTitle(TextView view) {
		setUserBackground(CurrentUser.getInstance().getMe().getAvatar_url());
		view.setText("Notification");
		view.setSelected(true);
	}
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_list_layout);

		 recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		 adapter = new NotificationListAdapter(this);
		 adapter.setOnItemClickListener(new NotificationListAdapter.OnItemClickListener() {

			 @Override
			 public void onItemClick(View view, final int position) {
				 getApiService().markAsRead(String.valueOf(adapter.getItem(position).id))
						 .subscribeOn(Schedulers.io())
						 //.observeOn(AndroidSchedulers.mainThread())
						 .subscribe(new Observer<Response<Boolean>>() {
							 @Override
							 public void onNext(Response<Boolean> result) {
								 if(result.code() == 205){
									 toRemovePosition = -1;
								 }
							 }

							 @Override
							 public void onCompleted() {
							 }

							 @Override
							 public void onError(Throwable error) {
								 endError(error.getMessage());
							 }
						 });
				 Intent intent = new Intent(NotificationActivity.this, IssueDetailActivity.class);
				 Bundle bundle = new Bundle();
				 bundle.putString(IssueDetailActivity.ISSUE_URL, adapter.getItem(position).subject.url);
				 bundle.putParcelable(HotReposFragment.REPOS, adapter.getItem(position).repository);
				 intent.putExtras(bundle);
				 startActivityForResult(intent, FOR_NOTIFICATION);
			 }
		 });
		 adapter.setOnLoadMoreClickListener(new NotificationListAdapter.OnItemClickListener() {

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
		 //recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
		 recyclerView.setLayoutManager(new LinearLayoutManager(this));
		 //recyclerView.setItemAnimator(new SlideInLeftAnimator());
		 ScaleInAnimationAdapter scaleInAdapter = new ScaleInAnimationAdapter(adapter);
		 SlideInBottomAnimationAdapter slideInAdapter = new SlideInBottomAnimationAdapter(scaleInAdapter);
		 slideInAdapter.setDuration(300);
		 slideInAdapter.setInterpolator(new OvershootInterpolator());
		 recyclerView.setAdapter(slideInAdapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FOR_NOTIFICATION){
			startRefresh();
			if(toRemovePosition >= 0){
				adapter.remove(toRemovePosition);
				toRemovePosition = -1;
			}
		}
	}

	@Override
	protected void startRefresh(){
		super.startRefresh();
		getNotifications();
	}

	public void onOK(ArrayList<Notification> list) {
		if(list.size() == 0){
			recyclerView.setVisibility(View.GONE);
			findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
		}
		else {
			if(page == 1){
				adapter.update(list);
			}
			else{
				isLoadingMore = false;
				adapter.insertAtBack(list);
			}
		}
		endRefresh();
	}

	public void getNotifications(){
		getApiService().getNotifications()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<ArrayList<Notification>>() {
					@Override
					public void onNext(ArrayList<Notification> result) {
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
