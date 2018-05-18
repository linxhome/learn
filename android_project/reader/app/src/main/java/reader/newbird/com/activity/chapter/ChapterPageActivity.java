package reader.newbird.com.activity.chapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.newbird.parse.config.ColorConfig;
import com.newbird.parse.config.FontConfig;
import com.newbird.parse.model.NBPage;

import java.util.Arrays;

import reader.newbird.com.R;
import reader.newbird.com.book.BookModel;
import reader.newbird.com.chapter.ChapterModel;
import reader.newbird.com.chapter.ChapterPresenter;
import reader.newbird.com.chapter.IGetChapter;
import reader.newbird.com.config.IntentConstant;
import reader.newbird.com.utils.Logs;
import reader.newbird.com.view.ViewCombineGroup;

public class ChapterPageActivity extends AppCompatActivity implements IGetChapter, PageClickListener, ColorStyleAdapter.ColorStyleListener {

    private RecyclerView mPageRecyclerView;
    private View mBottomMenu;
    private View mHeadMenu;
    private TextView mChapterTitle;
    private NavigationView mDrawerNavigation;
    private DrawerLayout mDrawerLayout;

    private ViewCombineGroup mMenuGroup;
    private ViewCombineGroup mChapterSeekGroup;
    private ViewCombineGroup mFontSetGroup;
    private ViewCombineGroup mColorSetGroup;

    //进度设置页
    private TextView mSwitchChapterTitle;

    //背景颜色选择
    private RecyclerView mColorSelectListView;

    //字体大小选择
    private View mSmallerFontBtn;
    private View mLargerFontBtn;
    private SeekBar mFontSizeSeekBar;

    private int mCurrentChapterSeq;
    private int mReadPositionOfChapter;//当前阅读的位置，index标识的是在整个章节字符串中的位置
    private BookModel mBookInfo;
    private ChapterPresenter mDataPresenter;
    private PageAdapter mPageAdapter;
    private LinearLayoutManager mPageManager;
    private static final String TAG = "ChapterPageActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chapter_page);

        initData();
        initView();
        initDrawer();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mPageAdapter.getItemCount() <= 0) {
            jumpToChapter(mCurrentChapterSeq);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        mBookInfo = intent.getParcelableExtra(IntentConstant.PARCEL_BOOK_MODEL);
        if (mBookInfo == null) {
            Logs.e("ChapterPageActivity", "empty book");
            finish();
            return;
        }
        mCurrentChapterSeq = intent.getIntExtra(IntentConstant.PARAM_CHAPTER_ID, 1);
        mDataPresenter = new ChapterPresenter(this, mBookInfo);
        mDataPresenter.setViewCallback(this);
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mPageRecyclerView = (RecyclerView) findViewById(R.id.page_list);
        mHeadMenu = findViewById(R.id.menu_header);
        mBottomMenu = findViewById(R.id.menu_bottom);
        mChapterTitle = (TextView) findViewById(R.id.chapter_title);
        mDrawerNavigation = (NavigationView) findViewById(R.id.content_navi);
        mChapterTitle = (TextView) mHeadMenu.findViewById(R.id.chapter_title);
        mMenuGroup = ViewCombineGroup.createGroup(mHeadMenu, mBottomMenu);

        View backPress = mHeadMenu.findViewById(R.id.back_press);
        View bottomCategoryBtn = findViewById(R.id.category_bottom_btn);
        View bottomFontBtn = findViewById(R.id.font_bottom_btn);
        View bottomProgressBtn = findViewById(R.id.progress_bottom_btn);
        View bottomStyleBtn = findViewById(R.id.setting_bottom_btn);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mPageRecyclerView);
        mPageAdapter = new PageAdapter(mDataPresenter);
        mPageRecyclerView.setAdapter(mPageAdapter);
        mPageAdapter.setClickListener(this);
        mPageManager = new LinearLayoutManager(this);
        mPageManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPageRecyclerView.setLayoutManager(mPageManager);

        mPageRecyclerView.setRecyclerListener(holder -> {
            //todo here to recycle the bitmap

        });

        mPageRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = mPageManager.findFirstCompletelyVisibleItemPosition();
                    int chapterSeq = mPageAdapter.findChapterSeqByPosition(position);
                    if (chapterSeq != mCurrentChapterSeq) {
                        onCurrentChapterChange(chapterSeq);
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mMenuGroup.hide();
                }
            }
        });

        backPress.setOnClickListener(v -> finish());

        bottomCategoryBtn.setOnClickListener(v -> {
            mMenuGroup.hide();
            mDrawerLayout.openDrawer(mDrawerNavigation);
        });

        initChapterSeekBar();
        bottomProgressBtn.setOnClickListener(v -> mChapterSeekGroup.toggleShow());

        initColorSelector();
        bottomStyleBtn.setOnClickListener(v -> mColorSetGroup.toggleShow());

        initFontSizeSelect();
        bottomFontBtn.setOnClickListener(v -> mFontSetGroup.toggleShow());

        Handler handler = new Handler();
        handler.postDelayed(() -> mMenuGroup.hide(), 3000);

    }


    //初始化抽屉目录
    private void initDrawer() {
        if (mBookInfo == null) {
            return;
        }
        ImageView cover = (ImageView) mDrawerNavigation.getHeaderView(0).findViewById(R.id.item_cover);
        Glide.with(this).load(mBookInfo.cover).into(cover);
        TextView authorText = (TextView) mDrawerNavigation.getHeaderView(0).findViewById(R.id.item_author_name);
        authorText.setText(mBookInfo.authorName);
        TextView bookText = (TextView) mDrawerNavigation.getHeaderView(0).findViewById(R.id.item_book_name);
        bookText.setText(mBookInfo.bookName);

        if (mBookInfo.titles == null || mBookInfo.chapterFiles == null) {
            return;
        }
        int chapterSize = mBookInfo.chapterFiles.size();
        int titleSize = mBookInfo.titles.size();
        if (chapterSize > titleSize) {
            mBookInfo.chapterFiles = mBookInfo.chapterFiles.subList(0, titleSize);
        } else if (chapterSize < titleSize) {
            mBookInfo.titles = mBookInfo.titles.subList(0, chapterSize);
        }

        NavigationMenuView navigationMenuView = (NavigationMenuView) mDrawerNavigation.getChildAt(0);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }

        mDrawerNavigation.getMenu().clear();
        for (String title : mBookInfo.titles) {
            mDrawerNavigation.getMenu().add(title);
        }

        mDrawerNavigation.setNavigationItemSelectedListener(item -> {
            String itemTitle = item.getTitle().toString();
            int chapterSeq = mBookInfo.titles.indexOf(itemTitle) + 1;
            jumpToChapter(chapterSeq);

            mDrawerLayout.closeDrawer(mDrawerNavigation);
            return false;
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mMenuGroup.hide();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    //初始化进度设置view
    private void initChapterSeekBar() {
        mSwitchChapterTitle = (TextView) mBottomMenu.findViewById(R.id.switch_chapter_title);
        View preChapterBtn = mBottomMenu.findViewById(R.id.pre_chapter_btn);
        View nextChapterBtn = mBottomMenu.findViewById(R.id.next_chapter_btn);
        SeekBar chapterSeekBar = (SeekBar) mBottomMenu.findViewById(R.id.jump_chapter_seekbar);

        mChapterSeekGroup = ViewCombineGroup.createGroup(mSwitchChapterTitle, preChapterBtn, nextChapterBtn, chapterSeekBar);
        mMenuGroup.addChild(mChapterSeekGroup);
        mChapterSeekGroup.hide();

        preChapterBtn.setOnClickListener(v -> {
            jumpToChapter(mCurrentChapterSeq - 1);
        });
        nextChapterBtn.setOnClickListener(v -> {
            jumpToChapter(mCurrentChapterSeq + 1);
        });

        chapterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSwitchChapterTitle.setText(mBookInfo.titles.get(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                jumpToChapter(seekBar.getProgress() + 1);
            }
        });
        chapterSeekBar.setMax(mBookInfo.chapterFiles.size() - 1);
    }

    private void initColorSelector() {
        mColorSelectListView = (RecyclerView) findViewById(R.id.background_selector);
        View titleView = findViewById(R.id.background_title);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mColorSelectListView.setLayoutManager(layoutManager);
        mColorSetGroup = ViewCombineGroup.createGroup(mColorSelectListView, titleView);
        mMenuGroup.addChild(mColorSetGroup);
        mColorSetGroup.hide();

        //init
        Integer[] colorList = new Integer[]{ColorConfig.BackgroundColor.DIM_GREEN
                , ColorConfig.BackgroundColor.DIM_GREY
                , ColorConfig.BackgroundColor.SHEEP_SKIN
                , ColorConfig.BackgroundColor.STYLE_NIGHT};
        ColorStyleAdapter colorStyleAdapter = new ColorStyleAdapter(mDataPresenter.getFontConfig().backgroundColor, this);
        colorStyleAdapter.setColors(Arrays.asList(colorList));
        mColorSelectListView.setAdapter(colorStyleAdapter);
    }

    private void initFontSizeSelect() {
        mSmallerFontBtn = findViewById(R.id.smaller_font);
        mLargerFontBtn = findViewById(R.id.larger_font);
        mFontSizeSeekBar = (SeekBar) findViewById(R.id.font_size_seekbar);
        mFontSetGroup = ViewCombineGroup.createGroup(mSmallerFontBtn, mLargerFontBtn, mFontSizeSeekBar);
        mMenuGroup.addChild(mFontSetGroup);
        mFontSetGroup.hide();

        mFontSizeSeekBar.setMax(FontConfig.MAX_FONT_SIZE_SP - FontConfig.MIN_FINT_SIZE_SP);
        mSmallerFontBtn.setOnClickListener(v -> {
            int newSize = mDataPresenter.getFontConfig().incrFontSize();
            onChangeFontSize();
            mFontSizeSeekBar.setProgress(newSize + FontConfig.MIN_FINT_SIZE_SP);
        });

        mLargerFontBtn.setOnClickListener(v -> {
            int newSize = mDataPresenter.getFontConfig().descrFontSize();
            onChangeFontSize();
            mFontSizeSeekBar.setProgress(newSize + FontConfig.MIN_FINT_SIZE_SP);
        });

        mFontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mDataPresenter.getFontConfig().setFontSizeSp(progress + FontConfig.MIN_FINT_SIZE_SP);
                onChangeFontSize();
            }
        });
    }


    @Override
    public void onGetChapterInfo(ChapterModel chapterInfo) {
        if (chapterInfo == null) {
            return;
        }
        int chapterSeq = chapterInfo.chapterSeq;
        if (mCurrentChapterSeq == chapterInfo.chapterSeq) {
            chapterInfo.startReadPosition = mReadPositionOfChapter;
        }

        mDataPresenter.getPages(chapterInfo, rightPages -> {
            if (chapterSeq == mCurrentChapterSeq) {
                mChapterTitle.setText(chapterInfo.title);
                mPageAdapter.setData(chapterSeq, rightPages);
                mPageAdapter.notifyDataSetChanged();

                int scrollTo = -1;
                int size = rightPages.size();
                int startReadPosition = chapterInfo.startReadPosition;
                for (int index = 1; index < size; index++) {
                    NBPage ipage = rightPages.get(index);
                    if (startReadPosition >= ipage.getStartPosition() && startReadPosition <= ipage.getEndPosition()) {
                        scrollTo = index;
                        break;
                    }
                }
                if (scrollTo >= 0 && scrollTo <= size) {
                    mPageRecyclerView.scrollToPosition(scrollTo);
                }

                //预加载前后一章
                preLoadChapter(mCurrentChapterSeq + 1);
                preLoadChapter(mCurrentChapterSeq - 1);
            } else if (chapterSeq == mCurrentChapterSeq + 1) {
                int count = mPageAdapter.getItemCount();
                mPageAdapter.appendData(chapterSeq, rightPages);
                mPageAdapter.notifyItemRangeInserted(count, rightPages.size());

                mPageAdapter.removeChapterByRangeOut(mCurrentChapterSeq - 3, mCurrentChapterSeq + 3);
            } else if (chapterSeq == mCurrentChapterSeq - 1) {
                mPageAdapter.prependData(chapterSeq, rightPages);
                mPageAdapter.notifyItemRangeInserted(0, rightPages.size());
                mPageAdapter.removeChapterByRangeOut(mCurrentChapterSeq - 3, mCurrentChapterSeq + 3);
            }
            chapterInfo.pageList = rightPages;
        });

    }

    //点击跳转章节
    private void jumpToChapter(int chapterSeq) {
        if (chapterSeq <= 0 || chapterSeq > mBookInfo.chapterFiles.size()) {
            return;
        }
        //参数更新
        mCurrentChapterSeq = chapterSeq;
        mReadPositionOfChapter = 0;
        updateChapterTitle(chapterSeq);

        if (mPageAdapter.hasLoaded(chapterSeq)) {
            //如果请求的是已加载，则需要跳到该章节的第一页
            int position = mPageAdapter.getPositionByChapterSeq(chapterSeq);
            if (position >= 0) {
                mPageRecyclerView.scrollToPosition(position);
            }
            //前后章不完备，预加载一下
            preLoadChapter(mCurrentChapterSeq + 1);
            preLoadChapter(mCurrentChapterSeq - 1);
        } else {
            mDataPresenter.getChapterModel(chapterSeq);
        }
    }

    //预加载章节
    private void preLoadChapter(int chapterSeq) {
        int size = mBookInfo.chapterFiles.size();
        if (chapterSeq <= 0 || chapterSeq > size) {
            return;
        }
        if (!mPageAdapter.hasLoaded(chapterSeq)) {
            mDataPresenter.getChapterModel(chapterSeq);
        } else {
            Logs.i(TAG, "has load chapter " + chapterSeq);
        }
    }


    //swipe切换章节
    private void onCurrentChapterChange(int chapterSeq) {
        if (chapterSeq <= 0 || chapterSeq > mBookInfo.chapterFiles.size()) {
            return;
        }
        if (chapterSeq != mCurrentChapterSeq) {
            mCurrentChapterSeq = chapterSeq;
            updateChapterTitle(chapterSeq);
            preLoadChapter(mCurrentChapterSeq + 1);
            preLoadChapter(mCurrentChapterSeq - 1);
        }
        //更新当前阅读位置
        int position = mPageManager.findFirstCompletelyVisibleItemPosition();
        NBPage currentPage = mPageAdapter.getPage(position);
        mReadPositionOfChapter = currentPage != null ? currentPage.getStartPosition() : 0;
    }

    private void updateChapterTitle(int chapterSeq) {
        mChapterTitle.setText(mBookInfo.titles.get(chapterSeq - 1));
        mSwitchChapterTitle.setText(mBookInfo.titles.get(chapterSeq - 1));
    }


    @Override
    public void clickArea(int area) {
        int position = mPageManager.findFirstCompletelyVisibleItemPosition();
        switch (area) {
            case PageArea.HORIZONTAL_LEFT:
                if (position - 1 >= 0) {
                    mPageRecyclerView.smoothScrollToPosition(position - 1);
                }
                break;
            case PageArea.HORIZONTAL_RIGHT:
                if (position + 1 < mPageAdapter.getItemCount()) {
                    mPageRecyclerView.smoothScrollToPosition(position + 1);
                }
                break;
            case PageArea.HORIZONTAL_MIDDLE:
                mMenuGroup.toggleShow();
                break;
            default:
                break;
        }
    }

    @Override
    public void onColorStyleChange(int newColor) {
        mDataPresenter.getFontConfig().backgroundColor = newColor;
        int position = mPageManager.findFirstCompletelyVisibleItemPosition();
        int start = position - 2 >= 0 ? position - 2 : 0;
        int count = start + 4 > mBookInfo.titles.size() ? mBookInfo.titles.size() - start : 4;
        mPageAdapter.notifyItemRangeChanged(start, count);
    }

    private void onChangeFontSize() {
        //redraw the page
        mPageAdapter.clear();
        mDataPresenter.getChapterModel(mCurrentChapterSeq);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataPresenter.onDestroy();
        mMenuGroup.recycle();
        mChapterSeekGroup.recycle();
    }


}
