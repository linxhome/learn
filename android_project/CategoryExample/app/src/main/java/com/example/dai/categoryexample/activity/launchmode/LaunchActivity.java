package com.example.dai.categoryexample.activity.launchmode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.fragment.LaunchModeFragment;

public abstract class LaunchActivity extends FragmentActivity {

    String getTag() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emtpy);
        getActionBar().hide();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(android.R.id.content, LaunchModeFragment.newInstance(getTag(), getIntent().getStringExtra("flag")));
        transaction.commit();

        runOnUiThread(() -> Toast.makeText(getBaseContext(), "onCreate " + getTag(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(getBaseContext(), "onNew Intent " + getTag(), Toast.LENGTH_SHORT).show();
    }
}
