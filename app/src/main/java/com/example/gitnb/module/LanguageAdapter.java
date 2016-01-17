package com.example.gitnb.module;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.gitnb.R;
import com.example.gitnb.utils.Utils;
import com.example.gitnb.widget.LetterTileDrawable;

public class LanguageAdapter extends RecyclerView.Adapter<ViewHolder>{
	private Context mContext;
	private int iconSize;
    private CharSequence[] languageName;
    private CharSequence[] languageValue;
    protected final LayoutInflater mInflater;
    private OnItemClickListener mItemClickListener;
    
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    
    public LanguageAdapter(Context context) {
    	mContext = context;
    	mInflater = LayoutInflater.from(mContext);
		iconSize = Utils.dpToPx(context, 50);
		languageName = mContext.getResources().getTextArray(R.array.all_language_name);
		languageValue = mContext.getResources().getTextArray(R.array.all_language_value);
		//notifyItemRangeInserted(0, languageValue.length);
	}
    
    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

	@Override
	public int getItemCount() {
		return languageName == null ? 0 : languageName.length;
	}

	public String getItemName(int position){
		return languageName == null ? null : languageName[position].toString();
	}	
	
	public String getItemValue(int position){
		return languageValue == null ? null : languageValue[position].toString();
	}
	
	protected void setAnimation(View viewToAnimate, int position) {
        Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.bottom_up);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {	
		LanguageViewHolder viewHolder = (LanguageViewHolder) vh;
		LetterTileDrawable titleIcon = new LetterTileDrawable(mContext.getResources(), iconSize);
		titleIcon.setIsCircular(true);
		titleIcon.setContactDetails(getItemName(position), position);
	    viewHolder.language.setImageDrawable(titleIcon);
	    //setAnimation(vh.itemView, position);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int viewType) {
		View v = mInflater.inflate(R.layout.language_list_item,viewgroup,false);
		return new LanguageViewHolder(v);
	}

	public class LanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		public ImageView language;
		
		public LanguageViewHolder(View view) {
			super(view);
			language = (ImageView) view.findViewById(R.id.language);
	        view.setOnClickListener(this);
		}
	
		@Override
		public void onClick(View v) {
	        if (mItemClickListener != null) {
	        	mItemClickListener.onItemClick(v, getLayoutPosition());
	        }
		}
	}
}

