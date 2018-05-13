package com.newbird.parse.model;

public class NBWord {
    public static final int WORD = 1;
    public static final int COMMA = 2;
    public static final int NEWLINE = 3;

    public int position;
    public int measureWidth;
    public int textWidth;
    public boolean isParagraphHead; //是否段落开头
    public boolean isLineHead;

    public int type; // 类型，1为汉字，2为标点，3为换行


}
