package com.example.gitnb.module.repos;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Content;
import com.example.gitnb.model.Repository;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.module.viewholder.HorizontalDividerItemDecoration;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReposContentsListActivity extends BaseSwipeActivity {
	public static String CONTENT = "content";
    private ReposContentsAdapter adapter;
	private RecyclerView pathRecyclerView;
	private ReposPathAdapter pathAdapter;
	private RecyclerView recyclerView;
	private Repository repos;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        repos = intent.getParcelableExtra(HotReposFragment.REPOS);
        setContentView(R.layout.activity_repos_content_list);
        setUserBackground(repos.getOwner().getAvatar_url());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new ReposContentsAdapter(this);
        adapter.SetOnItemClickListener(new ReposContentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Content content = adapter.getItem(position);
                if (content.isDir()) {
                    pathAdapter.insertAtBack(content.name);
                    pathRecyclerView.smoothScrollToPosition(pathAdapter.getItemCount()-1);
                    startRefresh();
                }
                if (content.isFile()) {
                    showContent(content);
                }
            }
        });
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).showLastDivider().build());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

		pathRecyclerView = (RecyclerView) findViewById(R.id.pathRecyclerView);
		pathAdapter = new ReposPathAdapter(this, repos.getName());
        pathAdapter.SetOnItemClickListener(new ReposPathAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startRefresh();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		pathRecyclerView.setLayoutManager(layoutManager);
        pathRecyclerView.setItemAnimator(new SlideInRightAnimator());
        pathRecyclerView.setAdapter(pathAdapter);

		getSwipeRefreshLayout().setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onBackPressed() {
        if (pathAdapter != null && pathAdapter.isRoot()) {
            pathAdapter.goPrevious();
            startRefresh();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void startRefresh(){
    	super.startRefresh();
    	requestContents();
    }

	public void onOK(ArrayList<Content> ts){
        if(ts.size() == 0){
            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
        }
        else {
            adapter.update(ts);
        }
        endRefresh();
	}

    private void showContent(Content content){
		Intent intent = new Intent(ReposContentsListActivity.this, ReposContentActivity.class);
		Bundle bundle = new Bundle();
        bundle.putParcelable(HotReposFragment.REPOS, repos);
		bundle.putParcelable(CONTENT, content);
		intent.putExtras(bundle);
		startActivity(intent);
    }
    
    private void requestContents(){
        getApiService().contents(repos.getOwner().getLogin(), repos.getName(), pathAdapter.getPathString())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<ArrayList<Content>>() {
					@Override
					public void onNext(ArrayList<Content> result) {
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
