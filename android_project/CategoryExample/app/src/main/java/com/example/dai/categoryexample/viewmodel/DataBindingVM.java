package com.example.dai.categoryexample.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by dai
 * on 17/10/20.
 */

public class DataBindingVM {

    public final ObservableBoolean isTextVisible = new ObservableBoolean();
    public final ObservableField<String> textContent = new ObservableField();

    private Window mWindow;
    private TextView mTextView;

    public DataBindingVM(Window window,TextView textView) {
        mWindow = window;
        mTextView = textView;
    }

    public Window getWindow() {
        return mWindow;
    }

    public void OnClick(View view) {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText("root view post runnable to set text");
            }
        });
        textContent.set("on click data binding set text");
    }

    public void OnPostChoregrapher(View view) {
        Choreographer choreographer = Choreographer.getInstance();
        choreographer.postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                Log.e("Choreographer", "doFrame: postFrameCallback");
            }
        });

    }


}
