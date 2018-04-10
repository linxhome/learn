package com.newbird.parse.model;

public class NBWord {
    public CharSequence words;
    public boolean isParagraphStart; //是否段落结尾
    public boolean isParagraphEnd; //是否段落开头
    public boolean isLineEnd; //是否行结尾
    public int wordGap;//word间距
    public int wordHeight;
    public int wordWidth;
}
