package com.example.dai.categoryexample.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.dai.categoryexample.R;

/**
 * Created by dai
 * on 18/1/2.
 */

public class PermissionActivity extends Activity {
    TextView txt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calander);
        View btn = findViewById(R.id.get_perm_btn);
        txt = (TextView) findViewById(R.id.show_info);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryCalander();
            }
        });
    }

    String[] projection = new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME};

    private void queryCalander() {
        Cursor cursor = null;
        ContentResolver resolver = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 110);
            } else {
                cursor = resolver.query(uri, projection, null, null, null);
            }
        } else {
            //低于M系统的时候不需要判断权限
            cursor = resolver.query(uri, projection, null, null, null);
        }
        StringBuffer buffer = new StringBuffer();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long cid = 0;
                String accountName = null;
                cid = cursor.getLong(0);
                accountName = cursor.getString(1);
                buffer.append("cid is ").append(cid);
                buffer.append("account name ").append(accountName);
                buffer.append("\n");
            }
            txt.setText(buffer.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            queryCalander();
        }
    }
}
