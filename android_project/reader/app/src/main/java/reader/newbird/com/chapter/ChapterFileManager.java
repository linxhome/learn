package reader.newbird.com.chapter;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

import reader.newbird.com.book.BookModel;
import reader.newbird.com.utils.FileUtils;

public class ChapterFileManager {


    public static ChapterModel parseChapter(BookModel bookInfo, int chapterSeq) throws IOException {
        ChapterModel chapterInfo = ChapterModel.obtain(bookInfo, chapterSeq);


        return chapterInfo;
    }

    static class ChapterParesTask extends AsyncTask<ChapterModel, Integer, ChapterModel> {

        private IGetChapter mDataObeservable;

        @Override
        protected ChapterModel doInBackground(ChapterModel... chapterModels) {
            ChapterModel chapterInfo = chapterModels[0];
            BookModel bookInfo = chapterInfo.bookModel;
            String chapterFilePath = bookInfo.bookDir + File.separator + bookInfo.chapterSeqs.get(chapterSeq);
            File chapterFile = new File(chapterFilePath);
            if (!chapterFile.exists() || chapterFile.isDirectory()) {
                throw new IOException("bad chapter file");
            }
            chapterInfo.content = FileUtils.readFileToString(chapterFilePath);

            return null;
        }
    }
}
