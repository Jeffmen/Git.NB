package com.example.gitnb.module.issue;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gitnb.R;
import com.example.gitnb.model.GithubComment;
import com.example.gitnb.model.Issue;
import com.example.gitnb.model.Label;
import com.example.gitnb.model.Repository;
import com.example.gitnb.module.viewholder.IssueCommentViewHolder;
import com.example.gitnb.module.viewholder.IssueDetailViewHolder;
import com.example.gitnb.module.viewholder.LoadMoreViewHolder;
import com.example.gitnb.utils.CurrentUser;
import com.example.gitnb.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

import org.w3c.dom.Comment;

import java.util.ArrayList;

public class IssueCommentListAdapter extends RecyclerView.Adapter<ViewHolder>{

	private Context mContext;
    private static final int TYPE_HEADER_VIEW = 2;
    private static final int TYPE_FOOTER_VIEW = 1;
    private static final int TYPE_NORMAL_VIEW = 0;
    private static final int PAGE_COUNT = 30;
    protected final LayoutInflater mInflater;
    private ArrayList<GithubComment> comments;
    private OnItemClickListener mItemClickListener;
    private OnItemClickListener mLoadMoreClickListener;
    private boolean isShowLoadMore = false;
    private boolean isShowHeader = true;
    private boolean isLoadingMore = false;
    private String searchText = "";
	private Repository repos;
	private Issue issue;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public IssueCommentListAdapter(Context context, Repository repos, Issue issue) {
		this.mInflater = LayoutInflater.from(context);
		this.mContext = context;
		this.repos = repos;
		this.issue = issue;
	}
    
    public void setShowLoadMore(boolean value){
    	this.isShowLoadMore = value;
    }
    
    public void setShowSearch(boolean value){
    	this.isShowHeader = value;
    }

	public boolean getShowSearch(){
		return this.isShowHeader;
	}
    
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    public void setOnLoadMoreClickListener(final OnItemClickListener mLastItemClickListener) {
        this.mLoadMoreClickListener = mLastItemClickListener;
    }
    
    public String getSearchText(){
    	return searchText;
    }
    
	public GithubComment getItem(int position) {
		if(isShowHeader && position == 0){
			return null;
		}
		if(isShowLoadMore && position == getItemCount()-1){
			return null;
		}
		return comments == null ? null : comments.get(position-(isShowHeader?1:0));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    
    public void update(ArrayList<GithubComment> data){
		isShowLoadMore = true;
    	if(data == null || data.size()<PAGE_COUNT){
    		isShowLoadMore = false;
    	}
		comments= data;
    	reset();
    }
    
    public void insertAtBack(ArrayList<GithubComment> data){
    	if(data == null || data.size()<PAGE_COUNT){
    		isShowLoadMore = false;
    	}
    	else{
    		isShowLoadMore = true;
    	}
        if (data != null && data.size() > 0){
			comments.addAll(data);
        }
    	reset();
    }

    public void reset(){
    	this.isLoadingMore = false;
        notifyDataSetChanged();
    }

	public void delete(int position){
		if(isShowHeader) {
			comments.remove(position-1);
		}
		else{
			comments.remove(position);
		}
		notifyItemRemoved(position);
	}

	public void add(GithubComment comment){
		if(comments == null){
			comments = new ArrayList<>();
		}
		comments.add(comment);
		if(isShowHeader) {
			notifyItemInserted(comments.size()+1);
		}
		else{
			notifyItemInserted(comments.size());
		}
	}
    
	@Override
	public int getItemCount() {
		int orther = 0;
		if(isShowLoadMore) orther++;
		if(isShowHeader) orther++;
		if(comments == null){
			return 0 + orther;
		}
		else {
			return comments.size() + orther;
		}
	}
	
    @Override
    public int getItemViewType(int position) {
    	if(isShowHeader && position == 0){
    		return TYPE_HEADER_VIEW;
    	}
    	else if (isShowLoadMore && getItemCount() - 1 == position) { // footer
    		return TYPE_FOOTER_VIEW;
    	}
    	return TYPE_NORMAL_VIEW;
    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int viewType) {
		if(viewType == TYPE_FOOTER_VIEW){
			View v = mInflater.inflate(R.layout.list_data_load_more,viewgroup,false);
			return new LoadMoreView(v);
		}
		else if(viewType == TYPE_HEADER_VIEW){
			View v = mInflater.inflate(R.layout.issue_detail_item,viewgroup,false);
			return new IssueDetailView(v);
		}
		else{
			View v = mInflater.inflate(R.layout.issue_comment_list_item,viewgroup,false);
			return new CommentView(v);
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
		case TYPE_NORMAL_VIEW:
			CommentView viewHolder = (CommentView) vh;
			GithubComment item = getItem(position);
			if(item != null){
				viewHolder.comment_rank.setText(String.valueOf(isShowHeader?position:position+1)+".");
				viewHolder.user_login.setText(item.user.getLogin());
				viewHolder.updated_at.setText("Updated " + Utils.getTimeFromNow(item.updated_at));
				viewHolder.comment.setText(item.body);
				if(item.user != null){
					viewHolder.user_avatar.setImageURI(Uri.parse(item.user.getAvatar_url()));
				}
				if(repos.getOwner().getId() == item.user.getId()){
					viewHolder.ownerLabel.setVisibility(View.VISIBLE);
				}
				else{
					viewHolder.ownerLabel.setVisibility(View.GONE);
				}
				if(issue.state.toString().equals("open")
				        && (CurrentUser.getInstance().getMe().getId() == item.user.getId()
						//|| CurrentUser.getInstance().getMe().getId() == issue.user.getId()
						|| CurrentUser.getInstance().getMe().getId() == repos.getOwner().getId())){
					//viewHolder.edit.setVisibility(View.VISIBLE);
					//viewHolder.delete.setVisibility(View.VISIBLE);
					viewHolder.operation.setVisibility(View.VISIBLE);
				}
				else{
					//viewHolder.edit.setVisibility(View.GONE);
					//viewHolder.delete.setVisibility(View.GONE);
					viewHolder.operation.setVisibility(View.GONE);
				}
			}
			break;
		case TYPE_HEADER_VIEW:
			IssueDetailView issueDetailView = (IssueDetailView) vh;
			issueDetailView.issue_updated.setText("Updated " + Utils.getTimeFromNow(issue.updated_at));
			if(TextUtils.isEmpty(issue.body)){
				issueDetailView.issue_body.setVisibility(View.GONE);
			}
			else {
				issueDetailView.issue_body.setVisibility(View.VISIBLE);
				issueDetailView.issue_body.setText(issue.body);
			}
			if(issue.assignee != null) {
				issueDetailView.assignee.setText(issue.assignee.getLogin());
			}
			else{
				issueDetailView.assignee.setText("No one assigned");
			}
			issueDetailView.comments.setText(issue.comments+" comments");
			if(issue.milestone != null) {
				issueDetailView.milestone.setText(issue.milestone.title);
			}
			else{
				issueDetailView.milestone.setText("No milestone");
			}
			if(issue.labels != null && issue.labels.size() > 0){
				String result = "";
				for(Label label : issue.labels){
					result += ","+label.name;
				}
				issueDetailView.label.setText(result.length()>0?result.substring(1):result);
			}
			else{
				issueDetailView.label.setText("No labels");
			}
			issueDetailView.state.setText(issue.state.toString());
			break;
		}
	}

	private class CommentView extends IssueCommentViewHolder implements View.OnClickListener{

		public CommentView(View view) {
			super(view);
			this.user_avatar.setOnClickListener(this);
			this.operation.setOnClickListener(this);
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
	
	private static class IssueDetailView extends IssueDetailViewHolder {
		
		public IssueDetailView(View view) {
			super(view);
		}
	}
}

