package com.newbird.parse.core;

import com.newbird.parse.config.FontConfig;
import com.newbird.parse.config.TextSize;

public interface NBMeasure {
    TextSize getSizeByMeasure(String word, FontConfig config);

    static class DefaultM implements NBMeasure {

        @Override
        public TextSize getSizeByMeasure(String word, FontConfig config) {
            return null;
        }
    }
}
