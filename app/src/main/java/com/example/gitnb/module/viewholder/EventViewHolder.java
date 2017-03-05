package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.joanzapata.iconify.widget.IconTextView;

public class EventViewHolder extends RecyclerView.ViewHolder{
	public SimpleDraweeView user_avatar;
	public TextView event_action;
	public TextView created_date;
	public TextView description;
	public TextView event_user;
	public TextView event_type;
	public TextView repos_name;
	public IconTextView type_img;
	public TextView event_to;
	
	public EventViewHolder(View view) {
		super(view);
		created_date = (TextView) view.findViewById(R.id.created_date);
		repos_name = (TextView) view.findViewById(R.id.repos_name);
		event_user = (TextView) view.findViewById(R.id.event_user);
		//event_type = (TextView) view.findViewById(R.id.event_type);
		type_img = (IconTextView) view.findViewById(R.id.type_img);
		//event_action = (TextView) view.findViewById(R.id.event_action);
		//event_to = (TextView) view.findViewById(R.id.event_to);
		description = (TextView) view.findViewById(R.id.description);
		user_avatar = (SimpleDraweeView) view.findViewById(R.id.user_avatar);
	}
}
