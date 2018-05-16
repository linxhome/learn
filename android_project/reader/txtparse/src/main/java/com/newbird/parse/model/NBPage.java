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

}
