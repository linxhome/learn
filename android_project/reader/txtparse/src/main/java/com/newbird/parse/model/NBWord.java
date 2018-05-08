package com.newbird.parse.model;

public class NBWord {
    public static final int WORD = 1;
    public static final int PUNCT = 2;
    public static final int ENTER = 3;

    public int position;
    public int meaWidth;
    public boolean isParagraphEnd; //是否段落开头

    public int type; // 类型，1为汉字，2为标点，3为换行
}
