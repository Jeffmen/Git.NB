package com.example.gitnb.module.custom.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by Rain on 16/12/25.
 */

public class RightTranslationOutAnimation extends TranslateAnimation{

    public RightTranslationOutAnimation(View targetView){
        super(0,targetView.getWidth(),0,0);
        setFillAfter(true);
    }

}

