package com.example.dai.categoryexample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        final Map<String,Class> activitySet = new LinkedHashMap<>();
        activitySet.put("Layout Params",ParamsActivity.class);
        activitySet.put("Constrains Layout",ConstraintActivity.class);
        activitySet.put("DataBinding",DataBindingActivity.class);
        activitySet.put("Storage File",StorageActivity.class);
        activitySet.put("Dialog Problem",DialogActivity.class);
        activitySet.put("Start Service", ForeService.class);
        activitySet.put("Permission",PermissionActivity.class);

        int size = activitySet.keySet().size();
        final String[] titles = activitySet.keySet().toArray(new String[size]);

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {
                    TextView txtView = (TextView) view;
                    CharSequence txtStr = txtView.getText();
                    Toast.makeText(MainActivity.this, "got " + txtStr, Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                intent.setClass(getBaseContext(),activitySet.get(titles[position]));
                String className = activitySet.get(titles[position]).getName();
                if(className.contains("Service")) {
                    startService(intent);
                } else {
                    startActivity(intent);
                }
            }
        });
    }
}
