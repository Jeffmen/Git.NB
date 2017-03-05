package com.example.gitnb.module.custom.animation;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;


/**
 * Created by Rain on 16/1/23.
 */
public class ExpandCollapseAnimation extends Animation
{
    private View mAnimatedView;
    private boolean mIsVisibleAfter = false;
    private int initialHeight;
    private int targetHeight;

    public ExpandCollapseAnimation(View view, int duration)
    {
        this.setDuration(duration);
        this.mAnimatedView = view;
        if (view.getVisibility() == View.VISIBLE)
        {
            mIsVisibleAfter = false;
            initialHeight = mAnimatedView.getMeasuredHeight();

            mAnimatedView.setAlpha(1.0F);
            mAnimatedView.animate().alpha(0.0F).setDuration((int)(0.9 * duration));
        }
        else {
            mIsVisibleAfter = true;
            int maxContentWidth = ((ViewGroup)mAnimatedView.getParent()).getWidth();
            int j = View.MeasureSpec.makeMeasureSpec(maxContentWidth, View.MeasureSpec.EXACTLY);
            int k = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mAnimatedView.measure(j, k);
            targetHeight = mAnimatedView.getMeasuredHeight();

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            mAnimatedView.getLayoutParams().height = 1;
            mAnimatedView.setVisibility(View.VISIBLE);

            mAnimatedView.setAlpha(0.0F);
            mAnimatedView.animate().alpha(1.0F).setStartDelay(100L).setDuration(duration);
        }
    }

    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if(interpolatedTime == 1){
            if(mIsVisibleAfter) {
                mAnimatedView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                mAnimatedView.requestLayout();
            }
            else{
                mAnimatedView.setVisibility(View.GONE);
            }
        }else{

            if(mIsVisibleAfter) {
                mAnimatedView.getLayoutParams().height = (int)(targetHeight * interpolatedTime);
                mAnimatedView.requestLayout();
            }
            else{
                mAnimatedView.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                mAnimatedView.requestLayout();
            }
        }
    }
}
