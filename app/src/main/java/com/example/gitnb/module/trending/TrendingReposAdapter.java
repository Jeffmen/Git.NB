package com.example.gitnb.module.trending;

import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gitnb.R;
import com.example.gitnb.model.Repository;
import com.example.gitnb.module.custom.animation.ExpandCollapseAnimation;
import com.example.gitnb.module.custom.view.TagListView;
import com.example.gitnb.module.viewholder.ReposViewHolder;
import com.example.gitnb.module.viewholder.TrendingHeaderViewHolder;
import com.example.gitnb.utils.Utils;

public class TrendingReposAdapter extends RecyclerView.Adapter<ViewHolder>{

	private Context mContext;
	private static final int TYPE_HEADER_VIEW = 0;
    private static final int TYPE_NOMAL_VIEW = 1;
    protected final LayoutInflater mInflater;
    private ArrayList<Repository> mRepos;
    private OnItemClickListener mItemClickListener;
	private TagChangeListener tagChangeListener;
	private LanguageTagAdapter languageTagAdapter;
	private SinceTagAdapter sinceTagAdapter;
	private boolean isLanguageExpand = false;
	private boolean isSinceExpand = false;
	private String languageText, sinceText;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    
    public TrendingReposAdapter(Context context) {
    	mContext = context;
    	mInflater = LayoutInflater.from(mContext);
		languageTagAdapter = new LanguageTagAdapter(mContext);
		sinceTagAdapter = new SinceTagAdapter(mContext);
	}
    
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

	public void setOnTagChangeListener(final TagChangeListener mItemClickListener) {
		this.tagChangeListener = mItemClickListener;
	}
    
	public Repository getItem(int position) {

		return mRepos == null || position == 0 ? null : mRepos.get(position-1);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    
    public void update(ArrayList<Repository> data){
    	mRepos= data;
    	reset();
    }
    
    public void insertAtBack(ArrayList<Repository> data){
        if (data != null && data.size() > 0){
        	mRepos.addAll(data);
        }
    	reset();
    }

    public void reset(){
        notifyDataSetChanged();
    }
    
	@Override
	public int getItemCount() {
		return mRepos == null ? 1 : mRepos.size()+1;
	}
	
    @Override
    public int getItemViewType(int position) {
		if(position == 0){
			return TYPE_HEADER_VIEW;
		}
		else {
			return TYPE_NOMAL_VIEW;
		}
    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int viewType) {
		if(viewType == TYPE_HEADER_VIEW){
			View v = mInflater.inflate(R.layout.trending_header_item,viewgroup,false);
			return new TrendingHeaderView(v);
		}
		else {
			View v = mInflater.inflate(R.layout.repos_list_item,viewgroup,false);
			return new ReposView(v);
		}
	}
	  
	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {		
		switch(getItemViewType(position)){
			case TYPE_HEADER_VIEW:
				TrendingHeaderView trendingHeaderView = (TrendingHeaderView) vh;
				if(isLanguageExpand) {
					trendingHeaderView.tagLanguage.setVisibility(View.VISIBLE);
				}
				else{
					trendingHeaderView.tagLanguage.setVisibility(View.GONE);
				}
				if(isSinceExpand) {
					trendingHeaderView.tagSince.setVisibility(View.VISIBLE);
				}
				else{
					trendingHeaderView.tagSince.setVisibility(View.GONE);
				}
				break;
			case TYPE_NOMAL_VIEW:
				ReposView viewHolder = (ReposView) vh;
				Repository item = getItem(position);
				if(item != null){
					viewHolder.repos_name.setText(item.getName());
					viewHolder.repos_star.setText("Star:"+ Utils.getSoftValue(item.getStargazers_count()));
					viewHolder.repos_fork.setText("owner:"+item.getOwner().getLogin());
					viewHolder.repos_language.setText(item.getLanguage());
					viewHolder.repos_homepage.setText(item.getHomepage());
					viewHolder.repos_discription.setText(item.getDescription());
				}
				viewHolder.user_avatar.setVisibility(View.VISIBLE);
				if(item.getOwner() != null){
					viewHolder.user_avatar.setImageURI(Uri.parse(item.getOwner().getAvatar_url()));
				}
				viewHolder.repos_rank.setText(position+".");
				break;
		}
	}


	private class TrendingHeaderView extends TrendingHeaderViewHolder{

		public TrendingHeaderView(View view) {
			super(view);
			tagLanguage.setAdapter(languageTagAdapter);
			tagSince.setAdapter(sinceTagAdapter);
			language.setText("Language: "+ languageTagAdapter.getSelectedItem());
			since.setText("Since: "+ sinceTagAdapter.getSelectedItem());
			languageText = languageTagAdapter.getSelectedValue();
			sinceText = sinceTagAdapter.getSelectedItem();

			if(tagChangeListener != null){
				tagChangeListener.tagChange(languageText, sinceText);
			}

			language_layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startAnimation(tagLanguage);
					isLanguageExpand = !isLanguageExpand;
					isSinceExpand = false;
				}
			});
			since_layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startAnimation(tagSince);
					isSinceExpand = !isSinceExpand;
					isLanguageExpand = false;
				}
			});
			tagLanguage.setItemClickListener(new TagListView.TagItemClickListener() {
				@Override
				public void itemClick(int position) {
					language.setText("Language: "+ languageTagAdapter.getItem(position));
					languageTagAdapter.setSelectPosition(position);
					languageTagAdapter.notifyDataSetChanged();
					languageText = languageTagAdapter.getItemValue(position).toString();

					startAnimation(tagLanguage);
					isLanguageExpand = false;

					if(tagChangeListener != null){
						tagChangeListener.tagChange(languageText, sinceText);
					}
				}
			});
			tagSince.setItemClickListener(new TagListView.TagItemClickListener() {
				@Override
				public void itemClick(int position) {
					since.setText("Since: "+ sinceTagAdapter.getItem(position));
					sinceTagAdapter.setSelectPosition(position);
					sinceTagAdapter.notifyDataSetChanged();
					sinceText = sinceTagAdapter.getItem(position).toString();

					startAnimation(tagSince);
					isSinceExpand = false;

					if(tagChangeListener != null){
						tagChangeListener.tagChange(languageText, sinceText);
					}
				}
			});
		}

		public void startAnimation(View view) {
			if(isSinceExpand && tagSince != view) {
				ExpandCollapseAnimation localExpandAnimation = new ExpandCollapseAnimation(tagSince, 500);
				tagSince.startAnimation(localExpandAnimation);
			}
			if(isLanguageExpand && tagLanguage != view) {
				ExpandCollapseAnimation localExpandAnimation = new ExpandCollapseAnimation(tagLanguage, 500);
				tagLanguage.startAnimation(localExpandAnimation);
			}
			ExpandCollapseAnimation localExpandAnimation = new ExpandCollapseAnimation(view, 500);
			view.startAnimation(localExpandAnimation);
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

	public interface TagChangeListener {
		void tagChange(String language, String since);
	}
}

