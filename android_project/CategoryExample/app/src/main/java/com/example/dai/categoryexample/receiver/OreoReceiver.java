package com.example.dai.categoryexample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by juliandai on 2018/1/31.
 * Comment:
 */

public class OreoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("OreoReceiver","i can get");
    }
}
