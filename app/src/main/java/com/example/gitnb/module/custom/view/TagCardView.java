package com.example.gitnb.module.custom.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * Created by Rain on 17/2/25.
 */

public class TagCardView extends CardView {
    private static final int DEGREES_LEFT = -45;
    private static final int DEGREES_RIGHT = 45;
    private Paint trianglePaint;
    private Paint textPaint;
    private Corner corner;
    private String tag;

    public enum Corner {
        TOP_LEFT(1),
        TOP_RIGHT(2),
        BOTTOM_LEFT(3),
        BOTTOM_RIGHT(4),;
        private final int type;

        Corner(int type) {
            this.type = type;
        }

        private boolean top() {
            return this == TOP_LEFT || this == TOP_RIGHT;
        }

        private boolean left() {
            return this == TOP_LEFT || this == BOTTOM_LEFT;
        }

        private static Corner from(int type) {
            for (Corner c : values()) {
                if (c.type == type) return c;
            }
            return Corner.TOP_LEFT;
        }
    }

    public TagCardView(Context context) {
        super(context, null);
    }

    public TagCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trianglePaint.setColor(Color.parseColor("#FF9100"));
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(11);
        corner = Corner.TOP_RIGHT;
        tag = "Owner";
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.argb(40, 0, 0, 0));//加一层透明蒙层
        canvas.save();

        Rect rectText = new Rect();
        textPaint.getTextBounds(tag, 0, tag.length(), rectText);
        int width = rectText.width();
        int height = rectText.height();

        // translate
        if (corner.top()) {
            canvas.translate(0, (float) ((height * Math.sqrt(2)) - height));
        }

        // rotate
        if (corner.top()) {
            if (corner.left()) {
                canvas.rotate(DEGREES_LEFT, 0, height);
            } else {
                canvas.rotate(DEGREES_RIGHT, width, height);
            }
        } else {
            if (corner.left()) {
                canvas.rotate(DEGREES_RIGHT, 0, 0);
            } else {
                canvas.rotate(DEGREES_LEFT, width, 0);
            }
        }

        // draw triangle
        @SuppressLint("DrawAllocation")
        Path path = new Path();
        if (corner.top()) {
            path.moveTo(0, height);
            path.lineTo(width / 2, 0);
            path.lineTo(width, height);
        } else {
            path.moveTo(0, 0);
            path.lineTo(width / 2, height);
            path.lineTo(width, 0);
        }
        path.close();
        canvas.drawPath(path, trianglePaint);

        canvas.drawText(tag, (width) / 2, height, textPaint);

        canvas.restore();
    }
}
