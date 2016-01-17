package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class ShowCaseViewHolder extends RecyclerView.ViewHolder{
	public TextView showcase_name;
	public TextView showcase_discription;
	public SimpleDraweeView showcase_avatar;
	
	public ShowCaseViewHolder(View view) {
		super(view);
		showcase_name = (TextView) view.findViewById(R.id.showcase_name);
		showcase_discription = (TextView) view.findViewById(R.id.showcase_description);
		showcase_avatar = (SimpleDraweeView) view.findViewById(R.id.showcase_avatar);
	}
}
