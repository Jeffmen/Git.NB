package com.example.gitnb.module.user;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.example.gitnb.R;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.User;
import com.example.gitnb.module.viewholder.LoadMoreViewHolder;
import com.example.gitnb.module.viewholder.ReposViewHolder;
import com.example.gitnb.module.viewholder.UserDetailViewHolder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

public class UserReposAdapter extends RecyclerView.Adapter<ViewHolder>{

	private Context mContext;
    private static final int TYPE_HEADER_VIEW = 2;
    private static final int TYPE_FOOTER_VIEW = 1;
    private static final int TYPE_NOMAL_VIEW = 0;
    private static final int PAGE_COUNT = 30;
    private OnItemClickListener mLoadMoreClickListener;
    private OnItemClickListener mItemClickListener;
    protected final LayoutInflater mInflater;
    private boolean isShowLoadMore = true;
    private boolean isLoadingMore = false;
    private ArrayList<Repository> mRepos;
    private User userInfo; 

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    
    public UserReposAdapter(Context context, User userInfo) {
    	mContext = context;
    	this.userInfo = userInfo;
    	mInflater = LayoutInflater.from(mContext);
	}
    
    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    public void SetOnLoadMoreClickListener(final OnItemClickListener mLastItemClickListener) {
        this.mLoadMoreClickListener = mLastItemClickListener;
    }
    
    public void UpdateUserInfo(User value) {
        this.userInfo = value;
        notifyDataSetChanged();
    }
    
	public Repository getItem(int position) {
		if(position == 0){
			return null;
		}
		if(isShowLoadMore && position == getItemCount()-1){
			return null;
		}
		return mRepos == null ? null : mRepos.get(position-1);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    
    public void update(ArrayList<Repository> data){
    	if(data == null || data.size()<PAGE_COUNT){
    		isShowLoadMore = false;
    	}   
    	else{
    		isShowLoadMore = true;
    	}
//    	if (data != null && data.size() > 0){
    	mRepos= data;
//    	}
    	reset();
    }
    
    public void insertAtBack(ArrayList<Repository> data){
    	if(data == null || data.size()<PAGE_COUNT){
    		isShowLoadMore = false;
    	}
    	else{
    		isShowLoadMore = true;
    	}
        if (data != null && data.size() > 0){
        	mRepos.addAll(data);
        }
    	reset();
    }

    public void reset(){
    	this.isLoadingMore = false;
        notifyDataSetChanged();
    }
    
	@Override
	public int getItemCount() {
		return (mRepos == null ? 0 : mRepos.size())+(isShowLoadMore ? 2 : 1);
	}
	
    @Override
    public int getItemViewType(int position) {
    	if(position == 0){
    		return TYPE_HEADER_VIEW;
    	}
    	else if (isShowLoadMore && getItemCount() - 1 == position) {
    		return TYPE_FOOTER_VIEW;
    	}
    	return TYPE_NOMAL_VIEW;
    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int viewType) {
		if(viewType == TYPE_FOOTER_VIEW){
			View v = mInflater.inflate(R.layout.list_data_load_more,viewgroup,false);
			return new LoadMoreView(v);
		}
		else if(viewType == TYPE_HEADER_VIEW){
			View v = mInflater.inflate(R.layout.user_detail_item,viewgroup,false);
			return new UserDetailView(v);
		}
		else{
			View v = mInflater.inflate(R.layout.repos_list_item,viewgroup,false);
			return new ReposView(v);
		}
	}
	  
	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {		
		switch(getItemViewType(position)){
		case TYPE_HEADER_VIEW:
			if(userInfo!=null){
				UserDetailView userInfoVeiwHolder = (UserDetailView) vh;
				userInfoVeiwHolder.user_name.setText(userInfo.getName());
				userInfoVeiwHolder.user_location.setText(userInfo.getLocation());
				String date = userInfo.getCreated_at();
				if(date != null && !date.isEmpty()){
					date = date.substring(0, date.indexOf('T'));
				}
				userInfoVeiwHolder.user_avatar.setImageURI(Uri.parse(userInfo.getAvatar_url()));
				userInfoVeiwHolder.user_email.setText(userInfo.getEmail());
				userInfoVeiwHolder.user_created_date.setText(date);
				userInfoVeiwHolder.user_blog.setText(userInfo.getBlog());
				userInfoVeiwHolder.user_company.setText(userInfo.getCompany());
				if(userInfo.getCompany() == null || userInfo.getCompany().isEmpty()){
					userInfoVeiwHolder.user_company.setVisibility(View.VISIBLE);
				}
			}
			break;
		case TYPE_FOOTER_VIEW:
			LoadMoreView loadMoreViewHolder = (LoadMoreView) vh;
			Uri uri = (new Uri.Builder()).scheme("res").path(String.valueOf(R.drawable.github_loading)).build();
			DraweeController  draweeController= Fresco.newDraweeControllerBuilder()
					.setAutoPlayAnimations(isLoadingMore)
	                .setUri(uri)
	                .build();
			loadMoreViewHolder.loading_gif.setController(draweeController);
			break;
		case TYPE_NOMAL_VIEW:
			ReposView viewHolder = (ReposView) vh;
			Repository item = getItem(position);
			if(item != null){
				viewHolder.repos_name.setText(item.getName());
				viewHolder.repos_star.setText("Star:"+item.getStargazers_count());
				viewHolder.repos_fork.setText(item.isFork()?"fork":"owner");
				viewHolder.repos_language.setText(item.getLanguage());
				viewHolder.repos_discription.setText(item.getDescription());
			}
			viewHolder.user_avatar.setVisibility(View.GONE);
			viewHolder.repos_rank.setText(String.valueOf(position)+".");
			break;
		}
	}
	
	private class UserDetailView extends UserDetailViewHolder{

		public UserDetailView(View view) {
			super(view);
			user_avatar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(mContext, ImageShowerActivity.class);
					intent.putExtra(UserDetailActivity.AVATAR_URL, userInfo.getAvatar_url());
					mContext.startActivity(intent);
				}
	        	
	        });
		}
	}
	
	private class ReposView extends ReposViewHolder implements View.OnClickListener{
		
		public ReposView(View view) {
			super(view);
            view.setOnClickListener(this);
		}
	
		@Override
		public void onClick(View v) {
	        if (mItemClickListener != null) {
	            mItemClickListener.onItemClick(v, getLayoutPosition());
	        }
		}
	}
	
	private class LoadMoreView extends LoadMoreViewHolder implements View.OnClickListener{
		
		public LoadMoreView(View view) {
			super(view);
            view.setOnClickListener(this);
		}
	
		@Override
		public void onClick(View v) {
			DraweeController draweeController = loading_gif.getController();
			Animatable animatable = draweeController.getAnimatable();
			animatable.start();
			isLoadingMore = true;
	        if (mLoadMoreClickListener != null) {
	        	mLoadMoreClickListener.onItemClick(v, getLayoutPosition());
	        }
		}
	}
	
}

