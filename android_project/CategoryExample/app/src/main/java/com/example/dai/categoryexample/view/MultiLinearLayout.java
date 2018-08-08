package com.example.dai.categoryexample.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dai.categoryexample.R;

public class MultiLinearLayout extends LinearLayout {
    TextView infoTxt;
    TextView moveTxt;

    public MultiLinearLayout(Context context) {
        super(context);
        infoTxt = new TextView(context);
        addView(infoTxt);

    }

    public MultiLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        infoTxt = new TextView(context);
        moveTxt = new TextView(context);
        addView(infoTxt);
        addView(moveTxt);
        ViewGroup.LayoutParams pramas = infoTxt.getLayoutParams();
        pramas.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        pramas.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        infoTxt.setLayoutParams(pramas);
        infoTxt.setTextColor(context.getResources().getColor(R.color.black));
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                handleTwoFingerDown(ev);
                return true;
            case MotionEvent.ACTION_MOVE:
                handleTwoFinger(ev);
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void handleTwoFingerDown(MotionEvent event) {
        int index = event.getActionIndex();
        infoTxt.setText("finger " + index + " down " + event.getX());
    }

    private void handleTwoFinger(MotionEvent event) {
        if (event.getPointerCount() >= 2) {
            float x1 = event.getX(0);
            float x2 = event.getX(1);
            String txt = "finger1 x : " + x1 + " finger2 x" + x2;
            moveTxt.setText(txt);
        }
    }

}
