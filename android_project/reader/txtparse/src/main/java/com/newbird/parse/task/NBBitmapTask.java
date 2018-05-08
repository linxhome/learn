package com.newbird.parse.task;

import android.graphics.Bitmap;

import com.newbird.parse.model.NBPage;

public class NBBitmapTask implements Runnable {
    NBPage mPage;

    public NBBitmapTask(NBPage mPage) {
        this.mPage = mPage;
    }

    @Override
    public void run() {

    }

    public Bitmap create() {
        return null;
    }
}
