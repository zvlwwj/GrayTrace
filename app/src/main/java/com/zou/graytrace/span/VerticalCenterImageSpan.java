package com.zou.graytrace.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by zou on 2018/3/28.
 */

public class VerticalCenterImageSpan extends ImageSpan {
    public VerticalCenterImageSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();

        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        } else if (mVerticalAlignment == ALIGN_BOTTOM) {

        } else {
            transY += paint.getFontMetricsInt().descent * 2;
        }

        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}
