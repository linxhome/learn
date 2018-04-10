package com.newbird.parse.config;

public class FontConfig {
    //字体大小，行间距，字体类型设置
    public int fontSize;
    public int lineGap;
    public FontType fontType;

    enum FontType {
        DEFAULT_TYPE,SIMPLE,YAHEI
    }


    public static FontConfig defaultConfig() {
        FontConfig config = new FontConfig();
        config.fontSize = 12;
        config.fontType = FontType.DEFAULT_TYPE;
        config.lineGap = 3;
        return config;
    }
}
