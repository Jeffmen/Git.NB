package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class OrganizationViewHolder extends RecyclerView.ViewHolder{
	public SimpleDraweeView orga_avatar;
	public TextView orga_name;
	public TextView orga_rank;

	public OrganizationViewHolder(View view) {
		super(view);
		orga_avatar = (SimpleDraweeView) view.findViewById(R.id.orga_avatar);
		orga_name = (TextView) view.findViewById(R.id.orga_name);
		orga_rank = (TextView) view.findViewById(R.id.orga_rank);
	}
}
