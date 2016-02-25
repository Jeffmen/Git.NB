package com.example.gitnb.module.repos;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.gitnb.R;
import com.example.gitnb.api.RepoClient;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Content;
import com.example.gitnb.model.Repository;
import com.example.gitnb.module.viewholder.HorizontalDividerItemDecoration;
import com.example.gitnb.utils.MessageUtils;

public class ReposContentsListActivity extends BaseSwipeActivity {
	public static String CONTENT = "content";
    private LinearLayoutManager mLayoutManager;
    private ReposContentsAdapter adapter;
    private RecyclerView recyclerView;
	private Repository repos;
    private String path = "";
    private String clickName = "";
    
	public enum TYPE{
		REPOS_CONTENT,
		REPOS_CONTRIBUTOR,
		USER_FOLLOWER,
		USER_FOLLOWING,
		USER_REPOSITORY
	}    
	

	@Override
	protected void setTitle(TextView view) {
        if(repos != null && !repos.getName().isEmpty()){
        	view.setText(repos.getName());
        }else{
        	view.setText("NULL");
        }
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        repos = intent.getParcelableExtra(HotReposFragment.REPOS);
        setContentView(R.layout.activity_list_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new ReposContentsAdapter(this);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        adapter.SetOnItemClickListener(new ReposContentsAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Content content = adapter.getItem(position);
				if (content.isDir()) {
					clickName = content.name;
					getRefreshandler().sendEmptyMessage(START_UPDATE);
				}
				if (content.isFile()) {
					showContent(content);
				}
			}
		});
        adapter.SetOnHeadClickListener(new ReposContentsAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				if (path.isEmpty() || path.equals("/")) {
					Snackbar.make(getSwipeRefreshLayout(), "Already to root ...", Snackbar.LENGTH_LONG).show();
				} else {
					int pos;
					if (path != null && !path.isEmpty()) {
						pos = path.lastIndexOf("/");
						if (pos >= 0)
							path = path.substring(0, pos);
					}
					if (path != null && !path.isEmpty()) {
						pos = path.lastIndexOf("/");
						if (pos >= 0)
							path = path.substring(0, pos);
					}
					clickName = "";
					getRefreshandler().sendEmptyMessage(START_UPDATE);
				}
			}
		});
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

		getSwipeRefreshLayout().setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void startRefresh(){
    	super.startRefresh();
    	requestContents();
    }

    @Override
    protected void endRefresh(){
    	super.endRefresh();
    }

    @Override
    protected void endError(){
    	super.endError();
    }
    
    private void showContent(Content content){
		Intent intent = new Intent(ReposContentsListActivity.this, ReposContentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable(CONTENT, content);
		intent.putExtras(bundle);
		startActivity(intent);
    }
    
    private void requestContents(){
    	RepoClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<ArrayList<Content>>() {

			@Override
			public void onOK(ArrayList<Content> ts) {
				path += clickName + "/";
				adapter.update(ts);
				getRefreshandler().sendEmptyMessage(END_UPDATE);
			}

			@Override
			public void onError(String Message) {
				MessageUtils.showErrorMessage(ReposContentsListActivity.this, Message);
				getRefreshandler().sendEmptyMessage(END_ERROR);
			}
			
    	}).contents(repos.getOwner().getLogin(), repos.getName(), path + clickName);
    }
    
}
