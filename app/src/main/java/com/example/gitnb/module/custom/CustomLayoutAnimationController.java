package com.example.gitnb.module.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;

import java.util.Random;

/**
 * Created by Rain on 17/1/8.
 */

public class CustomLayoutAnimationController extends GridLayoutAnimationController {

    // 7 just lucky number
    public static final int ORDER_CUSTOM  = 7;
    private Callback onIndexListener;

    public void setOnIndexListener(Callback onIndexListener) {
        this.onIndexListener = onIndexListener;
    }

    public CustomLayoutAnimationController(Animation anim) {
        super(anim);
    }

    public CustomLayoutAnimationController(Animation anim, float columnDelay, float rowDelay) {
        super(anim, columnDelay, rowDelay);
    }

    public CustomLayoutAnimationController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected long getDelayForView(View view) {
        if(getOrder() == ORDER_CUSTOM) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            AnimationParameters params = (AnimationParameters) lp.layoutAnimationParameters;
            if (params == null) {
                return 0;
            }
            if (mRandomizer == null) {
                mRandomizer = new Random();
            }

            final int row = (int) (params.rowsCount * mRandomizer.nextFloat());
            final int column = (int) (params.columnsCount * mRandomizer.nextFloat());
            final long duration = mAnimation.getDuration();
            final float columnDelay = getColumnDelay() * duration;
            final float rowDelay = getRowDelay() * duration;

            return (long) (column * columnDelay + row * rowDelay);
        }
        else{
            return super.getDelayForView(view);
        }
    }

    @Override
    protected int getTransformedIndex(LayoutAnimationController.AnimationParameters params) {
        if(getOrder() == ORDER_CUSTOM && onIndexListener != null) {
            return onIndexListener.onIndex(this, params.count, params.index);
        } else {
            return super.getTransformedIndex(params);
        }
    }

    /**
     * callback for get play animation order
     *
     */
    public interface Callback{

        int onIndex(CustomLayoutAnimationController controller, int count, int index);
    }
}
