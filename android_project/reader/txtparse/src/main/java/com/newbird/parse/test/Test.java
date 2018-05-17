package com.newbird.parse.test;

import com.newbird.parse.core.NBParserCore;

public class Test {
    static {
        //NBParserCore.initDefault().into("").async().into(null);
        NBParserCore.initDefault(null).setContent("").async().into(null);


    }
}

