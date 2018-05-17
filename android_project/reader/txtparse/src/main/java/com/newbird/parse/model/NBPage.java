package com.newbird.parse.model;

import com.newbird.parse.config.FontConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * 每章分页之后的数据
 */
public class NBPage {
    public List<NBLine> lines = new ArrayList<>();
    public FontConfig config;
    public String oriString;
    public String chapterSeq;

    public void add(NBLine line) {
        lines.add(line);
    }

    //获得当前页首字母在章节中的位置
    public int getStartPosition() {
        if (lines != null && lines.size() > 0) {
            List<NBWord> words = lines.get(0).getWords();
            if (words != null && words.size() > 0) {
                return words.get(0).position;
            }
        }
        return -1;
    }
}
