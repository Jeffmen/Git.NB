package com.example.gitnb.module.user;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gitnb.R;
import com.example.gitnb.model.User;
import com.example.gitnb.module.custom.ExpandAnimation;
import com.example.gitnb.module.repos.ReposListActivity;
import com.example.gitnb.module.viewholder.LoadMoreViewHolder;
import com.example.gitnb.module.viewholder.SearchViewHolder;
import com.example.gitnb.module.viewholder.UserViewHolder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

public class UserListAdapter extends RecyclerView.Adapter<ViewHolder>{

	private Context mContext;
    private static final int TYPE_HEADER_VIEW = 2;
    private static final int TYPE_FOOTER_VIEW = 1;
    private static final int TYPE_NOMAL_VIEW = 0;
    private static final int PAGE_COUNT = 30;
    protected final LayoutInflater mInflater;
    private ArrayList<User> mUsers;
    private OnItemClickListener mItemClickListener;
    private OnItemClickListener mLoadMoreClickListener;
    private OnItemClickListener mSearchClickListener;
    private boolean isShowLoadMore = false;
    private boolean isShowSearch = false;
    private boolean isLoadingMore = false;
    private String searchText = "";
	private ArrayList openPosition;
	private int maxContentWidth;
	private int fun = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    
    public UserListAdapter(Context context) {
    	mContext = context;
		fun = new Random().nextInt(100);
    	mInflater = LayoutInflater.from(mContext);
		openPosition = new ArrayList();
		maxContentWidth = (int)(mContext.getResources().getDisplayMetrics().widthPixels
				- 2.0F * mContext.getResources().getDimension(R.dimen.card_padding_horizontal)
				- 2.0F * mContext.getResources().getDimension(R.dimen.card_padding));
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
    
	public User getItem(int position) {
		if(isShowSearch && position == 0){
			return null;
		}
		if(isShowLoadMore && position == getItemCount()-1){
			return null;
		}
		return mUsers == null ? null : mUsers.get(position-(isShowSearch?1:0));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    
    public void update(ArrayList<User> data){
		isShowLoadMore = true;
    	if(data == null || data.size()<PAGE_COUNT){
    		isShowLoadMore = false;
    	}  
        mUsers= data;
    	reset();
    }
    
    public void insertAtBack(ArrayList<User> data){
    	if(isShowLoadMore){
	    	if(data == null || data.size()<PAGE_COUNT){
	    		isShowLoadMore = false;
	    	}
	    	else{
	    		isShowLoadMore = true;
	    	}
    	}
        if (data != null && data.size() > 0){
        	mUsers.addAll(data);
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
		if(mUsers == null){
			return 0 + other;
		}
		else {
			return mUsers.size() + other;
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
			View v = mInflater.inflate(R.layout.user_list_item,viewgroup,false);
			return new UserView(v);
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
			break;
		case TYPE_NOMAL_VIEW:
			UserView viewHolder = (UserView) vh;
			User user = getItem(position);
			if(user != null){
			    viewHolder.ivAvatar.setImageURI(Uri.parse(user.getAvatar_url()));
				viewHolder.tvLogin.setText(user.getLogin());
				if(position == fun){
					viewHolder.expandableContent.setVisibility(View.VISIBLE);
					viewHolder.more.setVisibility(View.VISIBLE);
					initExpendContentView(viewHolder, user, position);
				}
				else{
					viewHolder.expandableContent.setVisibility(View.GONE);
					viewHolder.more.setVisibility(View.GONE);
				}
			}
			viewHolder.tvRank.setText(String.valueOf(isShowSearch?position:position+1)+".");
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

	private void initExpendContentView(UserView viewHolder, User user, int position){
		TextView contentView = new TextView(mContext);
		contentView.setText("\n       Have some fun... \n");
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		viewHolder.expandableContent.removeAllViews();
		//params.setMargins(0, 0, 0, bottom);
		viewHolder.expandableContent.addView(contentView, params);

		RelativeLayout.LayoutParams relParams = (RelativeLayout.LayoutParams) viewHolder.expandableContent.getLayoutParams();
		if(openPosition.contains(String.valueOf(position))){
			viewHolder.expandableContent.setVisibility(View.VISIBLE);
			viewHolder.buttonLearnMore.setText("Hide");
			relParams.setMargins(relParams.leftMargin, relParams.topMargin, relParams.rightMargin, 0);
		}
		else{
			viewHolder.expandableContent.setVisibility(View.GONE);
			viewHolder.buttonLearnMore.setText("Learn more");
			int j = View.MeasureSpec.makeMeasureSpec(maxContentWidth, View.MeasureSpec.EXACTLY);
			int k = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			viewHolder.expandableContent.measure(j, k);
			relParams.setMargins(relParams.leftMargin, relParams.topMargin, relParams.rightMargin, 0 - viewHolder.expandableContent.getMeasuredHeight());
		}
		viewHolder.expandableContent.setLayoutParams(relParams);
	}

	private class UserView extends UserViewHolder implements View.OnClickListener{
        public ImageButton buttonLike;
		public Button buttonLearnMore;
		public ImageButton buttonSettings;
		public final LinearLayout expandableContent;
		public final RelativeLayout more;
		public UserView(View view) {
			super(view);
			more  = (RelativeLayout) view.findViewById(R.id.more);
			expandableContent = (LinearLayout) view.findViewById(R.id.expandableContent);
			buttonLearnMore = (Button) view.findViewById(R.id.buttonLearnMore);
			buttonLike = (ImageButton) view.findViewById(R.id.buttonLike);
			buttonSettings = (ImageButton) view.findViewById(R.id.buttonSettings);
			buttonLearnMore.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View paramAnonymousView)
				{
					ExpandAnimation localExpandAnimation = new ExpandAnimation(expandableContent, 1000);
					expandableContent.startAnimation(localExpandAnimation);
					if(openPosition.contains(String.valueOf(getLayoutPosition()))){
						buttonLearnMore.setText("Learn more");
						openPosition.remove(String.valueOf(getLayoutPosition()));
					}
					else {
						buttonLearnMore.setText("Hide");
						openPosition.add(String.valueOf(getLayoutPosition()));
					}
				}
			});
			buttonLike.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View view) {
					buttonLike.setSelected(!buttonLike.isSelected());
				}
			});
			buttonSettings.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					Intent intent = new Intent(mContext, ReposListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelable(HotUserFragment.USER, getItem(getAdapterPosition()));
					intent.putExtras(bundle);
					intent.putExtra(ReposListActivity.REPOS_TYPE, ReposListActivity.REPOS_TYPE_USER);
					mContext.startActivity(intent);
				}
			});
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

