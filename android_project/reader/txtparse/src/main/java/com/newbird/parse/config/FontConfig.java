package com.newbird.parse.config;

import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

public class FontConfig {
    //字体大小，行间距，字体类型设置
    public int fontSize;
    public int lineGap;
    public Typeface typeface;
    public FontType fontType;
    public int horizonGap;//水平页边距
    public int verticalGap;//竖直页边距
    public int screenWidth;
    public int screenHeight;

    enum FontType {
        DEFAULT_TYPE,SIMPLE,YAHEI
    }


    public static FontConfig defaultConfig(Context context) {
        FontConfig config = new FontConfig();
        config.fontSize = 12;
        config.fontType = FontType.DEFAULT_TYPE;
        config.lineGap = 3;

        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        if(windowManager != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            config.screenWidth = metrics.widthPixels;
            config.screenHeight = metrics.heightPixels;
        }
        return config;
    }

}
