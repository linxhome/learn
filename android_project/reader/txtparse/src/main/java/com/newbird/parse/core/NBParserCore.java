package com.newbird.parse.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.newbird.parse.config.FontConfig;
import com.newbird.parse.model.NBPage;
import com.newbird.parse.task.NBParserTask;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NBParserCore {

    public static ConfigWrapper initDefault(Context context) {
        ConfigWrapper config =  new ConfigWrapper(FontConfig.defaultConfig(context));
        config.setMeasure(new NBMeasure.DefaultMeasure());
        return config;
    }

    public static ConfigWrapper init(FontConfig config) {
        ConfigWrapper pConfig =  new ConfigWrapper(config);
        pConfig.setMeasure(new NBMeasure.DefaultMeasure());
        return pConfig;
    }

    public static class ConfigWrapper {
        FontConfig config;
        NBMeasure measure;

        private ConfigWrapper(FontConfig config) {
            this.config = config;
        }

        private void setMeasure(NBMeasure measure) {
            this.measure = measure;
        }

        public ParseWorker setContent(String orgStr) {
            return new ParseWorker(this, orgStr);
        }

        public Bitmap getBitmap(NBPage page) {
            return NBBitmapFactory.load(page).sync();
        }
    }


    public static class ParseWorker {
        private ConfigWrapper mConfig;
        private String mOrgString;
        private ExecutorService mExecutor;

        public ParseWorker(ConfigWrapper mConfig, String orgString) {
            this.mConfig = mConfig;
            this.mOrgString = orgString;
        }

        public AsyncResp async() {
            if(mExecutor == null) {
                mExecutor = Executors.newSingleThreadExecutor();
            }
            AsyncResp async = new AsyncResp();
            execute(async);
            return async;
        }

        public SyncRet sync() {
            SyncRet sync = new SyncRet();
            execute(sync);
            return sync;
        }

        public AsyncResp async(ExecutorService executor) {
            if(executor != null) {
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
            mExecutor.execute(new NBParserTask(asyncResp,this));
        }

        private void execute(SyncRet syncRet) {
            NBParserTask task = new NBParserTask(this);
            syncRet.setPages(task.createPages());
        }
    }

     public static class SyncRet {
        private List<NBPage> mList;

        void setPages(List<NBPage> list) {
            this.mList = list;
        }

        public List<NBPage> getPages() {
            return null;
        }
    }

    public static class AsyncResp {

        private NBParseListener mListener;
        Handler handler = new Handler(Looper.getMainLooper());

        public void load(NBParseListener listener) {
            mListener = listener;
        }

        public void setPages(final List<NBPage> list) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.respPages(list);
                }
            });
        }
    }

}
