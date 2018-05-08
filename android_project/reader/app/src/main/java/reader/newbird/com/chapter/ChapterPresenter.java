package reader.newbird.com.chapter;

import android.content.Context;
import android.graphics.Bitmap;

import com.newbird.parse.core.NBParseListener;
import com.newbird.parse.core.NBParserCore;
import com.newbird.parse.model.NBPage;

import reader.newbird.com.book.BookModel;

public class ChapterPresenter implements IGetChapter {
    private BookModel mBookInfo;
    private IGetChapter mChapterPageView;
    private Context mContext;

    public ChapterPresenter(Context context,BookModel mBookInfo) {
        this.mContext = context;
        this.mBookInfo = mBookInfo;
    }

    public void setChapterPageView(IGetChapter mChapterPageView) {
        this.mChapterPageView = mChapterPageView;
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
    public void getPages(ChapterModel chapterInfo,NBParseListener listener) {
        NBParserCore.initDefault(mContext).setContent(chapterInfo.content).async().load(listener);
    }

    public Bitmap getPageBitmap(NBPage page) {
        //from page data to bitmap
        return null;
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
