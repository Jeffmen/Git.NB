package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gitnb.R;

public class IssueHeaderViewHolder extends RecyclerView.ViewHolder{
	public LinearLayout open_layout;
	public LinearLayout close_layout;
	public TextView open;
	public TextView close;

	public IssueHeaderViewHolder(View view) {
		super(view);
		open_layout = (LinearLayout) view.findViewById(R.id.open_layout);
		close_layout = (LinearLayout) view.findViewById(R.id.close_layout);
		open = (TextView) view.findViewById(R.id.open);
		close = (TextView) view.findViewById(R.id.close);
	}
}
