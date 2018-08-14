package com.example.dai.categoryexample.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.dai.categoryexample.R;

public class FloatTouchView extends LinearLayout {
    private Context mContext;
    private WindowManager.LayoutParams wLayoutParam;
    private WindowManager mWinManager;
    private MoveState mMoveListener;

    private boolean hasShow = false;

    public FloatTouchView(Context context) {
        super(context);
        mContext = context;
    }

    public void setMoveListener(MoveState mMoveListener) {
        this.mMoveListener = mMoveListener;
    }

    public FloatTouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public void showView() {
        setBackground(mContext.getDrawable(R.drawable.btn_delete_big_light));
        wLayoutParam = new WindowManager.LayoutParams(100
                , 100
                , WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                , WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        wLayoutParam.gravity = Gravity.CENTER;

        if (mWinManager == null) {
            mWinManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }

        if (mWinManager != null) {
            mWinManager.addView(this, wLayoutParam);
            hasShow = true;
        }

        setOnClickListener(v -> {
            mWinManager.removeView(this);
            hasShow = false;
            if (mMoveListener != null) {
                mMoveListener.onHide();
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private float lastX;
    private float lastY;

    private float startX;
    private float startY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                lastY = event.getRawY();

                startX = event.getRawX();
                startY = event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getRawX() - lastX;
                float dy = event.getRawY() - lastY;

                wLayoutParam.x += dx;
                wLayoutParam.y += dy;
                mWinManager.updateViewLayout(this, wLayoutParam);
                lastX = event.getRawX();
                lastY = event.getRawY();

                if (mMoveListener != null) {
                    mMoveListener.onMoving();
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (mMoveListener != null) {
                    mMoveListener.onStopMove();
                }
                if (Math.abs(event.getRawX() - startX) <= 10 && Math.abs(event.getRawY() - startY) <= 0) {
                    this.performClick();
                    return true;
                }
        }
        return false;
    }


    public interface MoveState {
        void onMoving();

        void onStopMove();

        void onHide();
    }
}
