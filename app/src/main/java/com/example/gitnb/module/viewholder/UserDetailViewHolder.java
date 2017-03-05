package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class UserDetailViewHolder extends RecyclerView.ViewHolder{
	public TextView user_company;
	public TextView user_location;
	public TextView user_created_date;
	public TextView user_blog;
	public TextView user_email;
	public LinearLayout company_layout;
	public LinearLayout location_layout;
	public LinearLayout blog_layout;
	public LinearLayout email_layout;
	
	public UserDetailViewHolder(View view) {
		super(view);
		user_company = (TextView) view.findViewById(R.id.user_company);
		user_location = (TextView) view.findViewById(R.id.user_location);
		user_created_date = (TextView) view.findViewById(R.id.user_created_date);
		user_blog = (TextView) view.findViewById(R.id.user_blog);
		user_email = (TextView)view.findViewById(R.id.user_email);
		company_layout = (LinearLayout)view.findViewById(R.id.company_layout);
		location_layout = (LinearLayout)view.findViewById(R.id.location_layout);
		blog_layout = (LinearLayout)view.findViewById(R.id.blog_layout);
		email_layout = (LinearLayout)view.findViewById(R.id.email_layout);
	}
}
