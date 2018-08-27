package com.example.dai.categoryexample.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;

import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.viewmodel.DataBindingVM;
import com.example.dai.categoryexample.databinding.ActivityDataBindingBinding;

/**
 * Created by dai
 * test on Data binding
 * on 17/10/20.
 */

public class DataBindingActivity extends Activity {

    private DataBindingVM mDataBindingVM;
    private ActivityDataBindingBinding mRootBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("databinding activity","xx");
            }
        });

        mRootBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_binding);
        mRootBinding.setData(mDataBindingVM = new DataBindingVM(getWindow(),mRootBinding.textview1));

        Log.e("bad ", "onCreate: ", new Throwable());

        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                //do something
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDataBindingVM.isTextVisible.set(true);

    }
}
