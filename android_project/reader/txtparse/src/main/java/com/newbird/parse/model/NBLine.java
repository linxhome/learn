package com.newbird.parse.model;

import java.util.ArrayList;
import java.util.List;

public class NBLine {
    private List<NBWord> words = new ArrayList<>();

    public void append(NBWord word) {
        words.add(word);
    }

}
