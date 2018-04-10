package reader.newbird.com.chapter;

import com.newbird.parse.model.PageModel;

import reader.newbird.com.book.BookModel;

public class ChapterPresenter implements IGetChapter {
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
        ChapterFileManager.parseBaseChapterInfo(mBookInfo, seq, this);
    }

    /**
     * 获得分页后的分页列表
     * @param chapterInfo
     */
    public void getPages(ChapterModel chapterInfo) {

    }

    public void getPageBitmap(PageModel pageData) {

    }

    public void onDestroy() {
        this.mChapterPageView = null;
    }


    @Override
    public void onGetChapterInfo(ChapterModel chapterInfo) {
        if (mChapterPageView != null) {
            mChapterPageView.onGetChapterInfo(chapterInfo);
        }
    }
}
