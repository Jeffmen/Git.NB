package com.example.gitnb.widget;


import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import junit.framework.Assert;
import com.example.gitnb.R;

/**
 * A drawable that encapsulates all the functionality needed to display a letter tile to
 * represent a contact image.
 */
public class LetterTileDrawable extends Drawable {

    private final String TAG = LetterTileDrawable.class.getSimpleName();

    private final Paint mPaint;

    /** Letter tile */
    private static TypedArray sColors;
    private static int sDefaultColor;
    private static int sTileFontColor;
    private static float sLetterToTileRatio;
    private int DrawableSize = 100;

    /** Reusable components to avoid new allocations */
    private static final Paint sPaint = new Paint();
    private static final Rect sRect = new Rect();
    private static final char[] sFirstChar = new char[1];

    private String mDisplayName;
    private float mScale = 1.0f;
    private float mOffset = 0.0f;
    private boolean mIsCircle = false;
    private int position = 0;

    public LetterTileDrawable(final Resources res, int size) {
    	this(res);
    	DrawableSize = size;
    }
    public LetterTileDrawable(final Resources res) {
        mPaint = new Paint();
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);

        if (sColors == null) {
            sColors = res.obtainTypedArray(R.array.letter_tile_colors);
            sDefaultColor = res.getColor(R.color.letter_tile_default_color);
            sTileFontColor = res.getColor(R.color.letter_tile_font_color);
            sLetterToTileRatio = res.getFraction(R.dimen.letter_to_tile_ratio, 1, 1);
            sPaint.setTypeface(Typeface.create(
                    res.getString(R.string.letter_tile_letter_font_family), Typeface.NORMAL));
            sPaint.setTextAlign(Align.CENTER);
            sPaint.setAntiAlias(true);
        }
    }

    @Override
    public void draw(final Canvas canvas) {
        final Rect bounds = getBounds();
        if (!isVisible() || bounds.isEmpty()) {
            return;
        }
        // Draw letter tile.
        drawLetterTile(canvas);
    }

    private void drawLetterTile(final Canvas canvas) {
        // Draw background color.
        sPaint.setColor(sColors.getColor(position%sColors.length(), sDefaultColor));

        sPaint.setAlpha(mPaint.getAlpha());
        final Rect bounds = getBounds();
        //final int minDimension = Math.min(bounds.width(), bounds.height());
        final int minDimension = DrawableSize;

        if (mIsCircle) {
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), minDimension / 2, sPaint);
        } else {
            canvas.drawRect(bounds, sPaint);
        }
        // Scale text by canvas bounds and user selected scaling factor
        sPaint.setTextSize(mScale * sLetterToTileRatio * minDimension);
        //sPaint.setTextSize(sTileLetterFontSize);
        sPaint.getTextBounds(sFirstChar, 0, 1, sRect);
        sPaint.setColor(sTileFontColor);

        // Draw the letter in the canvas, vertically shifted up or down by the user-defined
        // offset
        canvas.drawText(mDisplayName, 0, mDisplayName.length(), bounds.centerX(),
                    bounds.centerY() + mOffset * bounds.height() + sRect.height() / 2,
                    sPaint);
    }

    public int getColor() {
        return pickColor(mDisplayName);
    }

    /**
     * Returns a deterministic color based on the provided contact identifier string.
     */
    private int pickColor(final String identifier) {
        if (TextUtils.isEmpty(identifier)) {
            return sDefaultColor;
        }
        // String.hashCode() implementation is not supposed to change across java versions, so
        // this should guarantee the same email address always maps to the same color.
        // The email should already have been normalized by the ContactRequest.
        final int color = Math.abs(identifier.hashCode()) % sColors.length();
        return sColors.getColor(color, sDefaultColor);
    }
    
    @Override
    public void setAlpha(final int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(final ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.OPAQUE;
    }

    /**
     * Scale the drawn letter tile to a ratio of its default size
     *
     * @param scale The ratio the letter tile should be scaled to as a percentage of its default
     * size, from a scale of 0 to 2.0f. The default is 1.0f.
     */
    public void setScale(float scale) {
        mScale = scale;
    }

    /**
     * Assigns the vertical offset of the position of the letter tile to the ContactDrawable
     *
     * @param offset The provided offset must be within the range of -0.5f to 0.5f.
     * If set to -0.5f, the letter will be shifted upwards by 0.5 times the height of the canvas
     * it is being drawn on, which means it will be drawn with the center of the letter starting
     * at the top edge of the canvas.
     * If set to 0.5f, the letter will be shifted downwards by 0.5 times the height of the canvas
     * it is being drawn on, which means it will be drawn with the center of the letter starting
     * at the bottom edge of the canvas.
     * The default is 0.0f.
     */
    public void setOffset(float offset) {
        Assert.assertTrue(offset >= -0.5f && offset <= 0.5f);
        mOffset = offset;
    }

    public void setContactDetails(final String displayName, final int pos) {
        mDisplayName = displayName;
        position = pos;
    }

    public void setIsCircular(boolean isCircle) {
        mIsCircle = isCircle;
    }
}
