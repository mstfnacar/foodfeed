package com.pem.mustafa.servertest.Other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by mustafa on 25.05.2016.
 */
public class CustomProfileImageView extends ImageView {

    public static float radius = 5.0f;

    public CustomProfileImageView(Context context) {
        super(context);
    }

    public CustomProfileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomProfileImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //float radius = 36.0f;
        Path clipPath = new Path();
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);

        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
