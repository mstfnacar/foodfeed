package com.pem.mustafa.servertest.Other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pem.mustafa.servertest.R;

/**
 * Created by mustafa on 12.05.2016.
 */
public class CustomDividerItemDecoration extends RecyclerView.ItemDecoration{

    private Drawable mDivider;

    public CustomDividerItemDecoration(Context context) {
        mDivider = ContextCompat.getDrawable(context,R.drawable.line_divider);;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft()+ 100;
        int right = parent.getWidth() - parent.getPaddingRight()-100;

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
