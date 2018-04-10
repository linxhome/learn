package com.newbird.parse.test;

import com.newbird.parse.core.NBParser;
import com.newbird.parse.core.ParseListener;
import com.newbird.parse.model.NBPage;

import java.util.List;

public class Test {
    static {
        NBParser.initDefault().load("").async().into(null);
    }
}
