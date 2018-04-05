package reader.newbird.com.utils;

import android.util.Log;

public class Logs {
    public static void i(String tag, String message) {
        Log.i(tag, message);
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
    }


    public static void e(String tag, Throwable error) {
        Log.e(tag,"", error);
    }


    public static void d(String tag, String message) {
        Log.e(tag, message);
    }

    public static void d(String tag, Throwable error) {
        Log.e(tag,"", error);
    }

}
