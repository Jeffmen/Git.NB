package com.example.gitnb.module.repos;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
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
import com.example.gitnb.module.user.HotUserFragment;
import com.example.gitnb.module.user.UserDetailActivity;
import com.example.gitnb.module.viewholder.EventViewHolder;
import com.example.gitnb.module.viewholder.LoadMoreViewHolder;
import com.example.gitnb.module.viewholder.SearchViewHolder;
import com.example.gitnb.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

public class EventListAdapter extends RecyclerView.Adapter<ViewHolder>{

    //maven { url 'http://repo1.maven.org/maven2' }
	private Context mContext;
    private static final int TYPE_HEADER_VIEW = 2;
    private static final int TYPE_FOOTER_VIEW = 1;
    private static final int TYPE_NOMAL_VIEW = 0;
    private static final int PAGE_COUNT = 30;
    protected final LayoutInflater mInflater;
    private ArrayList<Event> events;
    private OnItemClickListener mItemClickListener;
    private OnItemClickListener mLoadMoreClickListener;
    private OnItemClickListener mSearchClickListener;
    private boolean isShowLoadMore = false;
    private boolean isShowSearch = false;
    private boolean isLoadingMore = false;
    private String searchText = "";

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    
    public EventListAdapter(Context context) {
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
    
	public Event getItem(int position) {
		if(isShowSearch && position == 0){
			return null;
		}
		if(isShowLoadMore && position == getItemCount()-1){
			return null;
		}
		return events == null ? null : events.get(position-(isShowSearch?1:0));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    
    public void update(ArrayList<Event> data){
		isShowLoadMore = true;
    	if(data == null || data.size()<PAGE_COUNT){
    		isShowLoadMore = false;
    	}
    	events= data;
    	reset();
    }
    
    public void insertAtBack(ArrayList<Event> data){
    	if(data == null || data.size()<PAGE_COUNT){
    		isShowLoadMore = false;
    	}
    	else{
    		isShowLoadMore = true;
    	}
        if (data != null && data.size() > 0){
        	events.addAll(data);
        }
    	reset();
    }

    public void reset(){
    	this.isLoadingMore = false;
        notifyDataSetChanged();
    }
    
	@Override
	public int getItemCount() {
		int orther = 0;
		if(isShowLoadMore) orther++;
		if(isShowSearch) orther++;
		if(events == null){
			return 0 + orther;
		}
		else {
			return events.size() + orther;
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
			View v = mInflater.inflate(R.layout.search,viewgroup,false);
			return new SearchView(v);
		}
		else{
			View v = mInflater.inflate(R.layout.event_list_item,viewgroup,false);
			return new EventView(v);
		}
	}
	//https://api.github.com/users/jeffmen/received_events
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
			EventView viewHolder = (EventView) vh;
			Event item = getItem(position);
			if(item != null){
				viewHolder.type_img.setBackgroundResource(R.drawable.ic_chevron_right_white_18dp);
				int hours = Utils.fromNow(item.created_at);
				int days = hours/24;
				int months = days/30;
				int years = months/12;
				if(years == 1){
					viewHolder.created_date.setText(years+" year ago");
				}
				else if(years > 1){
					viewHolder.created_date.setText(years+" years ago");
				}
				else if(months == 1){
					viewHolder.created_date.setText(months+" month ago");
				}
				else if(months > 1){
					viewHolder.created_date.setText(months+" months ago");
				}
				else if(days == 1){
					viewHolder.created_date.setText(days+" day ago");
				}
				else if(days > 1){
					viewHolder.created_date.setText(days+" days ago");
				}
				else if(hours > 1){
					viewHolder.created_date.setText(hours + " hours ago");
				}
				else{
					viewHolder.created_date.setText(hours + " hour ago");
				}
				//viewHolder.event_user.setText(item.actor.getLogin());
				//viewHolder.description.setText(item.payload.issue.title);
				//viewHolder.event_type.setText(getTypeString(item.getType()));
				setSpanString(viewHolder, item, position);
			}
			viewHolder.user_avatar.setVisibility(View.VISIBLE);
			if(item.actor != null){
			    viewHolder.user_avatar.setImageURI(Uri.parse(item.actor.getAvatar_url()));
			}
			break;
		case TYPE_HEADER_VIEW:
			SearchView searchHolder = (SearchView) vh;
			if(searchText != null && !searchText.isEmpty()){
				searchHolder.search_text.setText(searchText.toCharArray(), 0, searchText.length());
				searchHolder.clear_button.setVisibility(View.VISIBLE);
			}
			else{
				searchHolder.clear_button.setVisibility(View.INVISIBLE);
			}
			//searchHolder.search_text.setEnabled(!isSearching);
			break;
		}
	}
	
	//https://api.github.com/repos/elastic/elasticsearch/events
	private void setSpanString(EventView viewHolder, Event item, int position){
        viewHolder.event_user.setText(createUserSpan(item.actor.getLogin(), position));
		switch (item.type) {
		case WatchEvent:
			viewHolder.event_user.append(" starred ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText("");
			break;
		case CreateEvent:
			viewHolder.event_user.append(" created repository ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText("");
			break;
		case CommitCommentEvent:
			viewHolder.event_user.append(" commented on ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText(item.payload.comment.body);
			break;
		case ForkEvent:
			viewHolder.event_user.append(" forked ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.event_user.append(" to ");
			viewHolder.event_user.append(createReposSpan(item.payload.forkee.getFull_name(), position));
			viewHolder.description.setText("");
			break;
		case GollumEvent:
			viewHolder.event_user.append(" created wiki page on ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText("");
			//viewHolder.description.setText(item.payload.pages.get(0).html_url
			break;
		case IssueCommentEvent:
			viewHolder.event_user.append(" commented on issue ");
			viewHolder.event_user.append("#" + String.valueOf(item.payload.issue.number));
			viewHolder.event_user.append(" in ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText(item.payload.comment.body);
			break;
		case IssuesEvent:
			viewHolder.event_user.append(" " + item.payload.action + " issue ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText(item.payload.issue.title);
			break;
		case MemberEvent:
			viewHolder.event_user.append(" added ");
			viewHolder.event_user.append(createUserSpan(item.payload.member.getLogin(), position));
			viewHolder.event_user.append(" as collaborator to ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText("");
			break;
		case MembershipEvent:
			viewHolder.event_user.append(" " + item.payload.action + " ");
			viewHolder.event_user.append(createUserSpan(item.payload.member.getLogin(), position));
			viewHolder.event_user.append(" to ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText("");
			break;
		case PublicEvent:
			viewHolder.event_user.append(" public ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText("");
			//showText += " public "+ item.payload.repository.getName();
			break;
		case PullRequestEvent:
			viewHolder.event_user.append(" " + item.payload.action + " pull request ");
			viewHolder.event_user.append(String.valueOf(item.payload.pull_request.number));
			viewHolder.event_user.append(" " + createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText(item.payload.pull_request.title);
			break;
		case PullRequestReviewCommentEvent:
			viewHolder.event_user.append(" commented on pull request in ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText(item.payload.comment.body);
			break;
		case PushEvent:
			viewHolder.event_user.append(" pushed to ");
			viewHolder.event_user.append(item.payload.ref);
			viewHolder.event_user.append(" at ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText(item.payload.commits.get(0).message);
			break;
		case StatusEvent:
			break;
		case TeamAddEvent:
			viewHolder.event_user.append(" is added " + item.payload.team.name + " to " );
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText("");
			break;
		case DeleteEvent:
			viewHolder.event_user.append(" deleted ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText("");
			break;
		case ReleaseEvent:
			viewHolder.event_user.append(" released ");
			viewHolder.event_user.append(createReposSpan(item.payload.repository.getName(), position));
			viewHolder.description.setText("");
			break;
		default:
			viewHolder.event_user.append(" starred ");
			viewHolder.event_user.append(createReposSpan(item.repo.getName(), position));
			viewHolder.description.setText("");
			break;
		}
	}

    private SpannableString createUserSpan(String showText, final int position){

        SpannableString spanString = new SpannableString(showText); 
        spanString.setSpan(showText, 0, showText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);                
        spanString.setSpan(new ClickableSpan() {
             
            @Override
            public void onClick(View widget) {

				Intent intent = new Intent(mContext, UserDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotUserFragment.USER, getItem(position).actor);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
            }

			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setColor(mContext.getResources().getColor(android.R.color.holo_blue_light));
				ds.setUnderlineText(true);
				ds.clearShadowLayer();
			}
			
        }, 0, showText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	return spanString;
    }
    
    private SpannableString createReposSpan(String showText, final int position){

        SpannableString spanString = new SpannableString(showText); 
        spanString.setSpan(showText, 0, showText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);                
        spanString.setSpan(new ClickableSpan() {
             
            @Override
            public void onClick(View widget) {

				Intent intent = new Intent(mContext, ReposDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(HotReposFragment.REPOS, getItem(position).repo);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
            }

			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setColor(mContext.getResources().getColor(android.R.color.holo_blue_light));
				ds.setUnderlineText(true);
				ds.clearShadowLayer();
			}
			
        }, 0, showText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	return spanString;
    }
    
	private class EventView extends EventViewHolder implements View.OnClickListener{
		
		public EventView(View view) {
			super(view);
            view.setOnClickListener(this);
			event_user.setMovementMethod(LinkMovementMethod.getInstance());
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
	
	private class SearchView extends SearchViewHolder{
		
		public SearchView(View view) {
			super(view);
			search_icon.setOnClickListener( new View.OnClickListener(){
	            public void onClick(View v) {
	    	        if (mSearchClickListener != null) {
	    	        	mSearchClickListener.onItemClick(v, getLayoutPosition());
	    	        }
	            }           
	        });      
			clear_button.setOnClickListener( new View.OnClickListener(){
	            public void onClick(View v) {
	            	search_text.setText("");
	            	searchText = "";
	            	clear_button.setVisibility(View.GONE);
	    	        if (mSearchClickListener != null) {
	    	        	mSearchClickListener.onItemClick(v, getLayoutPosition());
	    	        }
	            }           
	        });
			search_text.setOnKeyListener(new OnKeyListener(){

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_ENTER){
		    	        if (mSearchClickListener != null) {
		    	        	mSearchClickListener.onItemClick(v, getLayoutPosition());
		    	        }
		    	        return true;
					}
					return false;
				}
				
			});
			search_text.addTextChangedListener(new TextWatcher(){

				@Override
				public void afterTextChanged(Editable arg0) {
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count){		
					if(s.length() > 0){
						clear_button.setVisibility(View.VISIBLE);
						searchText = s.toString();
					}
				}
				
			});
		}
	}
}

