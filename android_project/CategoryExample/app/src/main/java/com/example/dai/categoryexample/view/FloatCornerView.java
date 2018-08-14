package com.example.dai.categoryexample.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

public class FloatCornerView extends android.support.v7.widget.AppCompatImageView {
    private WindowManager mWinManager;
    private Context mContext;
    private boolean isShowing = false;

    private static int mMaxSize = 200;
    private static final String mDrawColor = "#fa5151";
    private int mDrawRadius;

    public FloatCornerView(Context context) {
        super(context);
        init(context);
    }

    public FloatCornerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }


    public void showView() {
        if (isShowing) {
            return;
        }
        if (mWinManager == null) {
            mWinManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        setBackground(null);

        DisplayMetrics metrics = new DisplayMetrics();
        mWinManager.getDefaultDisplay().getRealMetrics(metrics);
        final int width = metrics.widthPixels;
        mMaxSize = width / 3;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(mMaxSize,
                mMaxSize, WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        mWinManager.addView(this, layoutParams);
        startToExpand();
        isShowing = true;
    }

    public void removeView() {
        if (mWinManager != null && isAttachedToWindow()) {
            mWinManager.removeView(this);
            isShowing = false;
        }
    }


    private void startToExpand() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mMaxSize);
        valueAnimator.setDuration(300);

        valueAnimator.addUpdateListener(animation -> {
            mDrawRadius = (int) animation.getAnimatedValue();
            invalidate();
        });
        valueAnimator.start();
    }

    Paint paint = new Paint();

    {
        paint.setColor(Color.parseColor(mDrawColor));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mMaxSize, mMaxSize, mDrawRadius, paint);
    }
}
