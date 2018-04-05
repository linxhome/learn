package reader.newbird.com.book;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import reader.newbird.com.base.ReaderContext;
import reader.newbird.com.config.PathConstant;
import reader.newbird.com.task.ZipBookParseTask;
import reader.newbird.com.utils.FileUtils;
import reader.newbird.com.utils.Logs;

/**
 * 书籍文件管理
 */
public class BookFileManager {
    private static final String TAG = "BookFileManager";

    public static final String COVER_PREFIX = "cover";//封面文件前缀
    public static final String INFO_PREFIX = "info";//书籍详情文件前缀

    public static final String TXT_SUFFIX = ".txt";//书籍内容文件前缀
    public static final String CHAPTER_DIR = "chapters";//章节文件目录
    public static final String CHAPTER_FILE_SUFFIX = ".cp";//章节文件后缀

    /**
     * 从 PathConstant.BOOK_STORE_DIR 目录中读取书籍
     *
     * @return List<BookModel>
     */
    public List<BookModel> getLocalBooks() {
        String rootBookDir = PathConstant.BOOK_STORE_DIR;
        List<BookModel> books = new ArrayList<>();
        File file = new File(rootBookDir);
        if (file.exists() && file.isDirectory()) {
            File[] dirs = file.listFiles();
            for (File bookDir : dirs) {
                BookModel book = getBook(bookDir.getAbsolutePath());
                if (book != null && book.isValid()) {
                    books.add(book);
                }
            }
        }
        return books;
    }

    private static BookModel getBook(String path) {
        File bookDir = new File(path);
        File[] files = bookDir.listFiles();
        BookModel bookModel = BookModel.obtain();
        bookModel.bookDir = path;
        for (File file : files) {
            String filename = file.getName();
            if (filename.equals(COVER_PREFIX)) {
                bookModel.cover = Uri.parse("file:" + path + File.separator + COVER_PREFIX);
            } else if (filename.equals(INFO_PREFIX)) {
                String infoJson = FileUtils.readFileToString(file.getAbsolutePath());
                try {
                    JSONObject jsonObj = new JSONObject(infoJson);
                    bookModel.authorName = jsonObj.getString("author_name");
                    bookModel.bookName = jsonObj.getString("book_name");
                    bookModel.detail = jsonObj.getString("detail");
                } catch (JSONException e) {
                    Logs.d(TAG, e);
                }
            } else if (filename.equals(CHAPTER_DIR)) {
                if (file.isDirectory()) {
                    String[] chapters = file.list();
                    List<String> chapterSeqs = Arrays.asList(chapters);
                    Collections.sort(chapterSeqs);
                    bookModel.chapterSeqs = chapterSeqs;
                }
            }
        }
        return bookModel;
    }

    public AssetsFileAsyncTask initAssetBook(IGetBook listener) {
        return new AssetsFileAsyncTask(listener);
    }

    static class AssetsFileAsyncTask extends AsyncTask<String, String, Boolean> {
        IGetBook dataListener;

        private AssetsFileAsyncTask(IGetBook listener) {
            this.dataListener = listener;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            AssetManager assetFile = ReaderContext.getInstance().get().getAssets();
            try {
                String[] files = assetFile.list(PathConstant.LOCAL_BOOK_DIR_NAME);
                if (files == null) {
                    return false;
                }
                for (String bookName : files) {
                    InputStream inputStream = assetFile.open(PathConstant.LOCAL_BOOK_DIR_NAME + File.separator + bookName);
                    String outputPath = PathConstant.BOOK_STORE_DIR + File.separator + bookName;
                    Runnable task = new ZipBookParseTask(inputStream, outputPath);
                    task.run();
                    publishProgress(outputPath);
                }
            } catch (IOException e) {
                Logs.e("", e);
                return false;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values != null && values.length > 0) {
                String bookPath = values[0];
                BookModel book = getBook(bookPath);
                if (dataListener != null) {
                    dataListener.updateBook(book);
                }
            }
        }

    }


}
