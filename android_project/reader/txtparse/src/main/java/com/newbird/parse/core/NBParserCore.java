package com.newbird.parse.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.newbird.parse.config.FontConfig;
import com.newbird.parse.model.NBPage;
import com.newbird.parse.task.NBParserTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NBParserCore {

    public static ConfigWrapper initDefault(Context context) {
        ConfigWrapper config = new ConfigWrapper(FontConfig.defaultConfig(context));
        config.setMeasure(new NBMeasure.DefaultMeasure());
        return config;
    }

    public static ConfigWrapper init(FontConfig config) {
        ConfigWrapper pConfig = new ConfigWrapper(config);
        pConfig.setMeasure(new NBMeasure.DefaultMeasure());
        return pConfig;
    }

    public static class ConfigWrapper {
        FontConfig config;
        NBMeasure measure;
        int startReadPosition;

        private ConfigWrapper(FontConfig config) {
            this.config = config;
        }

        private void setMeasure(NBMeasure measure) {
            this.measure = measure;
        }

        public ParseWorker setContent(String orgStr) {
            return new ParseWorker(this, orgStr, startReadPosition);
        }

        public ConfigWrapper setStartReadPosition(int position) {
            startReadPosition = position;
            return this;
        }


        public Bitmap getBitmap(NBPage page) {
            return NBBitmapFactory.load(page).sync();
        }
    }


    public static class ParseWorker {
        private ConfigWrapper mConfig;
        private String mOrgString;
        private int startReadPosition;
        private ExecutorService mExecutor;

        public ParseWorker(ConfigWrapper mConfig, String mOrgString, int startReadPosition) {
            this.mConfig = mConfig;
            this.mOrgString = mOrgString;
            this.startReadPosition = startReadPosition;

            if (mOrgString == null || startReadPosition > mOrgString.length()) {
                this.startReadPosition = 0;
            }
        }

        public AsyncResp async() {
            if (mExecutor == null) {
                mExecutor = Executors.newSingleThreadExecutor();
            }
            AsyncResp async = new AsyncResp();
            execute(async);
            return async;
        }

        public AsyncResp async(ExecutorService executor) {
            if (executor != null) {
                this.mExecutor = executor;
            } else {
                mExecutor = Executors.newSingleThreadExecutor();
            }
            AsyncResp async = new AsyncResp();
            execute(async);
            return async;
        }

        public String getOriString() {
            return mOrgString;
        }

        public FontConfig getConfig() {
            return mConfig.config;
        }

        public NBMeasure getMeasure() {
            return mConfig.measure;
        }

        private void execute(AsyncResp asyncResp) {
            mExecutor.execute(new NBParserTask(asyncResp, this, startReadPosition));
        }
    }

    public static class AsyncResp {

        private NBParseListener mListener;
        Handler handler = new Handler(Looper.getMainLooper());

        public void into(NBParseListener listener) {
            mListener = listener;
        }

        public void setPages(final List<NBPage> list, final List<NBPage> leftList) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.respPages(list, leftList);
                }
            });
        }
    }

}
