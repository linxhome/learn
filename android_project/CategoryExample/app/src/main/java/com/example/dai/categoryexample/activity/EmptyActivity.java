package com.example.dai.categoryexample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.dai.categoryexample.R;

public class EmptyActivity extends Activity {
    TextView pendingTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emtpy);
        pendingTxt = (TextView) findViewById(R.id.pending_txt);



        String mykey = getIntent().getStringExtra("mykey");
        pendingTxt.setText(mykey);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
