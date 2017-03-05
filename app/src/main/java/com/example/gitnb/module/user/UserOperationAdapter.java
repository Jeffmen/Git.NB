package com.example.gitnb.module.user;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gitnb.R;
import com.example.gitnb.model.User;
import com.example.gitnb.module.viewholder.OperationViewHolder;
import com.example.gitnb.module.viewholder.UserDetailViewHolder;
import com.example.gitnb.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserOperationAdapter extends RecyclerView.Adapter<ViewHolder>{

	private Context mContext;
	private SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
	public static final int TYPE_USER_DETAIL_VIEW = 0;
    public static final int TYPE_EVENTS_VIEW = 1;
    public static final int TYPE_ORGANIZATIONS_VIEW = 2;
	public static final int TYPE_STARRED_VIEW = 3;
    public static final int TYPE_FOLLOWERS_VIEW = 4;
    public static final int TYPE_FOLLOWING_VIEW = 5;
    public static final int TYPE_REPOSITORIES_VIEW = 6;
    private OnItemClickListener mItemClickListener;
    protected final LayoutInflater mInflater;
    private User user;

	private static final String[] icon_id = {"",
			"{fe-event}",
			"{fe-organization}",
			"{fe-star}",
			"{fe-follower}",
			"{fe-following}",
			"{fe-repos}",
	};
	private static final int[] name_id = {-1,
			R.string.events_title,
			R.string.organizations_title,
			R.string.starred_title,
			R.string.followers_title,
			R.string.following_title,
			R.string.repositories_title};


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public UserOperationAdapter(Context context, User user) {
    	mContext = context;
        this.user = user;
    	mInflater = LayoutInflater.from(mContext);
	}

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void updateUser(User value){
        user = value;
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
		if(viewType == TYPE_USER_DETAIL_VIEW){
			View v = mInflater.inflate(R.layout.user_detail_item,viewgroup,false);
			return new UserDetailView(v);
		}
		else{
			View v = mInflater.inflate(R.layout.operation_list_item,viewgroup,false);
			return new OperationView(v);
		}
	}
	  
	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {
        if(user == null) return;
		if(getItemViewType(position) == TYPE_USER_DETAIL_VIEW){
			UserDetailView viewHolder = (UserDetailView) vh;
			if(user.getLocation()!=null && !user.getLocation().isEmpty()){
				viewHolder.user_location.setText(user.getLocation());
			}
			else{
				viewHolder.user_location.setText("No location");
			}
			if(user.getEmail()!=null && !user.getEmail().isEmpty()){
				viewHolder.user_email.setText(user.getEmail());
			}
			else {
				viewHolder.user_email.setText("No email");
			}
			if(user.getCompany()!=null && !user.getCompany().isEmpty()){
				viewHolder.user_company.setText(user.getCompany());
			}
			else{
				viewHolder.user_company.setText("No company");
			}
			if(user.getBlog()!=null && !user.getBlog().isEmpty()){
				viewHolder.user_blog.setText(user.getBlog());
			}
			else{
				viewHolder.user_blog.setText("No Blog");
			}
			viewHolder.user_created_date.setText("Joined on "+format.format(Utils.getDate(user.getCreated_at())));
		}
		else {
			OperationView viewHolder = (OperationView) vh;
			viewHolder.operation_icon.setText(icon_id[position]);
            String showName = mContext.getResources().getString(name_id[position]);
			viewHolder.operation_name.setText(showName);
			switch (getItemViewType(position)) {
				case TYPE_FOLLOWERS_VIEW:
				case TYPE_FOLLOWING_VIEW:
				case TYPE_REPOSITORIES_VIEW:
					GradientDrawable drawable = new GradientDrawable();
					drawable.setCornerRadius(10);
					//drawable.setStroke(1, Color.parseColor("#e0e0e0"));
					drawable.setColor(Color.parseColor("#D32F2F"));
					viewHolder.operation_value.setBackground(drawable);
					viewHolder.operation_value.setText(String.valueOf(user.getPublic_repos()));
					viewHolder.operation_value.setVisibility(View.GONE);
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

	private class UserDetailView extends UserDetailViewHolder{

		public UserDetailView(View view) {
			super(view);
		}
	}


}

