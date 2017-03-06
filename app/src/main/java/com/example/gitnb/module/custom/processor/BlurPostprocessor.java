package com.example.gitnb.module.custom.processor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.graphics.Palette;

import com.example.gitnb.module.custom.FastBlur;
import com.example.gitnb.module.custom.RSBlur;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.BasePostprocessor;

/**
 * Copyright (C) 2015 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class BlurPostprocessor extends BasePostprocessor {

    private static int MAX_RADIUS = 25;
    private static int DEFAULT_DOWN_SAMPLING = 1;
    private ColorChangeListener colorChangeListener;
    private Context context;
    private int radius;
    private int sampling;

    public interface ColorChangeListener{
        void toolBarColorChange(int color);
    }

    public BlurPostprocessor(Context context) {
        this(context, MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
        if(context instanceof ColorChangeListener){
            colorChangeListener = (ColorChangeListener)context;
        }
    }

    public BlurPostprocessor(Context context, int radius) {
        this(context, radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurPostprocessor(Context context, int radius, int sampling) {
        this.context = context.getApplicationContext();
        this.radius = radius;
        this.sampling = sampling;
    }

    @Override
    public void process(Bitmap dest, Bitmap source) {
        if(colorChangeListener != null) {
//            Palette.Builder builder = Palette.from(source);
//            builder.generate(new Palette.PaletteAsyncListener() {
//                @Override
//                public void onGenerated(Palette palette) {
//                    if (palette != null) {
//                        Palette.Swatch swatch = palette.getLightMutedSwatch();
//                        if (null != swatch) {
//                            colorChangeListener.toolBarColorChange(swatch.getRgb());
//                        }
//                    }
//                }
//            });
        }

        int width = source.getWidth();
        int height = source.getHeight();
        int scaledWidth = width / sampling;
        int scaledHeight = height / sampling;

        Bitmap blurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(blurredBitmap);
        canvas.scale(1 / (float) sampling, 1 / (float) sampling);
        final Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, 0, 0, paint);
        canvas.drawColor(Color.argb(50, 0, 0, 0));//加一层透明蒙层

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                blurredBitmap = RSBlur.blur(context, blurredBitmap, radius);
            } catch (android.renderscript.RSRuntimeException e) {
                blurredBitmap = FastBlur.blur(blurredBitmap, radius, true);
            }
        } else {
            blurredBitmap = FastBlur.blur(blurredBitmap, radius, true);
        }

        Bitmap scaledBitmap =
                Bitmap.createScaledBitmap(blurredBitmap, dest.getWidth(), dest.getHeight(), true);
        blurredBitmap.recycle();

        super.process(dest, scaledBitmap);
    }

    @Override public String getName() {
        return getClass().getSimpleName();
    }

    @Override public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey("radius=" + radius + ",sampling=" + sampling);
    }

}
