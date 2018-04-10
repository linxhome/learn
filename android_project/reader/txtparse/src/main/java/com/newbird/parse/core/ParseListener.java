package com.newbird.parse.core;

import com.newbird.parse.model.NBPage;

import java.util.List;

public interface ParseListener {
    void respPages(List<NBPage> pages);
}
