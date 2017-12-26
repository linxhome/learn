package com.example.dai.categoryexample.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.dai.categoryexample.R;

/**
 * Created by dai
 * on 17/12/25.
 */

public class DialogActivity extends Activity {

    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.setTitle("试试").setMessage("打开后消失").create();
        final Task task = new Task();
        findViewById(R.id.dialog_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!task.getStatus().equals(AsyncTask.Status.FINISHED)) {
                    task.execute();
                }
            }
        });

    }

    class Task extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... objects) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.hide();
        }
    }

}
