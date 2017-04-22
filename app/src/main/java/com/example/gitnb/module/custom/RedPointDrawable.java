package com.example.gitnb.module.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import com.example.gitnb.R;

/**
 * Created by Rain on 17/2/27.
 */
public class RedPointDrawable extends Drawable {
    private Drawable mDrawable;
    private boolean mShowRedPoint = true;
    private Paint mPaint;
    private int mRadius;
    private int mGravity = Gravity.CENTER;

    public RedPointDrawable(Context context, Drawable origin) {
        mDrawable = origin;// 原来的drawable
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(context.getResources().getColor(R.color.orange_yellow));
        mRadius = context.getResources().getDimensionPixelSize(R.dimen.red_point_radius_small);//小红点半径
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setShowRedPoint(boolean showRedPoint) {
        mShowRedPoint = showRedPoint;
        invalidateSelf();
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mDrawable.draw(canvas);//先绘制原图标
        if (mShowRedPoint) {
            // 获取原图标的右上角坐标
            int cx = getBounds().right;
            int cy = getBounds().top;
            // 计算我们的小红点的坐标
            if ((Gravity.LEFT & mGravity) == Gravity.LEFT) {
                cx -= mRadius;
            } else if ((Gravity.RIGHT & mGravity) == Gravity.RIGHT) {
                cx += mRadius;
            }
            if ((Gravity.TOP & mGravity) == Gravity.TOP) {
                cy -= mRadius;
            } else if ((Gravity.BOTTOM & mGravity) == Gravity.BOTTOM) {
                cy += mRadius;
            }
            canvas.drawCircle(cx, cy, mRadius, mPaint);//绘制小红点
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mDrawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return mDrawable.getOpacity();
    }

    @Override
    public int getIntrinsicHeight() {
        return mDrawable.getIntrinsicHeight();//它的高度使用原来的高度
    }

    @Override
    public int getIntrinsicWidth() {
        return mDrawable.getIntrinsicWidth();//它的宽度使用原来的宽度
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        mDrawable.setBounds(bounds);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mDrawable.setBounds(left, top, right, bottom);
    }

    public static RedPointDrawable wrap(Context context, Drawable drawable) {
        // 把原来的Drawable包装为一个小红点的Drawable
        if (drawable instanceof RedPointDrawable) {
            return (RedPointDrawable) drawable;
        }
        return new RedPointDrawable(context, drawable);
    }
}