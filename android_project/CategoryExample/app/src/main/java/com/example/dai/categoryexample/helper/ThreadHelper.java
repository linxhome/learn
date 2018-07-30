package com.example.dai.categoryexample.helper;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bird on 2018/7/30.
 * Comment:thread helper
 */
public class ThreadHelper {
    private static ThreadHelper mInstance;

    private ThreadHelper() {

    }



    public static ThreadHelper get() {
        if (mInstance == null) {
            synchronized (ThreadHelper.class) {
                if (mInstance == null) {
                    mInstance = new ThreadHelper();
                }
            }
        }
        return mInstance;
    }


    //单例进程
    public ExecutorService getSinglePool() {
        return Executors.newSingleThreadExecutor();
    }

    // fixed线程
    public ExecutorService getFixedPool() {
        return Executors.newFixedThreadPool(5);
    }

    //cache 无限的
    public ExecutorService getCachePool() {
        return Executors.newCachedThreadPool();//线程设置60s存活，也是通过workqueue实现的
    }



    public void test() {
        ExecutorService ser = ThreadHelper.get().getSinglePool();
        ser.execute(() -> {
            Log.i("tag", "1");
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ser.execute(() -> {
            Log.i("tag", "2");
        });
    }


}
