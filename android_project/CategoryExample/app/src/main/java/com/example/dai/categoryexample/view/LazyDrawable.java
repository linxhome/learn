package com.example.dai.categoryexample.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by dai on 2018/6/4.
 * Comment: lazy drawable
 */
public class LazyDrawable extends Drawable {
    public Drawable mBitmapDrawable;

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mBitmapDrawable != null) {
            mBitmapDrawable.setBounds(getBounds());
            mBitmapDrawable.draw(canvas);
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setTextSize(40);
            canvas.drawText("xxdfsdfsx", 0, 100, paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (mBitmapDrawable != null) {
            mBitmapDrawable.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (mBitmapDrawable != null) {
            mBitmapDrawable.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setDraw(Drawable bitmap) {
        mBitmapDrawable = bitmap;
        invalidateSelf();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (mBitmapDrawable != null) {
            //mBitmapDrawable.setBounds(bounds);
        }
    }

    /*@Override
    public int getIntrinsicWidth() {
        if (mBitmapDrawable != null) {
            return mBitmapDrawable.getIntrinsicWidth();
        }
        return super.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        if (mBitmapDrawable != null) {
            return mBitmapDrawable.getIntrinsicHeight();
        }
        return super.getIntrinsicHeight();
    }*/
}
