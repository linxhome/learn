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
    public int horizonMargin;//水平页边距
    public int verticalMargin;//竖直页边距
    public int contentWidth;
    public int contentHeight;
    public int backgroundColor;
    public int textColor;

    enum FontType {
        DEFAULT_TYPE, SIMPLE, YAHEI
    }


    public static FontConfig defaultConfig(Context context) {
        FontConfig config = new FontConfig();
        float scaleDensity = context.getResources().getDisplayMetrics().scaledDensity;
        config.fontSize = (int) (18 * scaleDensity);
        config.fontType = FontType.DEFAULT_TYPE;
        config.lineGap = (int) (3 * scaleDensity);
        config.verticalMargin = (int) (10 * scaleDensity);
        config.horizonMargin = (int) (10 * scaleDensity);
        config.backgroundColor = ColorConfig.BackgroundColor.SHEEP_SKIN;
        config.textColor = ColorConfig.TextColor.STYLE_ONE;

        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            config.contentWidth = metrics.widthPixels;
            config.contentHeight = metrics.heightPixels ;
        }
        return config;
    }

}
