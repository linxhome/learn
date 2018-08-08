package com.example.dai.categoryexample.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by dai on 2018/7/23.
 * Comment: test view for nest Scroller
 */

public class NestScrollParent extends RelativeLayout implements NestedScrollingParent {

    private NestedScrollingParentHelper mScrollingParentHelper;

    public NestScrollParent(Context context) {
        super(context);
        mScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    public NestScrollParent(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        mScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        mScrollingParentHelper.onStopNestedScroll(target);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        int newY = (int) getY() + dyUnconsumed;
        int consumeY = 0;
        if (newY <= 0) {
            consumeY = -(int) getY();
        } else if (newY >= ((ViewGroup) getParent()).getHeight() - getHeight()) {
            consumeY = ((ViewGroup) getParent()).getHeight() - getHeight() - (int) getY();
        } else {
            consumeY = dyUnconsumed;
        }
        setTranslationY(getTranslationY() + consumeY);

    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed) {

    }
}
