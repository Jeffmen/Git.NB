package com.example.gitnb.module.custom.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jeff on 16/11/9.
 */
public class BubbleView extends View {
    private BubbleDrawer bubbleDrawer;
    private final ValueAnimator animator = ValueAnimator.ofFloat(1f);
    private Handler handler = new Handler();
    private int DURATION = 10000;

    public BubbleView(Context context) {
        super(context);
        init(context);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        bubbleDrawer = new BubbleDrawer(context);
        bubbleDrawer.setBackgroundGradient(new int[]{0xffe0e0e0, 0xffe0e0e0});
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                handler.postDelayed(createRunnable(),100);
//            }
//        });
        animator.setDuration(DURATION);
        animator.start();
    }

    private Runnable createRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                animator.start();
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bubbleDrawer.setViewSize(getMeasuredWidth(), getMeasuredHeight());
        bubbleDrawer.drawBgAndBubble(canvas, 0.6f);
    }
}
