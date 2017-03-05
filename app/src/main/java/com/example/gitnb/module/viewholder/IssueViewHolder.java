package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class IssueViewHolder extends RecyclerView.ViewHolder{
	public SimpleDraweeView user_avatar;
	public TextView issue_rank;
	public TextView title;
	public TextView state;
	public TextView comments;
	public TextView assignee;
	public TextView updated_at;

	public IssueViewHolder(View view) {
		super(view);
		user_avatar = (SimpleDraweeView) view.findViewById(R.id.user_avatar);
		issue_rank = (TextView) view.findViewById(R.id.issue_rank);
		title = (TextView) view.findViewById(R.id.title);
		state = (TextView) view.findViewById(R.id.state);
		comments = (TextView) view.findViewById(R.id.comments);
		assignee = (TextView) view.findViewById(R.id.assignee);
		updated_at = (TextView) view.findViewById(R.id.updated_at);
	}
}
