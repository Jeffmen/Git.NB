package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class ReposDetailViewHolder extends RecyclerView.ViewHolder{
	public TextView repos_name;
	public TextView repos_owner;
	public TextView repos_updated;
	public TextView repos_homepage;
	public TextView repos_discription;
	public SimpleDraweeView user_avatar;
	
	public ReposDetailViewHolder(View view) {
		super(view);
		repos_name = (TextView) view.findViewById(R.id.repos_name);
		repos_owner = (TextView) view.findViewById(R.id.repos_owner);
    	repos_updated = (TextView) view.findViewById(R.id.repos_updated);
		repos_homepage = (TextView) view.findViewById(R.id.repos_homepage);
		repos_discription = (TextView) view.findViewById(R.id.repos_description);
		user_avatar = (SimpleDraweeView) view.findViewById(R.id.user_avatar);
	}
}
