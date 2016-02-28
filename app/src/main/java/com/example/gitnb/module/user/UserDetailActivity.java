package com.example.gitnb.module.user;

import java.text.SimpleDateFormat;

import com.example.gitnb.R;
import com.example.gitnb.api.RetrofitNetworkAbs;
import com.example.gitnb.api.UsersClient;
import com.example.gitnb.app.BaseSwipeActivity;
import com.example.gitnb.model.User;
import com.example.gitnb.module.repos.EventListActivity;
import com.example.gitnb.module.repos.ReposListActivity;
import com.example.gitnb.utils.MessageUtils;
import com.example.gitnb.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kyleduo.switchbutton.SwitchButton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class UserDetailActivity extends BaseSwipeActivity{

	private String TAG = "UserDetailActivity";
	public static String AVATAR_URL = "avatar_url";
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private FloatingActionButton faButton;
    private LinearLayout main;
	private boolean isFollow = false;
	private User user;
	
    protected void setTitle(TextView view){
        if(user != null && !user.getLogin().isEmpty()){
        	view.setText(user.getLogin());
        }else{
        	view.setText("NULL");
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        user = (User) intent.getParcelableExtra(HotUserFragment.USER);
        setContentView(R.layout.activity_user_detail);
        main = (LinearLayout) findViewById(R.id.main);
        main.setVisibility(View.GONE);

		faButton = (FloatingActionButton) findViewById(R.id.faButton);
		faButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isFollow) {
					unFollowUser();
				} else {
					followUser();
				}
			}
		});
    }

	private void showFollow(boolean value){
		this.isFollow = value;
		AnimatorSet bouncer = new AnimatorSet();
		ObjectAnimator alpha = ObjectAnimator.ofFloat(faButton, "alpha", 0.0f, 1.0f);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(faButton, "scaleX", 0.0f, 1.0f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(faButton, "scaleY", 0.0f, 1.0f);
		bouncer.play(alpha).with(scaleX).with(scaleY);
		bouncer.setDuration(500);
		bouncer.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				faButton.setVisibility(View.VISIBLE);
				if(isFollow) {
					ColorStateList colorRed = ColorStateList.valueOf(getResources().getColor(R.color.orange_yellow));
					faButton.setBackgroundTintList(colorRed);
					faButton.setImageResource(R.drawable.ic_like_off_white);
					//ColorStateList colorWhite = ColorStateList.valueOf(Color.WHITE);
					//faButton.setImageTintList(colorWhite);
				}
				else{
					ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);
					ViewCompat.setBackgroundTintList(faButton, colorStateList);
					faButton.setImageResource(R.drawable.ic_like_on_yellow);
				}
			}
		});
		bouncer.start();
	}

    private void setUserInfo(){
		TextView user_name = (TextView) findViewById(R.id.user_name);
		TextView user_company = (TextView) findViewById(R.id.user_company);
		TextView user_location = (TextView) findViewById(R.id.user_location);
		TextView user_created_date = (TextView) findViewById(R.id.user_created_date);
		TextView user_blog = (TextView) findViewById(R.id.user_blog);
		TextView user_email = (TextView) findViewById(R.id.user_email);
		SimpleDraweeView user_avatar = (SimpleDraweeView) findViewById(R.id.user_avatar);
		
		if(user!=null){
			user_name.setText(user.getName());
			user_location.setText(user.getLocation());
			user_avatar.setImageURI(Uri.parse(user.getAvatar_url()));
			user_email.setText(user.getEmail());
			user_created_date.setText(format.format(Utils.getDate(user.getCreated_at())));
			user_blog.setText(user.getBlog());
			user_company.setText(user.getCompany());
			if(user.getCompany() == null || user.getCompany().isEmpty()){
				user_company.setVisibility(View.VISIBLE);
			}
		}			
		
		user_avatar.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(UserDetailActivity.this, ImageShowerActivity.class);
				intent.putExtra(UserDetailActivity.AVATAR_URL, user.getAvatar_url());
				startActivity(intent);
			}
        	
        });
		
    	TextView events = (TextView) findViewById(R.id.events);
    	TextView organizations = (TextView) findViewById(R.id.organizations);
    	TextView followers = (TextView) findViewById(R.id.followers);
    	TextView following = (TextView) findViewById(R.id.following);
    	TextView repositories = (TextView) findViewById(R.id.repositorys);

    	events.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserDetailActivity.this, EventListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotUserFragment.USER, user);
				intent.putExtras(bundle);
				intent.putExtra(EventListActivity.EVENT_TYPE, EventListActivity.EVENT_TYPE_USER);
				startActivity(intent);
			}
		});
    	organizations.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserDetailActivity.this, OrganizationListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotUserFragment.USER, user);
				intent.putExtras(bundle);
				intent.putExtra(OrganizationListActivity.ORGANIZATION_TYPE, OrganizationListActivity.ORGANIZATION_TYPE_USER);
				startActivity(intent);
			}
		});
		followers.setText("Followers(" + user.getFollowers() + ")");
    	followers.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserDetailActivity.this, UserListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotUserFragment.USER, user);
				intent.putExtras(bundle);
				intent.putExtra(UserListActivity.USER_TYPE, UserListActivity.USER_TYPE_FOLLOWER);
				startActivity(intent);
			}
		});
		following.setText("Following(" + user.getFollowing() + ")");
    	following.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserDetailActivity.this, UserListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotUserFragment.USER, user);
				intent.putExtras(bundle);
				intent.putExtra(UserListActivity.USER_TYPE, UserListActivity.USER_TYPE_FOLLOWING);
				startActivity(intent);
			}
		});
		repositories.setText("Repositories(" + user.getPublic_repos() + ")");
		repositories.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserDetailActivity.this, ReposListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotUserFragment.USER, user);
				intent.putExtras(bundle);
				intent.putExtra(ReposListActivity.REPOS_TYPE, ReposListActivity.REPOS_TYPE_USER);
				startActivity(intent);
			}
		});
    }
    
	@Override
    protected void startRefresh(){
		super.startRefresh();
        getSingleUser();
        checkFollowing();
    }

	@Override
    protected void endRefresh(){
		super.endRefresh();
        setUserInfo();
        main.setVisibility(View.VISIBLE);
    }

	@Override
    protected void endError(){
		super.endError();
    }
    
	private void getSingleUser(){
    	UsersClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<User>() {

			@Override
			public void onOK(User ts) {
				user = ts;
				getRefreshHandler().sendEmptyMessage(END_UPDATE);
			}

			@Override
			public void onError(String Message) {
				MessageUtils.showErrorMessage(UserDetailActivity.this, Message);
				getRefreshHandler().sendEmptyMessage(END_ERROR);
			}
			
    	}).getSingleUser(user.getLogin());
	} 
	
	private void checkFollowing(){
    	UsersClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<Object>() {

			@Override
			public void onOK(Object ts) {
				showFollow(true);
			}

			@Override
			public void onError(String Message) {
				showFollow(false);
			}
			
    	}).checkFollowing(user.getLogin());
	}
	
	private void followUser(){
		final Snackbar snackbar = Snackbar.make(getSwipeRefreshLayout(), "Following ...", Snackbar.LENGTH_INDEFINITE);
		snackbar.show();
    	UsersClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<Object>() {

			@Override
			public void onOK(Object ts) {
				snackbar.dismiss();
				showFollow(true);
				//Snackbar.make(getSwipeRefreshLayout(), "Already Followed", Snackbar.LENGTH_LONG).show();
			}

			@Override
			public void onError(String Message) {
				snackbar.dismiss();
				MessageUtils.showErrorMessage(UserDetailActivity.this, Message);
			}
			
    	}).followUser(user.getLogin());
	}	
	
	private void unFollowUser(){
		final Snackbar snackbar = Snackbar.make(getSwipeRefreshLayout(), "UnFollowing ...", Snackbar.LENGTH_INDEFINITE);
		snackbar.show();
    	UsersClient.getNewInstance().setNetworkListener(new RetrofitNetworkAbs.NetworkListener<Object>() {

			@Override
			public void onOK(Object ts) {
				snackbar.dismiss();
				showFollow(false);
				//Snackbar.make(getSwipeRefreshLayout(), "Already unFollowed", Snackbar.LENGTH_LONG).show();
			}

			@Override
			public void onError(String Message) {
				snackbar.dismiss();
				MessageUtils.showErrorMessage(UserDetailActivity.this, Message);
			}
			
    	}).unFollowUser(user.getLogin());
	}
    
}
