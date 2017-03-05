package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.joanzapata.iconify.widget.IconTextView;

public class IssueCommentViewHolder extends RecyclerView.ViewHolder{
	public LinearLayout operation;
	public SimpleDraweeView user_avatar;
	public TextView comment_rank;
	public TextView user_login;
	public TextView updated_at;
	public TextView comment;
	public IconTextView edit;
	public IconTextView delete;
	public View ownerLabel;

	public IssueCommentViewHolder(View view) {
		super(view);
		user_avatar = (SimpleDraweeView) view.findViewById(R.id.user_avatar);
		comment_rank = (TextView) view.findViewById(R.id.comment_rank);
		user_login = (TextView) view.findViewById(R.id.user_login);
		updated_at = (TextView) view.findViewById(R.id.updated_at);
		comment = (TextView) view.findViewById(R.id.comment);
		operation = (LinearLayout) view.findViewById(R.id.operation);
		delete = (IconTextView) view.findViewById(R.id.delete);
		edit = (IconTextView) view.findViewById(R.id.edit);
		ownerLabel = view.findViewById(R.id.ownerLabel);
	}
}
