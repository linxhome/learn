package com.example.dai.categoryexample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.dai.categoryexample.R;

public class EmptyActivity extends Activity {
    TextView pendingTxt;
    public static final String TAG = "EmptyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emtpy);
        pendingTxt = (TextView) findViewById(R.id.pending_txt);

        Log.i(TAG, "onCreate");

        String mykey = getIntent().getStringExtra("mykey");
        pendingTxt.setText(mykey);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");
    }
}
