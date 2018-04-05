package reader.newbird.com.task;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import reader.newbird.com.book.BookFileManager;
import reader.newbird.com.utils.FileUtils;
import reader.newbird.com.utils.IOUtils;
import reader.newbird.com.utils.Logs;


/**
 * 解压并解析指定的zip文件到制定目录
 */
public class ZipBookParseTask implements Runnable {
    private static final String TAG = "ZipBookParseTask";

    private InputStream mBookInputStream;
    private String mOutputDir;


    public ZipBookParseTask(InputStream inputStream, String outputPath) {
        this.mBookInputStream = inputStream;
        this.mOutputDir = outputPath;
    }

    @Override
    public void run() {
        String bookPath = mOutputDir;
        String infoPath = bookPath + File.separator + BookFileManager.INFO_PREFIX;
        String coverPath = bookPath + File.separator + BookFileManager.COVER_PREFIX;
        String chapterPath = bookPath + File.separator + BookFileManager.CHAPTER_DIR;

        File bookDir = new File(bookPath);
        if (!bookDir.exists() || !bookDir.isDirectory()) {
            bookDir.mkdir();
        } else {
            File info = new File(infoPath);
            File cover = new File(coverPath);
            File chapter = new File(chapterPath);
            if (info.exists() && cover.exists() && chapter.exists()) {
                //不重复解析
                return;
            }
        }

        ZipInputStream zipFile = new ZipInputStream(this.mBookInputStream, Charset.forName("gbk"));
        ZipEntry fileEntry = null;
        try {
            fileEntry = zipFile.getNextEntry();
            while (fileEntry != null) {
                if (fileEntry.isDirectory()) {
                    continue;
                }
                String entryName = fileEntry.getName();
                if (entryName.startsWith(BookFileManager.COVER_PREFIX)) {
                    FileUtils.writeFromInputStream(zipFile, coverPath);
                } else if (entryName.equals(BookFileManager.INFO_PREFIX)) {
                    FileUtils.writeFromInputStream(zipFile, infoPath);
                } else if (entryName.endsWith(BookFileManager.TXT_SUFFIX)) {
                    parseToChapter(zipFile, chapterPath);
                }
                fileEntry = zipFile.getNextEntry();
            }
        } catch (IOException e) {
            Logs.e(TAG, e);
        } finally {
            IOUtils.closeQuite(zipFile);
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
                    if(write != null) {
                        write.flush();
                    }
                    IOUtils.closeQuite(write);
                    write = new FileWriter(path + File.separator + chapterIndex + BookFileManager.CHAPTER_FILE_SUFFIX);
                    write.write(line);
                } else {
                    if(write != null) {
                        write.write(line);
                    }
                }
                line = bufferedReader.readLine();
            }
            if(write != null) {
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
