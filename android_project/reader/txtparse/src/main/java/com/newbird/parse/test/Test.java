package com.newbird.parse.test;

import com.newbird.parse.core.NBParseListener;
import com.newbird.parse.core.NBParserCore;
import com.newbird.parse.model.NBPage;

import java.util.List;

public class Test {
    static {
        //NBParserCore.initDefault().load("").async().into(null);
        NBParserCore.initDefault(null).setContent("").async().load(new NBParseListener() {
            @Override
            public void respPages(List<NBPage> pages) {

            }
        });


    }
}

