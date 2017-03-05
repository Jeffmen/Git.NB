package com.example.gitnb.module.trending;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.gitnb.R;
import com.example.gitnb.utils.Utils;

/**
 * Created by Rain on 17/1/8.
 */
public class LanguageTagAdapter extends BaseAdapter {

    private Context mContext;
    private CharSequence[] languageName;
    private CharSequence[] languageValue;
    private static TypedArray sColors;
    private static int sDefaultColor;
    private int selectPosition = 0;
    private int borderRadius = 0;

    public LanguageTagAdapter(Context context) {
        mContext = context;
        sColors = context.getResources().obtainTypedArray(R.array.letter_tile_colors);
        sDefaultColor = context.getResources().getColor(R.color.letter_tile_default_color);
        languageName = mContext.getResources().getTextArray(R.array.all_language_name);
        languageValue = mContext.getResources().getTextArray(R.array.all_language_value);
        borderRadius = Utils.dpToPx(mContext, 10);
    }

    @Override
    public int getCount() {
        return languageName.length;
    }

    @Override
    public String getItem(int position) {
        return languageName == null ? null : languageName[position].toString();
    }

    public String getItemValue(int position){
        return languageValue == null ? null : languageValue[position].toString();
    }

    public void setSelectPosition(int position){
        this.selectPosition = position;
    }

    public String getSelectedValue(){
        return getItemValue(selectPosition);
    }

    public String getSelectedItem(){
        return getItem(selectPosition);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.tag_grid_item, null);
            holder = new ViewHolder();
            holder.tagBtn = (Button) convertView.findViewById(R.id.tag_btn);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        final String text = getItem(position);
        holder.tagBtn.setText(text);
        if(position == selectPosition){
            holder.tagBtn.setSelected(true);
        }
        else{
            holder.tagBtn.setSelected(false);
        }
        if(position == selectPosition){
            holder.tagBtn.setPressed(true);
            holder.tagBtn.setTextColor(Color.WHITE);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(borderRadius);
            drawable.setStroke(1, Color.parseColor("#F44336"));
            drawable.setColor(Color.parseColor("#F44336"));
            holder.tagBtn.setBackground(drawable);
        }
        else{
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(borderRadius);
            drawable.setStroke(1, sColors.getColor(position%sColors.length(), sDefaultColor));
            //drawable.setStroke(1, Color.parseColor("#e0e0e0"));
            drawable.setColor(Color.parseColor("#FCFCFC"));

            holder.tagBtn.setBackground(drawable);
            holder.tagBtn.setSelected(false);
            holder.tagBtn.setTextColor(Color.BLACK);
        }
        holder.tagBtn.setTypeface(null, Typeface.NORMAL);
        return convertView;
    }

    static class ViewHolder {
        Button tagBtn;
    }
}
