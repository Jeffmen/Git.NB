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
import com.example.gitnb.module.viewholder.ReposViewHolder;

public class TrendingReposAdapter extends RecyclerView.Adapter<ViewHolder>{

	private Context mContext;
    private static final int TYPE_NOMAL_VIEW = 0;
    protected final LayoutInflater mInflater;
    private ArrayList<Repository> mRepos;
    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    
    public TrendingReposAdapter(Context context) {
    	mContext = context;
    	mInflater = LayoutInflater.from(mContext);
	}
    
    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
	public Repository getItem(int position) {

		return mRepos == null ? null : mRepos.get(position);
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
		return mRepos == null ? 0 : mRepos.size();
	}
	
    @Override
    public int getItemViewType(int position) {
    	return TYPE_NOMAL_VIEW;
    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int viewType) {
		View v = mInflater.inflate(R.layout.repos_list_item,viewgroup,false);
		return new ReposView(v);
	}
	  
	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {		
		switch(getItemViewType(position)){
		case TYPE_NOMAL_VIEW:
			ReposView viewHolder = (ReposView) vh;
			Repository item = getItem(position);
			if(item != null){
				viewHolder.repos_name.setText(item.getName());
				viewHolder.repos_star.setText("Star:"+item.getStargazers_count());
				viewHolder.repos_fork.setText("owner:"+item.getOwner().getLogin());
				viewHolder.repos_language.setText(item.getLanguage());
				viewHolder.repos_homepage.setText(item.getHomepage());
				viewHolder.repos_discription.setText(item.getDescription());
			}
			viewHolder.user_avatar.setVisibility(View.VISIBLE);
			if(item.getOwner() != null){
			    viewHolder.user_avatar.setImageURI(Uri.parse(item.getOwner().getAvatar_url()));
			}
			viewHolder.repos_rank.setText(String.valueOf(position+1)+".");
			break;
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
}

