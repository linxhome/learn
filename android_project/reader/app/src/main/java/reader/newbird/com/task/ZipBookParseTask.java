package reader.newbird.com.task;

import android.text.TextUtils;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reader.newbird.com.book.BookManager;
import reader.newbird.com.utils.FileUtils;
import reader.newbird.com.utils.IOUtils;
import reader.newbird.com.utils.Logs;


/**
 * 解压并解析指定的zip文件到制定目录
 */
public class ZipBookParseTask implements Runnable {
    private static final String TAG = "ZipBookParseTask";

    private String mZipPath;
    private String mOutputDir;
    private List<String> mTitles = new ArrayList<>();


    public ZipBookParseTask(String zipPath, String outputPath) {
        this.mZipPath = zipPath;
        this.mOutputDir = outputPath;
    }

    @Override
    public void run() {
        String bookPath = mOutputDir;
        String infoPath = bookPath + File.separator + BookManager.INFO_PREFIX;
        String coverPath = bookPath + File.separator + BookManager.COVER_PREFIX;
        String chapterPath = bookPath + File.separator + BookManager.CHAPTER_DIR;

        File bookDir = new File(bookPath);
        if (!bookDir.exists() || !bookDir.isDirectory()) {
            bookDir.mkdir();
        } else {
            File info = new File(infoPath);
            File cover = new File(coverPath);
            File chapter = new File(chapterPath);
           /* if (info.exists() && cover.exists() && chapter.exists()) {
                return;
            }*/
        }

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(mZipPath, "GBK");
            Enumeration<ZipEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                InputStream entryInputStream = zipFile.getInputStream(entry);
                if (entryName.startsWith(BookManager.COVER_PREFIX)) {
                    FileUtils.putInputStream(entryInputStream, coverPath);
                } else if (entryName.equals(BookManager.INFO_PREFIX)) {
                    FileUtils.putInputStream(entryInputStream, infoPath);
                } else if (entryName.endsWith(BookManager.TXT_SUFFIX)) {
                    parseToChapter(entryInputStream, chapterPath);
                }
            }
        } catch (IOException e) {
            Logs.e(TAG, e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {

                }
            }
        }

        String infJson = FileUtils.getContent(infoPath);
        if (TextUtils.isEmpty(infJson)) {
            return;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(infJson);
            jsonObject.put("category", new JSONArray(mTitles));
        } catch (JSONException e) {
            Logs.e(TAG, e);
        }
        if (jsonObject != null) {
            FileUtils.putContent(infoPath, jsonObject.toString(), false);
        }
    }

    private void parseToChapter(InputStream inputStream, String path) {
        File chapterDir = new File(path);
        if (!chapterDir.exists() || !chapterDir.isDirectory()) {
            chapterDir.mkdir();
        }
        InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("gb2312"));
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            int chapterIndex = 0;
            String line = bufferedReader.readLine();
            FileWriter write = null;
            while (line != null) {
                if (isChapterTitle(line)) {
                    //make the new file
                    chapterIndex++;
                    if (write != null) {
                        write.flush();
                    }
                    IOUtils.closeQuite(write);
                    write = new FileWriter(path + File.separator + chapterIndex + BookManager.CHAPTER_FILE_SUFFIX);
                    write.write(line + "\n");
                    mTitles.add(line);
                } else {
                    if (write != null) {
                        write.write(line + "\n");
                    }
                }
                line = bufferedReader.readLine();
            }
            if (write != null) {
                write.flush();
            }
            IOUtils.closeQuite(write);
        } catch (IOException e) {
            Logs.d(TAG, e);
        }
    }

    private boolean isChapterTitle(String line) {
        if (TextUtils.isEmpty(line)) {
            return false;
        }
        String[] column = line.trim().split("\\s");
        if (column.length > 0) {
            String firstColumn = column[0].trim();
            return matchChapter(firstColumn);
        } else {
            return false;
        }
    }

    private boolean matchChapter(String title) {
        if (TextUtils.isEmpty(title)) {
            return false;
        }
        String headPattern = "第0123456789零一二三四五六七八九十百千前序";
        if (headPattern.indexOf(title.charAt(0)) == -1) {
            //先排除首字符不匹配的
            return false;
        }
        //任一个全匹配都算章节名
        List<String> patterns = new ArrayList<String>();
        patterns.add("第[0123456789]+章");
        patterns.add("第[0123456789]+");
        patterns.add("[0123456789]+章");
        patterns.add("第[零一二三四五六七八九十百千]+章");
        patterns.add("[零一二三四五六七八九十百千]+章");
        patterns.add("[零一二三四五六七八九十百千]+");
        patterns.add("第[零一二三四五六七八九十百千]+");
        patterns.add("前言");
        patterns.add("序言");
        patterns.add("序");
        for (String regx : patterns) {
            Pattern pattern = Pattern.compile(regx);
            Matcher matcher = pattern.matcher(title);
            if (matcher.matches()) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }

}
