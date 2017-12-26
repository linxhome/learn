package com.example.dai.categoryexample.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by dai
 * on 17/12/26.
 */

public class ForeService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while(true) {
                    try {
                        Thread.sleep(1000);
                        Log.i("service","good");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }
}
