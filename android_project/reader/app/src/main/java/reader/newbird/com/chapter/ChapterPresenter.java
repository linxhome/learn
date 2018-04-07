package reader.newbird.com.chapter;

import java.io.File;

import reader.newbird.com.book.BookModel;

public class ChapterPresenter {
    private BookModel mBookInfo;
    private IGetChapter mChapterPageView;

    public ChapterPresenter(BookModel mBookInfo) {
        this.mBookInfo = mBookInfo;
    }

    /**
     * 获得章节基本信息
     */
    public void getChapterInfo(int seq) {
        if (mBookInfo == null || !mBookInfo.isValid()) {
            return;
        }
        if (seq > mBookInfo.chapterSeqs.size()) {
            return;
        }
        String chapterPath = mBookInfo.bookDir + File.separator + mBookInfo.chapterSeqs.get(seq);
        ChapterFileManager.parseChapter(chapterPath);


    }

    public void getPageBitmap(ChapterPageModel pageData) {

    }

    public void getPages(ChapterModel chapterInfo) {

    }

    public void onDestroy() {
        this.mChapterPageView = null;
    }


}
