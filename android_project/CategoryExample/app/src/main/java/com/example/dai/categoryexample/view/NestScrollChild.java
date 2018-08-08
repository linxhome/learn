package com.example.dai.categoryexample.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by dai on 2018/7/23.
 * Comment:test view for nest Scroller
 */

public class NestScrollChild extends RelativeLayout implements NestedScrollingChild {

    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    public NestScrollChild(Context context) {
        super(context);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mNestedScrollingChildHelper.setNestedScrollingEnabled(true);
    }

    public NestScrollChild(Context context, AttributeSet attrs) {
        super(context, attrs);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mNestedScrollingChildHelper.setNestedScrollingEnabled(true);
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
        return false;
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    private float lastY = 0;
    private float dy = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getRawY();
                dy = 0;
                return startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
            case MotionEvent.ACTION_MOVE:
                dy = event.getRawY() - lastY;
                lastY = event.getRawY();
                if (dy == 0) {
                    return false;
                }
                float preY = getY() + dy;

                int consumedY = 0;
                if (preY <= 0) {
                    consumedY = -(int) getY();
                } else if (preY >= ((ViewGroup) getParent()).getHeight() - getHeight()) {
                    consumedY = ((ViewGroup) getParent()).getHeight() - getHeight() - (int) getY();
                } else {
                    consumedY = (int) dy;
                }
                setY(getY() + consumedY);

                dispatchNestedScroll(0, consumedY, 0, (int) dy - consumedY, null);
                /*if (dispatchNestedPreScroll(0, (int) dy, consumed, null, ViewCompat.TYPE_TOUCH)) {
                    dy -= consumed[1];
                }
                float preY = getY() + dy;
                preY = preY <= 0 ? 0 : preY;//上边界
                preY = preY >= ? ((ViewGroup) getParent()).getHeight() - getHeight() : preY;//下边界
*/
            case MotionEvent.ACTION_UP:
                //stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
