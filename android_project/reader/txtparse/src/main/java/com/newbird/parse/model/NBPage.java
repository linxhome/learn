package com.newbird.parse.model;

import com.newbird.parse.config.FontConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * 每章分页之后的数据
 */
public class NBPage {
    public List<NBLine> lists = new ArrayList<>();
    public FontConfig config;

    public void add(NBLine line) {
        lists.add(line);
    }

}
