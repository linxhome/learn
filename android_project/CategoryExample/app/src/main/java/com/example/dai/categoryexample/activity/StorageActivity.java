package com.example.dai.categoryexample.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dai.categoryexample.R;

import java.io.File;

/**
 * Created by dai
 * on 17/11/29.
 */

public class StorageActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);

        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);
        Button btn3 = (Button) findViewById(R.id.btn3);
        Button btn4 = (Button) findViewById(R.id.btn4);
        Button btn5 = (Button) findViewById(R.id.btn5);
        Button btn6 = (Button) findViewById(R.id.btn6);

        final TextView showView = (TextView) findViewById(R.id.show_info);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView.setText(getApplicationContext().getFilesDir().getAbsolutePath());
            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView.setText(getBaseContext().getExternalCacheDir().getAbsolutePath());
            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView.setText(Environment.getDataDirectory().getAbsolutePath());
            }
        });


        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView.setText(Environment.getExternalStorageDirectory().getAbsolutePath());
            }
        });


        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView.setText(Environment.getRootDirectory().getAbsolutePath());
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = "/data/data/com.example.dai.categoryexample/";
                File file = new File(path);
                if(!file.exists())
                {
                    file.mkdir();
                }
                boolean canwrite = file.canWrite();
                boolean catRead = file.canRead();
                Toast.makeText(StorageActivity.this, "cant write "+ canwrite + " cant read " + catRead, Toast.LENGTH_SHORT).show();

                try {
                    SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(path + "1.db", null);
                    database.beginTransaction();
                    database.execSQL("create table aa (id int)");
                    database.endTransaction();
                    database.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }




            }
        });


    }
}
