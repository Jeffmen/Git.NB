package com.example.gitnb.module.repos;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gitnb.R;
import com.example.gitnb.model.Repository;
import com.example.gitnb.model.User;
import com.example.gitnb.module.user.ImageShowerActivity;
import com.example.gitnb.module.user.UserDetailActivity;
import com.example.gitnb.module.viewholder.OperationViewHolder;
import com.example.gitnb.module.viewholder.ReposDetailViewHolder;
import com.example.gitnb.module.viewholder.UserDetailViewHolder;
import com.example.gitnb.utils.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ReposOperationAdapter extends RecyclerView.Adapter<ViewHolder>{

	private Context mContext;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	public static final int TYPE_REPOS_DETAIL_VIEW = 0;
    public static final int TYPE_READ_ME_VIEW = 1;
    public static final int TYPE_CONTRIBUTORS_VIEW = 2;
    public static final int TYPE_EVENTS_VIEW = 3;
    public static final int TYPE_SOURCES_VIEW = 4;
	public static final int TYPE_ISSUE_VIEW = 5;
	public static final int TYPE_STARGAZERS_VIEW = 6;
    private OnItemClickListener mItemClickListener;
    protected final LayoutInflater mInflater;
	private int iconPrimaryColor = -1;
    private Repository repos;

	private static final String[] icon_id = {"",
			"{fe-readme}",
			"{fe-contributor}",
			"{fe-event}",
			"{fe-source}",
			"{fe-issue}"
	};

	private static final int[] name_id = {-1,
			//R.string.stargazers_title,
			R.string.read_me_title,
			R.string.contributors_title,
			R.string.events_title,
			R.string.sources_title,
			R.string.issue_title};


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public ReposOperationAdapter(Context context, Repository repos) {
    	mContext = context;
        this.repos = repos;
    	mInflater = LayoutInflater.from(mContext);
		iconPrimaryColor = Color.parseColor("#EAEAEA");
	}

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void updateReposotory(Repository value){
		repos = value;
        notifyDataSetChanged();
    }

	public void setIconPrimaryColor(int color){
		this.iconPrimaryColor = color;
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return name_id.length;
	}
	
    @Override
    public int getItemViewType(int position) {
    	return position;
    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int viewType) {
		if(viewType == TYPE_REPOS_DETAIL_VIEW){
			View v = mInflater.inflate(R.layout.repos_detail_item,viewgroup,false);
			return new ReposDetailView(v);
		}
		else{
			View v = mInflater.inflate(R.layout.operation_list_item,viewgroup,false);
			return new OperationView(v);
		}
	}
	  
	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {
        if(repos == null) return;
		if(getItemViewType(position) == TYPE_REPOS_DETAIL_VIEW){
			ReposDetailView viewHolder = (ReposDetailView) vh;
			viewHolder.repos_updated.setText("Updated " + Utils.getTimeFromNow(repos.getUpdated_at()));
			if(!TextUtils.isEmpty(repos.getHomepage())) {
				viewHolder.repos_homepage.setText(repos.getHomepage());
			}
			else{
				viewHolder.repos_homepage.setVisibility(View.GONE);
			}
			if(!TextUtils.isEmpty(repos.getDescription())) {
				viewHolder.repos_discription.setText(repos.getDescription());
			}
			else{
				viewHolder.repos_discription.setVisibility(View.GONE);
			}

			viewHolder.type.setText(repos.is_private() ? "Private" : "Public");
			viewHolder.stars.setText(Utils.getSoftValue(repos.getStargazers_count()));
			viewHolder.created_date.setText(format.format(Utils.getDate(repos.getCreated_at())));
			viewHolder.language.setText(TextUtils.isEmpty(repos.getLanguage())?"No language":repos.getLanguage());
			viewHolder.forks.setText(Utils.getSoftValue(repos.getForks_count()));
			viewHolder.size.setText((float)((repos.getSize()/1024*100))/100+"M");
		}
		else {
			OperationView viewHolder = (OperationView) vh;
			viewHolder.operation_icon.setText(icon_id[position]);
            String showName = mContext.getResources().getString(name_id[position]);
			viewHolder.operation_name.setText(showName);
			switch (getItemViewType(position)) {
				case TYPE_STARGAZERS_VIEW:
					viewHolder.operation_value.setText(String.valueOf(repos.getStargazers_count()));
					break;
				default:
			}
            if(position == 1){
                viewHolder.top_divider.setVisibility(View.VISIBLE);
                viewHolder.bottom_short_divider.setVisibility(View.VISIBLE);
            }
            else if(position == getItemCount()-1){
                viewHolder.bottom_short_divider.setVisibility(View.GONE);
                viewHolder.bottom_divider.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.bottom_short_divider.setVisibility(View.VISIBLE);
                viewHolder.bottom_divider.setVisibility(View.GONE);
            }
			//viewHolder.operation_icon.setColorFilter(iconPrimaryColor, PorterDuff.Mode.SRC_IN);
        }
	}



	private class OperationView extends OperationViewHolder implements View.OnClickListener{

		public OperationView(View view) {
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

	private class ReposDetailView extends ReposDetailViewHolder{

		public ReposDetailView(View view) {
			super(view);
		}
	}
}

