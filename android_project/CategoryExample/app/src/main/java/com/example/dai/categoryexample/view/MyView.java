package com.example.dai.categoryexample.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.dai.categoryexample.R;

/**
 * Created by dai
 * on 17/9/17.
 */

public class MyView extends View {
    private final static String TAG = "MyView";

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        //xml中使用双惨构造函数
        super(context, attrs);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.MyViewStyle);
        String string = type.getString(R.styleable.MyViewStyle_android_text);
        float size = type.getDimension(R.styleable.MyViewStyle_view_size, 0f);

        Log.i(TAG, "MyView: " + type.toString());
        Log.i(TAG, "MyView: text is " + string + " and view size is " + size );
        type.recycle();

        int count = attrs.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String name = attrs.getAttributeName(i);
            String value = attrs.getAttributeValue(i);
            Log.i(TAG, "MyView: key is " + name + " value is " + value);
        }
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
