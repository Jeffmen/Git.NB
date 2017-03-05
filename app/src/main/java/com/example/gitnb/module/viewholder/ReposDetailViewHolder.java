package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.gitnb.R;

public class ReposDetailViewHolder extends RecyclerView.ViewHolder{
	public TextView repos_updated;
	public TextView repos_homepage;
	public TextView repos_discription;
	public TextView type;
	public TextView stars;
	public TextView created_date;
	public TextView language;
	public TextView forks;
	public TextView size;
	
	public ReposDetailViewHolder(View view) {
		super(view);
    	repos_updated = (TextView) view.findViewById(R.id.repos_updated);
		repos_homepage = (TextView) view.findViewById(R.id.repos_homepage);
		repos_discription = (TextView) view.findViewById(R.id.repos_description);
		type = (TextView) view.findViewById(R.id.type);
		stars = (TextView) view.findViewById(R.id.stars);
		created_date = (TextView) view.findViewById(R.id.created_date);
		language = (TextView) view.findViewById(R.id.language);
		forks = (TextView) view.findViewById(R.id.forks);
		size = (TextView) view.findViewById(R.id.size);
	}
}
