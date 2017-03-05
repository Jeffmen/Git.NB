package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.gitnb.R;

public class IssueDetailViewHolder extends RecyclerView.ViewHolder{
	public TextView issue_updated;
	public TextView issue_body;
	public TextView state;
	public TextView comments;
	public TextView assignee;
	public TextView label;
	public TextView milestone;

	public IssueDetailViewHolder(View view) {
		super(view);
		issue_updated = (TextView) view.findViewById(R.id.issue_updated);
		issue_body = (TextView) view.findViewById(R.id.issue_body);
		state = (TextView) view.findViewById(R.id.state);
		comments = (TextView) view.findViewById(R.id.comments);
		assignee = (TextView) view.findViewById(R.id.assignee);
		label = (TextView) view.findViewById(R.id.label);
		milestone = (TextView) view.findViewById(R.id.milestone);
	}
}
