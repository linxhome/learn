package com.example.dai.categoryexample.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.service.ForeService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * activity for the test input
 */
public class MainActivity extends Activity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listview);

        final Map<String, Class> activitySet = new LinkedHashMap<>();
        activitySet.put("FragmentManager", FragmentManagerActivity.class);
        activitySet.put("DataBinding", DataBindingActivity.class);
        activitySet.put("Start Service", ForeService.class);
        activitySet.put("Permission", PermissionActivity.class);
        activitySet.put("Empty Page", EmptyActivity.class);

        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Window window = getWindow();
                Log.e("windowstag", "window is null " + (window == null));
            }
        }, 8000);

        int size = activitySet.keySet().size();
        final String[] titles = activitySet.keySet().toArray(new String[size]);

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        mListView.setAdapter(adapter);

        getSharedPreferences("x", MODE_PRIVATE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {
                    TextView txtView = (TextView) view;
                    CharSequence txtStr = txtView.getText();
                    Toast.makeText(MainActivity.this, "got " + txtStr, Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                intent.setClass(getBaseContext(), activitySet.get(titles[position]));
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                String className = activitySet.get(titles[position]).getName();
                if (className.contains("Service")) {
                    startService(intent);
                } else {
                    startActivity(intent);
                }
                myPackageName();
            }
        });


    }

    @SuppressLint("NewApi")
    private void myPackageName() {
        PackageManager packageManager = getPackageManager();
        CharSequence appLabel = getApplication().getApplicationInfo().loadLabel(packageManager);

        String name1 = getPackageName();
        String name2 = getApplication().getPackageName();
        String name3 = appLabel.toString();

        Log.e("packagename", name1 + "-" + name2 + "-" + name3);


    }


}
