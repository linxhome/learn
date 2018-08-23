package com.example.dai.categoryexample.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dai.categoryexample.R;

import java.io.File;

/**
 * Created by dai on 2018/5/8.
 * Comment:
 */
public class StoreageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_four, container, false);

        Button btn1 = (Button) root.findViewById(R.id.btn1);
        Button btn2 = (Button) root.findViewById(R.id.btn2);
        Button btn3 = (Button) root.findViewById(R.id.btn3);
        Button btn4 = (Button) root.findViewById(R.id.btn4);
        Button btn5 = (Button) root.findViewById(R.id.btn5);
        Button btn6 = (Button) root.findViewById(R.id.btn6);
        Button btn7 = (Button) root.findViewById(R.id.btn7);

        final TextView showView = (TextView) root.findViewById(R.id.show_info);

        btn1.setOnClickListener(v -> showView.setText(getActivity().getFilesDir().getAbsolutePath()));


        btn2.setOnClickListener(v -> showView.setText(getActivity().getExternalCacheDir().getAbsolutePath()));


        btn3.setOnClickListener(v -> showView.setText(Environment.getDataDirectory().getAbsolutePath()));


        btn4.setOnClickListener(v -> showView.setText(Environment.getExternalStorageDirectory().getAbsolutePath()));


        btn5.setOnClickListener(v -> showView.setText(Environment.getRootDirectory().getAbsolutePath()));


        btn7.setOnClickListener(v -> showView.setText(getActivity().getExternalFilesDir("my").getAbsolutePath()));

        btn6.setOnClickListener(v -> {
            String path = "/data/data/com.example.dai.categoryexample/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            boolean canwrite = file.canWrite();
            boolean catRead = file.canRead();
            Toast.makeText(getActivity(), "cant write " + canwrite + " cant read " + catRead, Toast.LENGTH_SHORT).show();

            try {
                SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(path + "1.db", null);
                database.beginTransaction();
                database.execSQL("create table aa (id int)");
                database.endTransaction();
                database.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        return root;
    }

}
