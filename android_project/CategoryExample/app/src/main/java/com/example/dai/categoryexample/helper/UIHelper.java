package com.example.dai.categoryexample.helper;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;

import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.view.FloatTouchView;

public class UIHelper {

    public static void addFloatWindow(Activity context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(context)) {
                FloatTouchView floatView = new FloatTouchView(context);
                floatView.setBackground(context.getDrawable(R.drawable.btn_delete_big_light));
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT
                        , ActionBar.LayoutParams.WRAP_CONTENT
                        , WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
                        , WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSPARENT);
                params.gravity = Gravity.CENTER;
                WindowManager manager = context.getWindowManager();
                manager.addView(floatView, params);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                context.startActivity(intent);
            }
        }
    }
}
