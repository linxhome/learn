package com.example.dai.categoryexample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.dai.categoryexample.R;

/**
 * Created by dai
 * on 17/6/19.
 */

public class LoopImageView extends FrameLayout {
    public LoopImageView(Context context) {
        super(context);
    }

    public LoopImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.loop_image_view,null);

    }

    public LoopImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
