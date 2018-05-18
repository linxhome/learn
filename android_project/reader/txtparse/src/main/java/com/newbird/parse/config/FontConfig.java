package com.newbird.parse.config;

import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

public class FontConfig {
    public Context mContext;
    //字体大小，行间距，字体类型设置
    public int fontSize;
    private int fontSizeSp; //范围 [12-25]

    public int lineGap;
    public Typeface typeface;
    public FontType fontType;
    public int horizonMargin;//水平页边距
    public int verticalMargin;//竖直页边距
    public int contentWidth;
    public int contentHeight;
    private int mBackgroundColor;
    public int textColor;


    enum FontType {
        DEFAULT_TYPE, SIMPLE, YAHEI
    }

    public static final int MAX_FONT_SIZE_SP = 25;
    public static final int MIN_FINT_SIZE_SP = 12;

    public FontConfig(Context context) {
        mContext = context;
    }

    public int incrFontSize() {
        float scaleDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
        fontSizeSp = fontSizeSp++;
        this.fontSize = (int) (this.fontSizeSp * scaleDensity);
        return fontSizeSp;
    }

    public int descrFontSize() {
        float scaleDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
        fontSizeSp = fontSizeSp--;
        this.fontSize = (int) (this.fontSizeSp * scaleDensity);
        return fontSizeSp;
    }

    public int setFontSizeSp(int newSize) {
        float scaleDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
        this.fontSize = (int) (newSize * scaleDensity);
        this.fontSizeSp = newSize;
        return fontSizeSp;
    }

    public int getFontSizeSp() {
        return fontSizeSp;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        if (mBackgroundColor == ColorConfig.BackgroundColor.STYLE_NIGHT) {
            textColor = ColorConfig.TextColor.STYLE_TWO;
        }
    }

    public static FontConfig defaultConfig(Context context) {
        FontConfig config = new FontConfig(context);
        float scaleDensity = context.getResources().getDisplayMetrics().scaledDensity;
        config.fontSizeSp = 18;
        config.fontSize = (int) (config.fontSizeSp * scaleDensity);
        config.fontType = FontType.DEFAULT_TYPE;
        config.lineGap = (int) (5 * scaleDensity);
        config.verticalMargin = (int) (25 * scaleDensity);
        config.horizonMargin = (int) (25 * scaleDensity);
        config.mBackgroundColor = ColorConfig.BackgroundColor.SHEEP_SKIN;
        config.textColor = ColorConfig.TextColor.STYLE_ONE;

        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            config.contentWidth = metrics.widthPixels;
            config.contentHeight = metrics.heightPixels;
        }
        return config;
    }

}
