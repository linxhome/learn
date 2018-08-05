package com.example.dai.categoryexample.task;

import android.os.AsyncTask;

/**
 * Created by bird on 2018/7/30.
 * Comment: async task test example
 */
public class ExampleTask extends AsyncTask<ExampleTask.Pramas, ExampleTask.Progress, ExampleTask.Result> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Result doInBackground(Pramas... pramas) {
        return null;
    }

    static class Pramas {

    }

    static class Progress {

    }

    static class Result {

    }

    static {
        ExampleTask task = new ExampleTask();
        task.execute(new Pramas(), new Pramas());

    }
}


