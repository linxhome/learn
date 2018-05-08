package com.newbird.parse.core;

import android.content.Context;

import com.newbird.parse.config.FontConfig;
import com.newbird.parse.model.NBPage;
import com.newbird.parse.task.NBParserTask;

import java.lang.ref.WeakReference;
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
            mExecutor = Executors.newSingleThreadExecutor();
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
            syncRet.setPages(task.getData());
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

        private WeakReference<NBParseListener> mListener;

        public void load(NBParseListener listener) {
            mListener = new WeakReference<>(listener);
        }

        public void setPages(List<NBPage> list) {
            NBParseListener listener = mListener.get();
            if (listener != null) {
                listener.respPages(list);
            }
        }
    }

}
