package com.newbird.parse.core;

import com.newbird.parse.model.NBPage;

import java.util.List;

public interface NBParseListener {
    void respPages(List<NBPage> rightPages,List<NBPage> leftPages);
}
