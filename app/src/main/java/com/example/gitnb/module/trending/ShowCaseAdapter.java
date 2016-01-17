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
import com.example.gitnb.model.ShowCase;
import com.example.gitnb.module.viewholder.ShowCaseViewHolder;

public class ShowCaseAdapter extends RecyclerView.Adapter<ViewHolder>{

	private Context mContext;
    private static final int TYPE_NOMAL_VIEW = 0;
    protected final LayoutInflater mInflater;
    private ArrayList<ShowCase> showcaes;
    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    
    public ShowCaseAdapter(Context context) {
    	mContext = context;
    	mInflater = LayoutInflater.from(mContext);
	}
    
    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
	public ShowCase getItem(int position) {

		return showcaes == null ? null : showcaes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    
    public void update(ArrayList<ShowCase> data){
    	showcaes= data;
    	reset();
    }
    
    public void insertAtBack(ArrayList<ShowCase> data){
        if (data != null && data.size() > 0){
        	showcaes.addAll(data);
        }
    	reset();
    }

    public void reset(){
        notifyDataSetChanged();
    }
    
	@Override
	public int getItemCount() {
		return showcaes == null ? 0 : showcaes.size();
	}
	
    @Override
    public int getItemViewType(int position) {
    	return TYPE_NOMAL_VIEW;
    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int viewType) {
		View v = mInflater.inflate(R.layout.showcase_list_item,viewgroup,false);
		return new ShowCaseView(v);
	}
	  
	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {		
		switch(getItemViewType(position)){
		case TYPE_NOMAL_VIEW:
			ShowCaseView viewHolder = (ShowCaseView) vh;
			ShowCase item = getItem(position);
			if(item != null){
				viewHolder.showcase_name.setText(item.name);
				viewHolder.showcase_discription.setText(item.description);
				viewHolder.showcase_avatar.setImageURI(Uri.parse(item.image_url));
			}
			break;
		}
	}
	
	
	private class ShowCaseView extends ShowCaseViewHolder implements View.OnClickListener{
		
		public ShowCaseView(View view) {
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

