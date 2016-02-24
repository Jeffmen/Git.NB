package com.example.gitnb.module.repos;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gitnb.R;
import com.example.gitnb.model.Content;
import com.example.gitnb.module.viewholder.RepoContentViewHolder;

public class ReposContentsAdapter extends RecyclerView.Adapter<ViewHolder>{

	private Context mContext;
    private static final int TYPE_HEADER_VIEW = 2;
    private static final int TYPE_NOMAL_VIEW = 0;
    private OnItemClickListener mItemClickListener;
    private OnItemClickListener headClickListener;
    protected final LayoutInflater mInflater;
    private ArrayList<Content> mContents;
    
    
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    
    public ReposContentsAdapter(Context context) {
    	mContext = context;
    	mInflater = LayoutInflater.from(mContext);
	}
    
    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    public void SetOnHeadClickListener(final OnItemClickListener mItemClickListener) {
        this.headClickListener = mItemClickListener;
    }
    
	public Content getItem(int position) {
		if(position == 0){
			return null;
		}
		return mContents == null ? null : mContents.get(position-1);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    
    public void update(ArrayList<Content> data){
    	mContents= data;
    	Collections.sort(mContents);
    	reset();
    }
    
    public void insertAtBack(ArrayList<Content> data){
        if (data != null && data.size() > 0){
        	mContents.addAll(data);
        }
    	reset();
    }

    public void reset(){
        notifyDataSetChanged();
    }
    
	@Override
	public int getItemCount() {
		return mContents == null ? 1 : mContents.size()+1;
	}
	
    @Override
    public int getItemViewType(int position) {
    	if(position == 0){
    		return TYPE_HEADER_VIEW;
    	}
    	return TYPE_NOMAL_VIEW;
    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int viewType) {
		if(viewType == TYPE_HEADER_VIEW){
			View v = mInflater.inflate(R.layout.repo_content_list_head,viewgroup,false);
			return new HeadView(v);
		}
		else{
			View v = mInflater.inflate(R.layout.repo_content_list_item,viewgroup,false);
			return new ReposContentView(v);
		}
	}
	  
	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {		
		switch(getItemViewType(position)){
		case TYPE_HEADER_VIEW:
			HeadView headViewHolder = (HeadView) vh;
			headViewHolder.content_name.setText("Back to the Previous Level");
			headViewHolder.content_name.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
			break;
		case TYPE_NOMAL_VIEW:
			ReposContentView viewHolder = (ReposContentView) vh;
			Content content = getItem(position);
			if(content != null){
			    viewHolder.content_name.setText(content.name);
			    if(content.isDir())
			    	viewHolder.content_type.setImageResource(R.drawable.ic_folder_open_white_48dp);
			    if(content.isFile())
			    	viewHolder.content_type.setImageResource(R.drawable.ic_attachment_white_48dp);
			}
			else{
			    viewHolder.content_name.setText("Back to the Previous Level");
			}
			break;
		}
	}
	
	private class HeadView extends RepoContentViewHolder{
		
		public HeadView(View view) {
			super(view);		
			content_name.setText("Back to the Previous Level");			
			content_name.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
			        if (headClickListener != null) {
			        	headClickListener.onItemClick(v, getLayoutPosition());
			        }
				}
			});
		}
	}
	
	private class ReposContentView extends RepoContentViewHolder{
		
		public ReposContentView(View view) {
			super(view);		
			content_name.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
			        if (mItemClickListener != null) {
			            mItemClickListener.onItemClick(v, getLayoutPosition());
			        }
				}
			});
		}
	}
	
}

