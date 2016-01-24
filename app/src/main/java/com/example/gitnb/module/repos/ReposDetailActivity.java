package com.example.gitnb.module.repos;

import java.text.SimpleDateFormat;

import com.example.gitnb.R;
import com.example.gitnb.api.OKHttpClient;
import com.example.gitnb.api.RepoActionsClient;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.Repository;
import com.example.gitnb.module.user.HotUserFragment;
import com.example.gitnb.module.user.UserDetailActivity;
import com.example.gitnb.module.user.UserListActivity;
import com.example.gitnb.utils.MessageUtils;
import com.example.gitnb.utils.Utils;
import com.example.gitnb.widget.FlipImageView;
import com.facebook.drawee.view.SimpleDraweeView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class ReposDetailActivity extends BaseSwipeActivity{

	private String TAG = "ReposDetailActivity";
	public static String CONTENT_URL = "content_url";
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private LinearLayout main;
	private Repository repos;
    private Switch swithBt;
	private FlipImageView star;
	
    protected void setTitle(TextView view){
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
        repos = (Repository) intent.getParcelableExtra(HotReposFragment.REPOS);
        setContentView(R.layout.activity_repo_detail);
        main = (LinearLayout) findViewById(R.id.main);
        main.setVisibility(View.GONE);

        swithBt = (Switch) findViewById(R.id.switch_bt);
		star = (FlipImageView) findViewById(R.id.star);
	}

    private void setSwitchClicker(){
        swithBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
        	   if(swithBt.isChecked()){
        		   starRepo();
        	   }
        	   else{
        		   unstarRepo();
        	   }
            }
         
         });
    }
    
    private void setRepository(){
    	TextView repos_name = (TextView) findViewById(R.id.repos_name);
    	TextView repos_owner = (TextView) findViewById(R.id.repos_owner);
    	TextView repos_updated = (TextView) findViewById(R.id.repos_updated);
    	TextView repos_homepage = (TextView) findViewById(R.id.repos_homepage);
    	TextView repos_discription = (TextView) findViewById(R.id.repos_description);
    	SimpleDraweeView user_avatar = (SimpleDraweeView) findViewById(R.id.user_avatar);
    	
		user_avatar.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReposDetailActivity.this, UserDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotUserFragment.USER, repos.getOwner());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
    	TextView type = (TextView) findViewById(R.id.type);
    	TextView issues = (TextView) findViewById(R.id.issues);
    	TextView created_date = (TextView) findViewById(R.id.created_date);
    	TextView language = (TextView) findViewById(R.id.language);
    	TextView forks = (TextView) findViewById(R.id.forks);
    	TextView size = (TextView) findViewById(R.id.size);
    	
		if(this.repos != null){
			repos_name.setText(repos.getName());	
			//repos_updated.setText(repos.getUpdated_at());				
			int hours = Utils.fromNow(repos.getUpdated_at());
			int days = hours/24;
			int months = days/30;
			int years = months/12;
			if(years == 1){
				repos_updated.setText("Updated " + years+" year ago");
			}
			else if(years > 1){
				repos_updated.setText("Updated " + years+" years ago");
			}
			else if(months == 1){
				repos_updated.setText("Updated " + months+" month ago");
			}
			else if(months > 1){
				repos_updated.setText("Updated " + months+" months ago");
			}
			else if(days == 1){
				repos_updated.setText("Updated " + days+" day ago");
			}
			else if(days > 1){
				repos_updated.setText("Updated " + days+" days ago");
			}
			else if(hours > 1){
				repos_updated.setText("Updated " + hours + " hours ago");
			}
			else{
				repos_updated.setText("Updated " + hours + " hour ago");
			}
			repos_homepage.setText(repos.getHomepage());
			repos_discription.setText(repos.getDescription());

			type.setText(repos.is_private() ? "Private" : "Public");
			issues.setText(repos.getOpen_issues()+" Issues");			
			created_date.setText(format.format(Utils.getDate(repos.getCreated_at())));
			language.setText(repos.getLanguage());
			forks.setText(repos.getForks_count()+" Forks");
			size.setText((float)((repos.getSize()/1024*100))/100+"M");			
			float m = repos.getSize()/1024;
			if(m>0){
				size.setText(((int)(m*100))/100+"M");
			}
			else{
				size.setText(repos.getSize()+"K");
			}
		}
		if(repos.getOwner() != null){
			repos_owner.setText(repos.getOwner().getLogin());
			user_avatar.setImageURI(Uri.parse(repos.getOwner().getAvatar_url()));
		}
		
    	TextView stargazers = (TextView) findViewById(R.id.stargazers);
    	TextView readme = (TextView) findViewById(R.id.readme);
    	TextView contributor = (TextView) findViewById(R.id.contributor);
    	TextView source = (TextView) findViewById(R.id.source);
    	TextView events = (TextView) findViewById(R.id.events);
    	stargazers.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReposDetailActivity.this, UserListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotReposFragment.REPOS, repos);
				intent.putExtras(bundle);
				intent.putExtra(UserListActivity.USER_TYPE, UserListActivity.USER_TYPE_STARGZER);
				startActivity(intent);
				
			}
		});
    	readme.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReposDetailActivity.this, ReposContentActivity.class);
				intent.putExtra(CONTENT_URL, repos.getUrl());
				startActivity(intent);
			}
		});
    	contributor.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReposDetailActivity.this, UserListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotReposFragment.REPOS, repos);
				intent.putExtras(bundle);
				intent.putExtra(UserListActivity.USER_TYPE, UserListActivity.USER_TYPE_CONTRIBUTOR);
				startActivity(intent);
			}
		});
    	source.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReposDetailActivity.this, ReposContentsListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotReposFragment.REPOS, repos);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
    	events.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReposDetailActivity.this, EventListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotReposFragment.REPOS, repos);
				intent.putExtras(bundle);
				intent.putExtra(EventListActivity.EVENT_TYPE, EventListActivity.EVENT_TYPE_REPOS);
				startActivity(intent);
			}
		});
    }
    
    @Override
    protected void startRefresh(){
    	super.startRefresh();
        getRepositoryInfo();
    }

    @Override
    protected void endRefresh(){
    	super.endRefresh();
        main.setVisibility(View.VISIBLE);
    	setRepository();
    }

    @Override
    protected void endError(){
    	super.endError();
    }
    
    private void getRepositoryInfo(){
    	OKHttpClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<Repository>() {

			@Override
			public void onOK(Repository ts) {
				repos = ts;
		        checkIfRepoIsStarred();
		        getRefreshdler().sendEmptyMessage(END_UPDATE);
			}

			@Override
			public void onError(String Message) {
				MessageUtils.showErrorMessage(ReposDetailActivity.this, Message);
				getRefreshdler().sendEmptyMessage(END_ERROR);
			}
			
    	}).request(repos.getUrl(), Repository.class);
    }
    
	private void checkIfRepoIsStarred(){
		RepoActionsClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<Object>() {

			@Override
			public void onOK(Object ts) {
		        swithBt.setVisibility(View.VISIBLE);
				swithBt.setChecked(true);
				setSwitchClicker();
			}

			@Override
			public void onError(String Message) {
		        swithBt.setVisibility(View.VISIBLE);
				setSwitchClicker();
			}
			
    	}).checkIfRepoIsStarred(repos.getOwner().getLogin(), repos.getName());
	}
	
	private void starRepo(){
		star.start();
		final Snackbar snackbar = Snackbar.make(getSwipeRefreshLayout(), "UnStaring ...", Snackbar.LENGTH_INDEFINITE);
		snackbar.show();
		RepoActionsClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<Object>() {

			@Override
			public void onOK(Object ts) {
				swithBt.setChecked(true);
				star.setState(FlipImageView.Type.CLOSE);
				snackbar.dismiss();
			}

			@Override
			public void onError(String Message) {
				snackbar.dismiss();
				MessageUtils.showErrorMessage(ReposDetailActivity.this, Message);
			}
			
    	}).starRepo(repos.getOwner().getLogin(), repos.getName());
	}	
	
	private void unstarRepo(){
		star.start();
		final Snackbar snackbar = Snackbar.make(getSwipeRefreshLayout(), "Staring ...", Snackbar.LENGTH_INDEFINITE);
		snackbar.show();
		RepoActionsClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<Object>() {

			@Override
			public void onOK(Object ts) {
				swithBt.setChecked(false);
				star.setState(FlipImageView.Type.OPEN);
				snackbar.dismiss();
			}

			@Override
			public void onError(String Message) {
				snackbar.dismiss();
				MessageUtils.showErrorMessage(ReposDetailActivity.this, Message);
			}
			
    	}).unstarRepo(repos.getOwner().getLogin(), repos.getName());
	}
}
