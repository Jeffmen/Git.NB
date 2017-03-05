package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.joanzapata.iconify.widget.IconTextView;

public class NotificationViewHolder extends RecyclerView.ViewHolder{

	public TextView created_date;
	public TextView repos_name;
	public TextView notification_title;
	//public TextView repos_rank;
	public IconTextView notification_type;

	public NotificationViewHolder(View view) {
		super(view);
		created_date = (TextView) view.findViewById(R.id.created_date);
		//repos_rank = (TextView) view.findViewById(R.id.repos_rank);
		repos_name = (TextView) view.findViewById(R.id.repos_name);
		notification_title = (TextView) view.findViewById(R.id.notification_title);
		notification_type = (IconTextView) view.findViewById(R.id.notification_type);
	}
}
