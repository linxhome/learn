package com.newbird.parse.core;

import android.graphics.Paint;

import com.newbird.parse.config.FontConfig;
import com.newbird.parse.config.TextMeasureSize;

public interface NBMeasure {
    TextMeasureSize getSize(String word, FontConfig config, int start, int end);

    static class DefaultMeasure implements NBMeasure {

        public TextMeasureSize getSize(String word, FontConfig config, int start, int end) {
            Paint paint = new Paint();
            paint.setTextSize(config.fontSize);
           // paint.setTypeface(config.typeface);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            float width = paint.measureText(word,start,end);
            float height = paint.descent() - paint.ascent();
            TextMeasureSize sizeConfig = new TextMeasureSize();
            sizeConfig.height = (int) height;
            sizeConfig.width = (int) width;
            return sizeConfig;
        }


    }
}
