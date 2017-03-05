package com.example.gitnb.module.repos;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gitnb.R;
import com.example.gitnb.model.Content;
import com.example.gitnb.module.viewholder.RepoContentViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReposPathAdapter extends RecyclerView.Adapter<ViewHolder>{

	private Context mContext;
	private static final int TYPE_NORMAL_VIEW = 0;
    private OnItemClickListener mItemClickListener;
    protected final LayoutInflater mInflater;
    private ArrayList<String> mPaths;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public ReposPathAdapter(Context context, String reposName) {
    	mContext = context;
        mPaths = new ArrayList<>();
        mPaths.add(reposName);
    	mInflater = LayoutInflater.from(mContext);
	}
    
    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
	public String getItem(int position) {
		return mPaths == null ? null : mPaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    
    public void update(ArrayList<String> data){
		mPaths= data;
    	reset();
    }
    
    public void insertAtBack(ArrayList<String> data){
        if (data != null && data.size() > 0){
			mPaths.addAll(data);
        }
    	reset();
    }

    public void insertAtBack(String value){
        mPaths.add(value);
        notifyItemInserted(getItemCount() - 1);
        // if you want trigger the item animation,
        //do not call notifyDataSetChanged() function
    }

    public String getPathString(){
        StringBuilder result = new StringBuilder();
        for(int i=1; i< mPaths.size(); i++){
            result.append(mPaths.get(i));
            if(i!=mPaths.size()-1){
                result.append("/");
            }
        }
        return result.toString();
    }

    public void reset(){
        notifyDataSetChanged();
    }

	public boolean isRoot(){
		return mPaths.size() >1;
	}

	public void goPrevious(){
		mPaths = new ArrayList<>(mPaths.subList(0, mPaths.size()-1));
		reset();
	}
    
	@Override
	public int getItemCount() {
		return mPaths == null ? 0 : mPaths.size();
	}
	
    @Override
    public int getItemViewType(int position) {
    	return TYPE_NORMAL_VIEW;
    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int viewType) {
		View v = mInflater.inflate(R.layout.path_list_item,viewgroup,false);
		return new ReposPathView(v);
	}
	  
	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {		
		switch(getItemViewType(position)){
		case TYPE_NORMAL_VIEW:
			ReposPathView viewHolder = (ReposPathView) vh;
			viewHolder.path_name.setText(Html.fromHtml("<u>" + getItem(position) + "</u>"));
            if(position == 0) {
                viewHolder.path_seperator.setVisibility(View.GONE);
            }
            else{
                viewHolder.path_seperator.setVisibility(View.VISIBLE);
            }

			break;
		}
	}
	
	private class ReposPathView extends RecyclerView.ViewHolder{

		public TextView path_name;
        public TextView path_seperator;

		public ReposPathView(View view) {
			super(view);
			path_name = (TextView) view.findViewById(R.id.path_name);
            path_seperator = (TextView) view.findViewById(R.id.path_seperator);
			view.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
			        if (getLayoutPosition() != getItemCount()-1 && mItemClickListener != null) {
                        mPaths = new ArrayList<>(mPaths.subList(0, getLayoutPosition()+1));
						reset();
			            mItemClickListener.onItemClick(v, getLayoutPosition());
			        }
				}
			});
		}
	}
	
}

