package com.example.gitnb.module.custom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Rain on 16/12/25.
 */

public class MaskImageView extends ImageView {

    public MaskImageView(Context context){
        super(context);
    }

    public MaskImageView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.argb(40, 0, 0, 0));//加一层透明蒙层
    }
}
