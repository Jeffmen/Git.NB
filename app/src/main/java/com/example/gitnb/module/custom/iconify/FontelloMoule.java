package com.example.gitnb.module.custom.iconify;

import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconFontDescriptor;

/**
 * Created by Rain on 17/2/18.
 */

public class FontelloMoule implements IconFontDescriptor {
    @Override
    public String ttfFileName() {
        return "event.ttf";
    }

    @Override
    public Icon[] characters() {
        return FontelloIcons.values();
    }
}
