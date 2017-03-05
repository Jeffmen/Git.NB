package com.example.gitnb.module.custom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.gitnb.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rain on 17/1/8.
 */

public class TagListView extends ViewGroup {

    private static final int DEFAULT_LINE_SPACING = 5;
    private static final int DEFAULT_TAG_SPACING = 10;
    private static final int DEFAULT_FIXED_COLUMN_SIZE = 3; //默认列数

    private int mLineSpacing;
    private int mTagSpacing;
    private int mColumnSize;
    private boolean isFixed;
    private List rowLeft;

    private BaseAdapter mAdapter;
    private TagItemClickListener mListener;
    private DataChangeObserver mObserver;

    public TagListView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TagListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TagListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagListView);
        try {
            mLineSpacing = a.getDimensionPixelSize(R.styleable.TagListView_lineSpacing, DEFAULT_LINE_SPACING);
            mTagSpacing = a.getDimensionPixelSize(R.styleable.TagListView_tagSpacing, DEFAULT_TAG_SPACING);
            mColumnSize = a.getInteger(R.styleable.TagListView_columnSize, DEFAULT_FIXED_COLUMN_SIZE);
            isFixed = a.getBoolean(R.styleable.TagListView_isFixed,false);
        } finally {
            a.recycle();
        }
        rowLeft = new ArrayList();
    }

    private void drawLayout() {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }

        this.removeAllViews();

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View view = mAdapter.getView(i,null,null);
            final int position = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.itemClick(position);
                    }
                }
            });
            this.addView(view);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wantHeight = 0;
        int wantWidth = resolveSize(0, widthMeasureSpec);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int childLeft = paddingLeft;
        int childTop = paddingTop;
        int lineHeight = 0;
        rowLeft.clear();

        for (int i = 0; i < getChildCount(); i++) {
            final View childView = getChildAt(i);
            LayoutParams params = childView.getLayoutParams();
            childView.measure(
                    getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, params.width),
                    getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, params.height)
            );
            int childHeight = childView.getMeasuredHeight();
            int childWidth = childView.getMeasuredWidth();
            lineHeight = Math.max(childHeight, lineHeight);

            if (childLeft + childWidth + paddingRight > wantWidth) {
                rowLeft.add((wantWidth-childLeft+paddingLeft+ mTagSpacing)/2);
                childLeft = paddingLeft;
                childTop += mLineSpacing + childHeight;
                lineHeight = childHeight;
            }

            childLeft += childWidth + mTagSpacing;
        }
        rowLeft.add((wantWidth-childLeft+paddingLeft+ mTagSpacing)/2);
        wantHeight += childTop + lineHeight + paddingBottom;
        setMeasuredDimension(wantWidth, resolveSize(wantHeight+5, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int index = 0;
        int width = r - l;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        //int childLeft = paddingLeft;
        int childLeft = (int)rowLeft.get(index);
        int childTop = paddingTop;
        int lineHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            lineHeight = Math.max(childHeight, lineHeight);

            if (childLeft + childWidth + paddingRight > width) {
                index++;
                //childLeft = paddingLeft;
                childLeft = (int)rowLeft.get(index);
                childTop += mLineSpacing + lineHeight;
                lineHeight = childHeight;
            }

            childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childLeft += childWidth + mTagSpacing;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(this.getContext(), attrs);
    }

    public void setAdapter(BaseAdapter adapter){
            mAdapter = adapter;
            if (mObserver == null){
                mObserver = new DataChangeObserver();
                mAdapter.registerDataSetObserver(mObserver);
            }
            drawLayout();
    }

    public void setItemClickListener(TagItemClickListener mListener) {
        this.mListener = mListener;
    }

    public interface TagItemClickListener {
        void itemClick(int position);
    }

    class DataChangeObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            TagListView.this.drawLayout();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }

}

