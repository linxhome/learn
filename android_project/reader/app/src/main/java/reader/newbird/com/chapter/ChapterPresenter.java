package reader.newbird.com.chapter;

import android.content.Context;
import android.util.LruCache;

import com.newbird.parse.config.FontConfig;
import com.newbird.parse.core.NBParseListener;
import com.newbird.parse.core.NBParserCore;

import reader.newbird.com.book.BookModel;

public class ChapterPresenter implements IGetChapter {
    private BookModel mBookInfo;
    private IGetChapter mChapterPageView;
    private Context mContext;
    private static final int CACHE_SIZE = 10;
    private LruCache<Integer, ChapterModel> mCacheChapters = new LruCache<>(CACHE_SIZE);
    private FontConfig mFontConfig;


    public ChapterPresenter(Context context, BookModel mBookInfo) {
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
        if (seq > mBookInfo.chapterFiles.size()) {
            return;
        }
        ChapterModel cacheChapter = mCacheChapters.get(seq);
        if (cacheChapter != null) {
            onGetChapterInfo(cacheChapter);
        } else {
            ChapterManager.getChapterModel(mBookInfo, seq, this);
        }
    }


    public FontConfig getFontConfig() {
        //todo read from the db
        if (mFontConfig == null) {
            mFontConfig = FontConfig.defaultConfig(mContext);
        }
        return mFontConfig;
    }

    public void setFontConfig(FontConfig fontConfig) {
        this.mFontConfig = fontConfig;
    }


    /**
     * 获得分页后的分页列表
     *
     * @param chapterInfo
     */
    public void getPages(ChapterModel chapterInfo, NBParseListener listener) {
        NBParserCore.init(getFontConfig()).setStartReadPosition(chapterInfo.startReadPosition)
                .setContent(chapterInfo.content).async().into(listener);
    }

    public void onDestroy() {
        this.mChapterPageView = null;
    }


    @Override
    public void onGetChapterInfo(ChapterModel chapterInfo) {
        if (mChapterPageView != null) {
            mChapterPageView.onGetChapterInfo(chapterInfo);
        }
        if (chapterInfo != null) {
            mCacheChapters.put(chapterInfo.chapterSeq, chapterInfo);
        }
    }


}
