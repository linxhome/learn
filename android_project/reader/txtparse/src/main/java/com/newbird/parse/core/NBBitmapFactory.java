package com.newbird.parse.core;

import android.graphics.Bitmap;

import com.newbird.parse.config.FontConfig;
import com.newbird.parse.model.NBPage;
import com.newbird.parse.task.NBBitmapTask;

public class NBBitmapFactory {

    public static BitmapTaskWrapper load(NBPage page) {
        return new BitmapTaskWrapper(page);
    }

    static class BitmapTaskWrapper {
        NBBitmapTask task;

        BitmapTaskWrapper(NBPage page) {
            this.task = new NBBitmapTask(page);
        }

        public void async() {

        }

        public Bitmap sync() {
            return task.create();
        }
    }
}
