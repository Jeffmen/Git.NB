package com.example.gitnb.module.issue;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;

import com.example.gitnb.R;
import com.example.gitnb.model.Event;
import com.example.gitnb.model.GithubComment;
import com.example.gitnb.model.Issue;
import com.example.gitnb.module.repos.ReposDetailActivity;
import com.example.gitnb.module.search.HotReposFragment;
import com.example.gitnb.module.search.HotUserFragment;
import com.example.gitnb.module.user.UserDetailActivity;
import com.example.gitnb.module.viewholder.EventViewHolder;
import com.example.gitnb.module.viewholder.IssueHeaderViewHolder;
import com.example.gitnb.module.viewholder.IssueViewHolder;
import com.example.gitnb.module.viewholder.LoadMoreViewHolder;
import com.example.gitnb.module.viewholder.SearchViewHolder;
import com.example.gitnb.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

import java.util.ArrayList;

public class IssueListAdapter extends RecyclerView.Adapter<ViewHolder>{

	private Context mContext;
    private static final int TYPE_HEADER_VIEW = 2;
    private static final int TYPE_FOOTER_VIEW = 1;
    private static final int TYPE_NOMAL_VIEW = 0;
    private static final int PAGE_COUNT = 30;
    protected final LayoutInflater mInflater;
    private ArrayList<Issue> issues;
    private OnItemClickListener mItemClickListener;
    private OnItemClickListener mLoadMoreClickListener;
    private OnItemClickListener mSearchClickListener;
    private boolean isShowLoadMore = false;
    private boolean isShowSearch = true;
    private boolean isLoadingMore = false;
    private String searchText = "open";

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public IssueListAdapter(Context context) {
    	mContext = context;
    	mInflater = LayoutInflater.from(mContext);
	}
    
    public void setShowLoadMore(boolean value){
    	this.isShowLoadMore = value;
    }
    
    public void setShowSearch(boolean value){
    	this.isShowSearch = value;
    }
    
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    public void setOnLoadMoreClickListener(final OnItemClickListener mLastItemClickListener) {
        this.mLoadMoreClickListener = mLastItemClickListener;
    }
    
    public void setOnSearchClickListener(final OnItemClickListener mSearchClickListener) {
        this.mSearchClickListener = mSearchClickListener;
    }
    
    public String getSearchText(){
    	return searchText;
    }
    
	public Issue getItem(int position) {
		if(isShowSearch && position == 0){
			return null;
		}
		if(isShowLoadMore && position == getItemCount()-1){
			return null;
		}
		return issues == null ? null : issues.get(position-(isShowSearch?1:0));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    
    public void update(ArrayList<Issue> data){
		isShowLoadMore = true;
    	if(data == null || data.size()<PAGE_COUNT){
    		isShowLoadMore = false;
    	}
		issues= data;
    	reset();
    }

	public void add(Issue issue){
		if(issues == null){
			issues = new ArrayList<>();
		}
		issues.add(0, issue);
		if(isShowSearch) {
			notifyItemInserted(1);
		}
		else{
			notifyItemInserted(0);
		}
	}

    public void insertAtBack(ArrayList<Issue> data){
    	if(data == null || data.size()<PAGE_COUNT){
    		isShowLoadMore = false;
    	}
    	else{
    		isShowLoadMore = true;
    	}
        if (data != null && data.size() > 0){
			issues.addAll(data);
        }
    	reset();
    }

    public void reset(){
    	this.isLoadingMore = false;
        notifyDataSetChanged();
    }
    
	@Override
	public int getItemCount() {
		int other = 0;
		if(isShowLoadMore) other++;
		if(isShowSearch) other++;
		if(issues == null){
			return 0 + other;
		}
		else {
			return issues.size() + other;
		}
	}
	
    @Override
    public int getItemViewType(int position) {
    	if(isShowSearch && position == 0){
    		return TYPE_HEADER_VIEW;
    	}
    	else if (isShowLoadMore && getItemCount() - 1 == position) { // footer
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
			View v = mInflater.inflate(R.layout.issue_header_item,viewgroup,false);
			return new SearchView(v);
		}
		else{
			View v = mInflater.inflate(R.layout.issue_list_item,viewgroup,false);
			return new IssueView(v);
		}
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {		
		switch(getItemViewType(position)){
		case TYPE_FOOTER_VIEW:
			LoadMoreView loadMoreViewHolder = (LoadMoreView) vh;
			Uri uri = (new Uri.Builder()).scheme("res").path(String.valueOf(R.drawable.github_loading)).build();
			DraweeController  draweeController= Fresco.newDraweeControllerBuilder()
					.setAutoPlayAnimations(isLoadingMore)
	                .setUri(uri)
	                .build();
			loadMoreViewHolder.loading_gif.setController(draweeController);
			loadMoreViewHolder.loading_txt.setText("load more...");
			break;
		case TYPE_NOMAL_VIEW:
			IssueView viewHolder = (IssueView) vh;
			Issue item = getItem(position);
			if(item != null){
				viewHolder.issue_rank.setText("#"+item.number);
				viewHolder.title.setText(item.title);
				viewHolder.state.setText(item.state.toString());
				viewHolder.comments.setText(item.comments+" comments");
				viewHolder.assignee.setText(item.user.getLogin());
				viewHolder.updated_at.setText(Utils.getTimeFromNow(item.updated_at));
				if(item.user != null){
					viewHolder.user_avatar.setImageURI(Uri.parse(item.user.getAvatar_url()));
				}
			}
			break;
		case TYPE_HEADER_VIEW:
			SearchView searchHolder = (SearchView) vh;
			if(searchText != null && searchText.equals("open")){
				searchHolder.open.setTextColor(Color.GRAY);
				searchHolder.close.setTextColor(Color.BLACK);
				Drawable drawable = mContext.getResources().getDrawable(R.drawable.custom_dropdown_spinner);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
				searchHolder.open.setCompoundDrawables(null, null, null, null);
				searchHolder.close.setCompoundDrawables(drawable, null, null, null);
			}
			else{
				searchHolder.open.setTextColor(Color.BLACK);
				searchHolder.close.setTextColor(Color.GRAY);
				Drawable drawable = mContext.getResources().getDrawable(R.drawable.custom_dropdown_spinner);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
				searchHolder.open.setCompoundDrawables(drawable, null, null, null);
				searchHolder.close.setCompoundDrawables(null, null, null, null);
			}
			break;
		}
	}

	private class IssueView extends IssueViewHolder implements View.OnClickListener{
		
		public IssueView(View view) {
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
	
	private class SearchView extends IssueHeaderViewHolder {
		
		public SearchView(View view) {
			super(view);
			open_layout.setOnClickListener( new View.OnClickListener(){
	            public void onClick(View v) {
					searchText = "open";
					open.setTextColor(Color.GRAY);
					close.setTextColor(Color.BLACK);
					Drawable drawable = mContext.getResources().getDrawable(R.drawable.custom_dropdown_spinner);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
					open.setCompoundDrawables(drawable, null, null, null);
					close.setCompoundDrawables(null, null, null, null);
	    	        if (mSearchClickListener != null) {
	    	        	mSearchClickListener.onItemClick(v, getLayoutPosition());
	    	        }

	            }           
	        });      
			close_layout.setOnClickListener( new View.OnClickListener(){
	            public void onClick(View v) {
					searchText = "closed";
					open.setTextColor(Color.BLACK);
					close.setTextColor(Color.GRAY);
					Drawable drawable = mContext.getResources().getDrawable(R.drawable.custom_dropdown_spinner);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
					open.setCompoundDrawables(null, null, null, null);
					close.setCompoundDrawables(drawable, null, null, null);
	    	        if (mSearchClickListener != null) {
	    	        	mSearchClickListener.onItemClick(v, getLayoutPosition());
	    	        }
	            }           
	        });
		}
	}
}

