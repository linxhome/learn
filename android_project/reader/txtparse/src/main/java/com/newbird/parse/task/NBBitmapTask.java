package com.newbird.parse.task;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.newbird.parse.config.FontConfig;
import com.newbird.parse.model.NBLine;
import com.newbird.parse.model.NBPage;
import com.newbird.parse.model.NBWord;

/**
 * 生成bitmap
 */
public class NBBitmapTask implements Runnable {
    NBPage mPage;
    FontConfig mConfig;

    public NBBitmapTask(NBPage mPage) {
        this.mPage = mPage;
        mConfig = mPage.config;
    }

    public NBBitmapTask(NBPage mPage, FontConfig mConfig) {
        this.mPage = mPage;
        this.mConfig = mConfig;
    }

    @Override
    public void run() {

    }

    public Bitmap create() {
        Bitmap bitmap = Bitmap.createBitmap(mConfig.contentWidth , mConfig.contentHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setTextSize(mConfig.fontSize);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        canvas.drawColor(Color.YELLOW);
        float height = paint.descent() - paint.ascent();
        float drawPosY = height + mConfig.verticalMargin;
        float drawPosX = mConfig.horizonMargin;
        String oriString = mPage.oriString;
        for (NBLine line : mPage.lines) {
            for (NBWord word : line.getWords()) {
                String words = oriString.substring(word.position, word.position + 1);
                if (word.isParagraphHead) {
                    words = "  " + words;
                }
                canvas.drawText(words, drawPosX, drawPosY, paint);
                drawPosX += word.measureWidth;
            }
            drawPosY += height + mConfig.lineGap;
            drawPosX = mConfig.horizonMargin;
        }
        return bitmap;
    }
}
