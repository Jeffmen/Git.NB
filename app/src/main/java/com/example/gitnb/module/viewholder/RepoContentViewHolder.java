package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gitnb.R;
import com.joanzapata.iconify.widget.IconTextView;

public class RepoContentViewHolder extends RecyclerView.ViewHolder{
	public TextView content_name;
	public IconTextView content_type;

	public RepoContentViewHolder(View view) {
		super(view);
		content_name = (TextView) view.findViewById(R.id.content_name);
		content_type = (IconTextView) view.findViewById(R.id.content_type);
	}
}
