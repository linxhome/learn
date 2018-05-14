package com.example.dai.categoryexample.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.dai.categoryexample.R;

/**
 * Created by dai on 2018/5/8.
 * Comment: canvas example
 */
public class CanvasFragment extends Fragment {
    private Button mButton;
    private EditText mEditForDraw;
    private ImageView mCanvas;
    private Handler mHandler;

    private int mTextSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_canvas, container, false);
        mButton = (Button) root.findViewById(R.id.btn_for_draw);
        mEditForDraw = (EditText) root.findViewById(R.id.edit_for_draw);
        mCanvas = (ImageView) root.findViewById(R.id.canvas_for_draw);
        initView();
        mHandler = new Handler(Looper.getMainLooper());

        mTextSize = 16 * (int) getResources().getDisplayMetrics().scaledDensity;
        return root;
    }

    private void initView() {
        final paintThread paintThread = new paintThread();
        final Thread thread = new Thread(paintThread);
        mButton.setOnClickListener(v -> {
            String content = mEditForDraw.getText().toString();
            paintThread.setString(content);
            if (thread.isAlive()) {
                Log.i("CanvasFragment", "thread is alive");
            } else {
                thread.start();
            }
        });


    }

    class paintThread implements Runnable {
        private String mString;

        public void setString(String string) {
            mString = string;
        }

        @Override
        public void run() {
            if (TextUtils.isEmpty(mString)) {
                return;
            }
            Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            txtPaint.setTextSize(mTextSize);
            txtPaint.setColor(Color.BLUE);
            Rect rect = new Rect();
            txtPaint.getTextBounds(mString, 0, mString.length() - 1, rect);
            int height = rect.height();
            int width = rect.width();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawText(mString, 0, rect.height(), txtPaint);
            mHandler.post(() -> mCanvas.setImageBitmap(bitmap));
        }
    }


}
