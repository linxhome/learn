package com.example.dai.categoryexample.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.dai.categoryexample.R;

public class EmptyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emtpy);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("keyxxx",111);
    }
}
