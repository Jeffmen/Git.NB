package com.example.gitnb.module.custom;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;


/**
 * Created by Rain on 16/1/23.
 */
public class ExpandAnimation  extends Animation
{
    private View mAnimatedView;
    private boolean mIsVisibleAfter = false;
    private int mMarginEnd;
    private int mMarginStart;
    private RelativeLayout.LayoutParams mViewLayoutParams;

    public ExpandAnimation(View view, int duration)
    {
        this.setDuration(duration);
        this.mAnimatedView = view;
        this.mViewLayoutParams = ((RelativeLayout.LayoutParams)mAnimatedView.getLayoutParams());
        if (view.getVisibility() == View.VISIBLE)
        {
            this.mIsVisibleAfter = false;
            this.mMarginStart = mViewLayoutParams.bottomMargin;
            this.mMarginEnd = 0 - mAnimatedView.getHeight();
            this.mAnimatedView.setAlpha(1.0F);
            this.mAnimatedView.animate().alpha(0.0F).setDuration((int)(0.9 * duration));
        }
        else {
            this.mIsVisibleAfter = true;
            this.mAnimatedView.setVisibility(View.VISIBLE);
            this.mMarginStart = mViewLayoutParams.bottomMargin;
            this.mMarginEnd = 0;
            this.mAnimatedView.setAlpha(0.0F);
            this.mAnimatedView.animate().alpha(1.0F).setStartDelay(100L).setDuration(duration);
        }
    }

    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        this.mViewLayoutParams.bottomMargin =
                (this.mMarginStart + (int)(interpolatedTime * (this.mMarginEnd - this.mMarginStart)));
        if (interpolatedTime >= 1.0F) {
            if (!this.mIsVisibleAfter) {
                this.mAnimatedView.setVisibility(View.GONE);
            }
        }
        this.mAnimatedView.requestLayout();
    }
}
