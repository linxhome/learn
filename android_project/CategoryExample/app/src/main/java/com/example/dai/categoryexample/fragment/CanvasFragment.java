package com.example.dai.categoryexample.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.example.dai.categoryexample.view.LazyDrawable;

import java.io.File;

/**
 * Created by dai on 2018/5/8.
 * Comment: canvas example
 */
public class CanvasFragment extends Fragment {
    private Button mButton;
    private EditText mEditForDraw;
    private ImageView mCanvas;
    private Handler mHandler;
    private ImageView mCircleView;
    private ImageView mLazyImage;
    private Button mLazyButton;

    private int mTextSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_canvas, container, false);
        mButton = (Button) root.findViewById(R.id.btn_for_draw);
        mEditForDraw = (EditText) root.findViewById(R.id.edit_for_draw);
        mCanvas = (ImageView) root.findViewById(R.id.text_canvas_for_draw);
        mCircleView = (ImageView) root.findViewById(R.id.circle_for_draw);
        mHandler = new Handler(Looper.getMainLooper());
        mTextSize = 16 * (int) getResources().getDisplayMetrics().scaledDensity;

        mLazyButton = (Button) root.findViewById(R.id.lazy_button);
        mLazyImage = (ImageView) root.findViewById(R.id.lazy_imageview);
        initView();

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

        LazyDrawable drawable = new LazyDrawable();
        mLazyImage.setBackground(drawable);

        String filepath = getContext().getExternalCacheDir() + File.separator + "apple.png";
        Drawable bitmapDrawable = BitmapDrawable.createFromPath(filepath);

        //drawable.setDraw(bitmapDrawable);

        mLazyButton.setOnClickListener(v -> {
            drawable.setDraw(bitmapDrawable);
        });


        drawCircle();
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

    private void drawCircle() {
        Paint circlePaint = new Paint();
        Bitmap bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        int color = Color.parseColor("#39b2ba");
        LightingColorFilter filter = new LightingColorFilter(Color.rgb(0, 0, 0), color);
        circlePaint.setColorFilter(filter);
        circlePaint.setColor(color);
        canvas.drawCircle(10, 10, 10, circlePaint);

        circlePaint.setColorFilter(null);
        canvas.drawCircle(30, 10, 10, circlePaint);

        mHandler.post(() -> mCircleView.setImageBitmap(bitmap));


        mHandler.post(() -> {
            Context cont1 = mCircleView.getContext();
            Context cont2 = getActivity().getBaseContext();
            Context cont3 = getContext();
            Context cont4 = getActivity().getApplicationContext();

            Log.e("all context show", "show" + cont1.hashCode() + "-"
                    + cont2.hashCode() + "-" + cont3.hashCode() + "-" + cont4.hashCode());
        });
    }


}
