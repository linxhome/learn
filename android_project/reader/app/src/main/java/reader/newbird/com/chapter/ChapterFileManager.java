package reader.newbird.com.chapter;

import android.os.AsyncTask;


import com.newbird.parse.model.NBPage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import reader.newbird.com.base.ThreadManager;
import reader.newbird.com.book.BookManager;
import reader.newbird.com.book.BookModel;
import reader.newbird.com.utils.FileUtils;

public class ChapterFileManager {

    public static void parseBaseChapterInfo(BookModel bookInfo, int chapterSeq, IGetChapter callback)  {
        ChapterModel simpleChapter = ChapterModel.obtain(bookInfo,chapterSeq);
        new BaseChapterParesTask(callback).executeOnExecutor(ThreadManager.getInstance().getIOThread(),simpleChapter);
    }

    public static void parsePages(ChapterModel chapterInfo,IGetChapter callback) {
        new PageParseTask(callback).executeOnExecutor(ThreadManager.getInstance().getIOThread(),chapterInfo);
    }

    static class BaseChapterParesTask extends AsyncTask<ChapterModel, Integer, ChapterModel> {

        private IGetChapter mDataObservable;

        public BaseChapterParesTask(IGetChapter mDataObservable) {
            this.mDataObservable = mDataObservable;
        }

        @Override
        protected ChapterModel doInBackground(ChapterModel... chapterModels) {
            ChapterModel chapterInfo = chapterModels[0];
            if(chapterInfo == null ) {
                throw new RuntimeException("null chapter info");
            }
            BookModel bookInfo = chapterInfo.bookModel;
            String chapterFilePath = bookInfo.bookDir + File.separator + BookManager.CHAPTER_DIR + File.separator + chapterInfo.chapterFileName;
            File chapterFile = new File(chapterFilePath);
            if (!chapterFile.exists() || chapterFile.isDirectory()) {
                throw new RuntimeException(new IOException("bad chapter file"));
            }
            String content = FileUtils.readFileToString(chapterFilePath);
            chapterInfo.content = content;
            //根据前面的解析，第一行一定是标题
            int titleIndex = content != null ? content.indexOf("\n") : -1;
            chapterInfo.title = titleIndex >= 0 ? content.substring(0,titleIndex) : chapterInfo.chapterSeq + "";
            // get the list
            //chapterInfo.pageList = parseChapterToList(chapterInfo);
            return chapterInfo;
        }


        @Override
        protected void onPostExecute(ChapterModel chapterModel) {
            super.onPostExecute(chapterModel);
            if(mDataObservable != null) {
                mDataObservable.onGetChapterInfo(chapterModel);
            }
        }

        public List<NBPage> parseChapterToList(ChapterModel chapterInfo) {
            //todo parseChapterToList
            return null;
        }
    }

    static class PageParseTask extends AsyncTask<ChapterModel,Integer,ChapterModel> {
        private IGetChapter mDataObservable;

        public PageParseTask(IGetChapter mDataObservable) {
            this.mDataObservable = mDataObservable;
        }

        @Override
        protected ChapterModel doInBackground(ChapterModel... chapterModels) {
            return null;
        }


    }




}
