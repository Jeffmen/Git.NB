package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class LoadMoreViewHolder extends RecyclerView.ViewHolder{
	public TextView loading_txt;
	public SimpleDraweeView loading_gif;
	
	public LoadMoreViewHolder(View view) {
		super(view);
		loading_gif = (SimpleDraweeView) view.findViewById(R.id.loading_gif);
		loading_txt = (TextView) view.findViewById(R.id.loading_txt);
	}
}
