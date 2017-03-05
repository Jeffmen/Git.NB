package com.example.gitnb.module.issue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.example.gitnb.R;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Event;
import com.example.gitnb.model.GithubComment;
import com.example.gitnb.model.Issue;
import com.example.gitnb.model.IssueRequest;
import com.example.gitnb.model.IssueState;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.User;
import com.example.gitnb.module.repos.EventListActivity;
import com.example.gitnb.module.repos.EventListAdapter;
import com.example.gitnb.module.repos.ReposDetailActivity;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.module.search.HotUserFragment;
import com.example.gitnb.utils.CurrentUser;
import com.example.gitnb.utils.Utils;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueListActivity  extends BaseSwipeActivity {
    private String TAG = IssueListActivity.class.getName();
    public static final String ISSUE_TYPE = "issue_type";
    public static final String ISSUE_TYPE_REPOS = "issue_REPOS";
    public static final String ISSUE_TYPE_USER = "issue_USER";
    private IssueListAdapter adapter;
    private RecyclerView recyclerView;
    private PopupWindow popupWindow;
    private boolean isLoadingMore;
    private Repository repos;
    private User user;
    private String type;
    private int page = 1;


    private Observer<ArrayList<Issue>> observer = new Observer<ArrayList<Issue>>() {
        @Override
        public void onNext(ArrayList<Issue> result) {
            onOK(result);
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable error) {
            endError(error.getMessage());
        }
    };

    @Override
    protected void setTitle(TextView view) {
        if(repos != null && !repos.getName().isEmpty()){
            switch(type){
                case ISSUE_TYPE_REPOS:
                    view.setText(repos.getName()+" / Issues");
                    break;
            }
            setUserBackground(repos.getOwner().getAvatar_url());
        }else if(user != null && !user.getLogin().isEmpty()){
            switch(type){
                case ISSUE_TYPE_USER:
                    view.setText(user.getLogin()+" / Issues");
                    break;
            }
            setUserBackground(user.getAvatar_url());
        }else{
            view.setText("NULL");
        }
        view.setSelected(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        page = 1;
        repos = (Repository) intent.getParcelableExtra(HotReposFragment.REPOS);
        type = intent.getStringExtra(ISSUE_TYPE);
        switch(type){
            case ISSUE_TYPE_REPOS:
                repos = (Repository) intent.getParcelableExtra(HotReposFragment.REPOS);
                break;
            case ISSUE_TYPE_USER:
                user = (User) intent.getParcelableExtra(HotUserFragment.USER);
                break;
        }
        this.setContentView(R.layout.activity_list_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repos_detail_menu, menu);
//		shareActionProvider = (ShareActionProvider) item.getActionProvider();

        //返回true,显示菜单
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_item_add_issue).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add_issue:
                showAddIssuePop(recyclerView);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void startRefresh(){
        super.startRefresh();
        switch(type){
            case ISSUE_TYPE_REPOS:
                getReposIssues();
        }
    }

    @Override
    protected void endRefresh(){
        super.endRefresh();
        isLoadingMore = false;
    }

    @Override
    protected void endError(String Message){
        super.endError(Message);
        isLoadingMore = false;
    }

    private void updateAdapter(ArrayList<Issue> ts){
        if(adapter == null) {
            adapter = new IssueListAdapter(this);
            adapter.update(ts);
            adapter.setOnSearchClickListener(new IssueListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    page = 1;
                    startRefresh();
                }
            });
            adapter.setOnItemClickListener(new IssueListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(IssueListActivity.this, IssueDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(HotReposFragment.REPOS, repos);
                    bundle.putParcelable(IssueDetailActivity.ISSUE, adapter.getItem(position));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            adapter.setOnLoadMoreClickListener(new IssueListAdapter.OnItemClickListener() {

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
                recyclerView.scrollToPosition(0);
            }
            else{
                isLoadingMore = false;
                adapter.insertAtBack(ts);
            }
        }
    }

    public void onOK(ArrayList<Issue> ts) {
        if(ts.size() == 0){
            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
        }
        else {
            updateAdapter(ts);
        }
        endRefresh();
    }

    private void showAddIssuePop(View view){
        View contentView = LayoutInflater.from(this).inflate(R.layout.add_issue_popup_layout, null, false);
        final EditText title = (EditText)contentView.findViewById(R.id.title);
        final EditText comment = (EditText)contentView.findViewById(R.id.comment);
        ImageView back = (ImageView)contentView.findViewById(R.id.cancel);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        TextView send = (TextView)contentView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIssue(title.getText().toString(), comment.getText().toString());
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        final WindowManager.LayoutParams wlBackground = getWindow().getAttributes();
        wlBackground.alpha = 0.75f;
        getWindow().setAttributes(wlBackground);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wlBackground.alpha = 1.0f;
                getWindow().setAttributes(wlBackground);
            }
        });
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setAnimationStyle(R.style.anim_popup_bottombar);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    private void getReposIssues(){
        Map<String, String> filter = new HashMap();
        filter.put("state", adapter == null ? "open" : adapter.getSearchText());
        getApiService().reposIssues(repos.getOwner().getLogin(), repos.getName(), filter, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    private void createIssue(String title, String body){
        Issue issue = new Issue();
        issue.title = title;
        issue.body = body;
        issue.user = CurrentUser.getInstance().getMe();
        issue.updated_at = Utils.now();
        issue.state = IssueState.open;
        issue.comments = 0;
        issue.number = 0;
        adapter.add(issue);
        recyclerView.smoothScrollToPosition(0);

        IssueRequest issueRequest = new IssueRequest();
        issueRequest.title = title;
        issueRequest.body = body;

        getApiService().createIssue(repos.getOwner().getLogin(), repos.getName(), issueRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Issue>() {
                    @Override
                    public void onNext(Issue result) {
                        startRefresh();
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
