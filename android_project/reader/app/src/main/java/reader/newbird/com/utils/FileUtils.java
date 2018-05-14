package reader.newbird.com.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bird on 2018/4/1.
 * Comment:
 */
public class FileUtils {
    private static final String TAG = "FileUtils";


    public static void putInputStream(InputStream inputStream, String filePath) {
        File file = new File(filePath);
        FileOutputStream write = null;
        try {
            write = new FileOutputStream(file);
            byte[] by = new byte[4096];
            int size = 0;
            while ((size = inputStream.read(by)) > 0) {
                write.write(by, 0, size);
            }
            write.flush();
        } catch (IOException e) {
            Logs.d(TAG, e);
        } finally {
            IOUtils.closeQuite(write);
        }
    }

    public static String getContent(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            StringBuilder stringBuilder = new StringBuilder();
            byte[] by = new byte[4096];
            do {
                int re = inputStream.read(by);
                if (re > 0) {
                    stringBuilder.append(new String(by));
                } else {
                    break;
                }
            } while (true);
            return stringBuilder.toString();
        } catch (IOException e) {
            Logs.d("FileUtils", e);
            return null;
        } finally {
            IOUtils.closeQuite(inputStream);
        }
    }

    public static void putContent(String filePath, String content, boolean isAppend) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(content)) {
            return;
        }
        FileWriter write = null;
        try {
            write = new FileWriter(new File(filePath), isAppend);
            write.write(content);
            write.flush();
        } catch (IOException e) {
            Logs.e(TAG, e);
        } finally {
            IOUtils.closeQuite(write);
        }
    }

}
