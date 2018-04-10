package com.newbird.parse.core;

import com.newbird.parse.config.FontConfig;
import com.newbird.parse.model.NBPage;

import java.util.List;

public class ParserTask implements Runnable {

    NBParser.AsyncRet asyncRet;
    NBParser.ParseWrapper parseWrapper;

    public ParserTask(NBParser.ParseWrapper parseWrapper) {
        this.parseWrapper = parseWrapper;
    }

    public ParserTask(NBParser.AsyncRet asyncRet, NBParser.ParseWrapper parseWrapper) {
        this.asyncRet = asyncRet;
        this.parseWrapper = parseWrapper;
    }

    @Override
    public void run() {
        if (asyncRet != null) {
            asyncRet.setPages(getData());
        }
    }

    public List<NBPage> getData() {
        String oriStr = parseWrapper.getOriString();
        FontConfig config = parseWrapper.getConfig();
        NBMeasure measure = parseWrapper.getMeasure();
        //todo get the page list


        return null;
    }

}
