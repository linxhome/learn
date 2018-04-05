package reader.newbird.com.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bird on 2018/4/1.
 * Comment:
 */
public class FileUtils {
    private static final String TAG = "FileUtils";


    public static void writeFromInputStream(InputStream inputStream, String filePath) throws IOException {
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

    public static String readFileToString(String filePath) {
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

}
