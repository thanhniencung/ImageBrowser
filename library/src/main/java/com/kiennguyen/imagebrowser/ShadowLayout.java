package com.kiennguyen.imagebrowser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by kiennguyen on 3/4/17.
 */

public class ShadowLayout extends RelativeLayout {

    static final int BLUR_RADIUS = 6;
    static final Rect sShadowRect = new Rect(0, 0, 200 + 2 * BLUR_RADIUS, 200 + 2 * BLUR_RADIUS);
    static RectF tempShadowRectF = new RectF(0, 0, 0, 0);
    Paint mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float mShadowDepth;
    Bitmap mShadowBitmap;

    public ShadowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowLayout(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < getChildCount(); ++i) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.VISIBLE || child.getAlpha() == 0) {
                continue;
            }
            int depthFactor = (int) (80 * mShadowDepth);
            canvas.save();
            canvas.translate(child.getLeft() + depthFactor,
                    child.getTop() + depthFactor);
            canvas.concat(child.getMatrix());
            tempShadowRectF.right = child.getWidth();
            tempShadowRectF.bottom = child.getHeight();
            canvas.drawBitmap(mShadowBitmap, sShadowRect, tempShadowRectF, mShadowPaint);
            canvas.restore();
        }
    }
}