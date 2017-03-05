package com.example.gitnb.module.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gitnb.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.joanzapata.iconify.widget.IconTextView;

public class OperationViewHolder extends RecyclerView.ViewHolder{
    public View top_divider;
	public IconTextView operation_icon;
	public TextView operation_name;
	public TextView operation_value;
	public View bottom_divider;
	public View bottom_short_divider;

	public OperationViewHolder(View view) {
		super(view);
		top_divider = view.findViewById(R.id.top_divider);
		operation_icon = (IconTextView) view.findViewById(R.id.operation_icon);
		operation_name = (TextView) view.findViewById(R.id.operation_name);
		operation_value = (TextView) view.findViewById(R.id.operation_value);
		bottom_divider = view.findViewById(R.id.bottom_divider);
		bottom_short_divider = view.findViewById(R.id.bottom_short_divider);
	}
}
