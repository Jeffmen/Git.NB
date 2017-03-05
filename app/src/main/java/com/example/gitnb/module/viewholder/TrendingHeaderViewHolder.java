package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gitnb.R;
import com.example.gitnb.module.custom.view.TagListView;
import com.facebook.drawee.view.SimpleDraweeView;

public class TrendingHeaderViewHolder extends RecyclerView.ViewHolder{
	public LinearLayout language_layout;
	public LinearLayout since_layout;
	public TagListView tagLanguage;
	public TagListView tagSince;
	public TextView language;
	public TextView since;

	public TrendingHeaderViewHolder(View view) {
		super(view);
		language_layout = (LinearLayout) view.findViewById(R.id.language_layout);
		since_layout = (LinearLayout) view.findViewById(R.id.since_layout);
		tagLanguage = (TagListView) view.findViewById(R.id.tagLanguage);
		tagSince = (TagListView) view.findViewById(R.id.tagSince);
		language = (TextView) view.findViewById(R.id.language);
		since = (TextView) view.findViewById(R.id.since);
	}
}
