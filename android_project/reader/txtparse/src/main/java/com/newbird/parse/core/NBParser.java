package com.newbird.parse.core;

import com.newbird.parse.config.FontConfigDefault;
import com.newbird.parse.config.FontConfig;
import com.newbird.parse.model.NBPage;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NBParser {

    public static ParserWithConfig init(FontConfig config) {
        ParserWithConfig pConfig =  new ParserWithConfig(config);
        pConfig.measure(new NBMeasure.DefaultM());
        return pConfig;
    }

    public static ParserWithConfig initDefault() {
        ParserWithConfig config =  new ParserWithConfig(FontConfig.defaultConfig());
        config.measure(new NBMeasure.DefaultM());
        return config;
    }

    public static class ParserWithConfig {
        FontConfig config;
        NBMeasure measure;

        public ParserWithConfig(FontConfig config) {
            this.config = config;
        }

        public ParserWithConfig measure(NBMeasure measure) {
            this.measure = measure;
            return this;
        }

        public ParseWrapper load(String orgStr) {
            return new ParseWrapper(this, orgStr);
        }


    }

    public static class ParseWrapper {
        private ParserWithConfig mConfig;
        private String mOrgString;
        private ExecutorService mExecutor;

        public ParseWrapper(ParserWithConfig mConfig, String orgString) {
            this.mConfig = mConfig;
            this.mOrgString = orgString;
        }

        public AsyncRet async() {
            mExecutor = Executors.newSingleThreadExecutor();
            AsyncRet async = new AsyncRet();
            execute(async);
            return async;
        }

        public SyncRet sync() {
            SyncRet sync = new SyncRet();
            execute(sync);
            return sync;
        }

        public AsyncRet async(ExecutorService executor) {
            if(executor != null) {
                this.mExecutor = executor;
            } else {
                mExecutor = Executors.newSingleThreadExecutor();
            }
            AsyncRet async = new AsyncRet();
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

        private void execute(AsyncRet asyncRet) {
            mExecutor.execute(new ParserTask(asyncRet,this));
        }

        private void execute(SyncRet syncRet) {
            ParserTask task = new ParserTask(this);
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

    public static class AsyncRet {

        private WeakReference<ParseListener> mListener;

        public void into(ParseListener listener) {
            mListener = new WeakReference<>(listener);
        }

        protected void setPages(List<NBPage> list) {
            ParseListener listener = mListener.get();
            if (listener != null) {
                listener.respPages(list);
            }
        }
    }

}
