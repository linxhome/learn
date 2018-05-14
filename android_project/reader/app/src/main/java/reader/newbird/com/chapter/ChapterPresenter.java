package reader.newbird.com.chapter;

import android.content.Context;

import com.newbird.parse.core.NBParseListener;
import com.newbird.parse.core.NBParserCore;

import reader.newbird.com.book.BookModel;

public class ChapterPresenter implements IGetChapter {
    private BookModel mBookInfo;
    private IGetChapter mChapterPageView;
    private Context mContext;

    public ChapterPresenter(Context context,BookModel mBookInfo) {
        this.mContext = context;
        this.mBookInfo = mBookInfo;
    }

    public void setViewCallback(IGetChapter mChapterPageView) {
        this.mChapterPageView = mChapterPageView;
    }

    /**
     * 获得章节基本信息
     */
    public void getChapterModel(int seq) {
        if (mBookInfo == null || !mBookInfo.isValid()) {
            return;
        }
        if (seq > mBookInfo.chapterPaths.size()) {
            return;
        }
        ChapterManager.getChapterModel(mBookInfo, seq, this);
    }


    /**
     * 获得分页后的分页列表
     * @param chapterInfo
     */
    public void getPages(ChapterModel chapterInfo,NBParseListener listener) {
        NBParserCore.initDefault(mContext).setContent(chapterInfo.content).async().load(listener);
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
