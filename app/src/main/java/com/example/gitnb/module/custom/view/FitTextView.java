package com.example.gitnb.module.custom.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by Rain on 17/1/10.
 */

public class FitTextView extends TextView {
    private boolean mNeedsResize;
    private Paint mTestPaint;
    private float originalTextSize = 0f;

    public FitTextView(Context context) {
        super(context);
        initialise();
    }

    public FitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
        originalTextSize = getTextSize();
    }

    private void initialise() {
        mTestPaint = new Paint();
        mTestPaint.set(this.getPaint());
        //max size defaults to the initially specified text size unless it is too small
    }

    /* Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    private void resizeText(String text, int textWidth)
    {
        if (textWidth <= 0)
            return;
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableLeft = drawables[0];
        Drawable drawableRight = drawables[0];

        int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight()
                - getCompoundPaddingRight() - getCompoundPaddingLeft();
        mTestPaint.set(this.getPaint());
        mTestPaint.setTextSize(getTextSize());

        float hi = originalTextSize;
        float lo = 2;
        final float threshold = 0.5f; // How close we have to be

        while ((hi - lo) > threshold) {
            float size = (hi + lo) / 2;
            mTestPaint.setTextSize(size);
            if (mTestPaint.measureText(text) >= targetWidth)
                hi = size; // too big
            else
                lo = size; // too small
        }
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            int widthLimit = (right - left) - getCompoundPaddingLeft() - getCompoundPaddingRight();
            int heightLimit = (bottom - top) - getCompoundPaddingBottom() - getCompoundPaddingTop();
            resizeText(getText().toString(), widthLimit);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        int widthLimit = getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
        int heightLimit = getHeight() - getCompoundPaddingBottom() - getCompoundPaddingTop();
        resizeText(getText().toString(), widthLimit);
    }
}
