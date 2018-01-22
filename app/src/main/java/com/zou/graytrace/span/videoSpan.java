package com.zou.graytrace.span;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by zou on 2018/1/22.
 */

public class videoSpan extends ImageSpan {
    public videoSpan(Drawable d, String source) {
        super(d, source);
    }
}
