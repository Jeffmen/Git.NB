package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class UserViewHolder extends RecyclerView.ViewHolder{
	public TextView tvLogin;
	public TextView tvRank;
	public SimpleDraweeView ivAvatar;

	public UserViewHolder(View view) {
		super(view);
		ivAvatar = (SimpleDraweeView) view.findViewById(R.id.user_avatar);
        tvLogin = (TextView) view.findViewById(R.id.user_login);
        tvRank = (TextView) view.findViewById(R.id.user_rank);
	}
}
