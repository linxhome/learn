package com.example.dai.categoryexample.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.dai.categoryexample.view.FloatCornerView;
import com.example.dai.categoryexample.view.FloatTouchView;

public class UIHelper {

    public static void addFloatWindow(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                context.startActivity(intent);
            } else {
                showView(context);
            }
        } else {
            showView(context);
        }
    }

    private static void showView(Context context) {
        FloatTouchView floatView = new FloatTouchView(context);
        FloatCornerView cornerView = new FloatCornerView(context);
        floatView.setMoveListener(new FloatTouchView.MoveState() {
            @Override
            public void onMoving() {
                cornerView.showView();

                float touchZ = floatView.getZ();
                float cornerZ = cornerView.getZ();

                Log.e("z float " , "touch z " + touchZ + " corner z " + cornerZ);
            }

            @Override
            public void onStopMove() {
                cornerView.removeView();
            }

            @Override
            public void onHide() {
                cornerView.removeView();
            }
        });
        floatView.showView();
    }
}
