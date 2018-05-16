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
import com.newbird.parse.model.NBPage;

import java.util.List;

import reader.newbird.com.R;
import reader.newbird.com.book.BookModel;
import reader.newbird.com.chapter.ChapterModel;
import reader.newbird.com.chapter.ChapterPresenter;
import reader.newbird.com.chapter.IGetChapter;
import reader.newbird.com.config.IntentConstant;
import reader.newbird.com.utils.Logs;

public class ChapterPageActivity extends AppCompatActivity implements IGetChapter, PageClickListener {

    private RecyclerView mPageRecyclerView;
    private View mHeadMenu;
    private View mBottomMenu;
    private View mBackPress;
    private TextView mChapterTitle;
    private NavigationView mDrawerNavi;
    private DrawerLayout mDrawerLayout;

    private View mBottomCategoryBtn;
    private View mBottomProgressBtn;
    private View mBottomSettingBtn;
    private View mBottomFontBtn;

    //进度设置页
    private TextView mSwitchChapterTitle;
    private View mPreChapterBtn;
    private View mNextChapterBtn;
    private SeekBar mChapterSeekbar;

    private int mCurrentChapterSeq;
    private BookModel mBookInfo;
    private ChapterPresenter mDataPresenter;
    private PageAdapter mPageAdapter;
    private LinearLayoutManager mPageManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chapter_page);
        initView();
        initData();
        initCategory();
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mPageRecyclerView = (RecyclerView) findViewById(R.id.page_list);
        mHeadMenu = findViewById(R.id.menu_header);
        mBottomMenu = findViewById(R.id.menu_bottom);
        mBackPress = findViewById(R.id.back_press);
        mChapterTitle = (TextView) findViewById(R.id.chapter_title);
        mDrawerNavi = (NavigationView) findViewById(R.id.content_navi);
        mChapterTitle = (TextView) mHeadMenu.findViewById(R.id.chapter_title);
        mBackPress = mHeadMenu.findViewById(R.id.back_press);

        mBottomCategoryBtn = findViewById(R.id.category_bottom_btn);
        mBottomFontBtn = findViewById(R.id.font_bottom_btn);
        mBottomProgressBtn = findViewById(R.id.progress_bottom_btn);
        mBottomSettingBtn = findViewById(R.id.setting_bottom_btn);

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
                }
            }
        });


        mBackPress.setOnClickListener(v -> finish());
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                setMenuVisibility(View.GONE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        initSeekBarView();

        mBottomCategoryBtn.setOnClickListener(v -> {
            //setMenuVisibility(View.GONE);
            setSeekbarVisibility(View.GONE);
            mDrawerLayout.openDrawer(mDrawerNavi);
        });

        mBottomProgressBtn.setOnClickListener(v -> toggleSeekBarVisibility());

        Handler handler = new Handler();
        handler.postDelayed(() -> setMenuVisibility(View.GONE), 3000);

    }

    private void initData() {
        Intent intent = getIntent();
        mBookInfo = intent.getParcelableExtra(IntentConstant.PARCEL_BOOK_MODEL);
        if (mBookInfo == null) {
            Logs.e("ChapterPageActivity", "empty book");
            finish();
            return;
        }
        int chapterSeq = intent.getIntExtra(IntentConstant.PARAM_CHAPTER_ID, 0);
        mDataPresenter = new ChapterPresenter(this, mBookInfo);
        mDataPresenter.setViewCallback(this);
        jumpToChapter(chapterSeq);
    }

    //初始化目录
    private void initCategory() {
        if (mBookInfo == null) {
            return;
        }
        ImageView cover = (ImageView) mDrawerNavi.getHeaderView(0).findViewById(R.id.item_cover);
        Glide.with(this).load(mBookInfo.cover).into(cover);
        TextView authorText = (TextView) mDrawerNavi.getHeaderView(0).findViewById(R.id.item_author_name);
        authorText.setText(mBookInfo.authorName);
        TextView bookText = (TextView) mDrawerNavi.getHeaderView(0).findViewById(R.id.item_book_name);
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

        NavigationMenuView navigationMenuView = (NavigationMenuView) mDrawerNavi.getChildAt(0);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }

        mDrawerNavi.getMenu().clear();
        for (String title : mBookInfo.titles) {
            mDrawerNavi.getMenu().add(title);
        }
        mDrawerNavi.setNavigationItemSelectedListener(item -> {
            String itemTitle = item.getTitle().toString();
            int chapterSeq = mBookInfo.titles.indexOf(itemTitle) + 1;
            jumpToChapter(chapterSeq);

            mDrawerLayout.closeDrawer(mDrawerNavi);
            return false;
        });
    }

    //初始化进度设置view
    private void initSeekBarView() {
        mSwitchChapterTitle = (TextView) mBottomMenu.findViewById(R.id.switch_chapter_title);
        mPreChapterBtn = mBottomMenu.findViewById(R.id.pre_chapter_btn);
        mNextChapterBtn = mBottomMenu.findViewById(R.id.next_chapter_btn);
        mChapterSeekbar = (SeekBar) mBottomMenu.findViewById(R.id.jump_chapter_seekbar);
        setSeekbarVisibility(View.GONE);

        mPreChapterBtn.setOnClickListener(v -> {
            jumpToChapter(mCurrentChapterSeq - 1);
            mSwitchChapterTitle.setText(mBookInfo.titles.get(mCurrentChapterSeq - 1));
        });
        mNextChapterBtn.setOnClickListener(v -> {
            jumpToChapter(mCurrentChapterSeq + 1);
            mSwitchChapterTitle.setText(mBookInfo.titles.get(mCurrentChapterSeq + 1));
        });
        mChapterSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    }

    private void toggleSeekBarVisibility() {
        if (mChapterSeekbar.getVisibility() == View.GONE) {
            setSeekbarVisibility(View.VISIBLE);
            mChapterSeekbar.setMax(mBookInfo.chapterFiles.size() - 1);
        } else {
            setSeekbarVisibility(View.GONE);
        }
    }

    private void setSeekbarVisibility(int visibility) {
        mSwitchChapterTitle.setVisibility(visibility);
        mPreChapterBtn.setVisibility(visibility);
        mNextChapterBtn.setVisibility(visibility);
        mChapterSeekbar.setVisibility(visibility);
    }

    @Override
    public void onGetChapterInfo(ChapterModel chapterInfo) {
        if (chapterInfo != null) {
            int chapterSeq = chapterInfo.chapterSeq;

            List<NBPage> loadedPages = mPageAdapter.getLoadedPages(chapterInfo.chapterSeq);
            if (loadedPages != null) {
                if (chapterSeq == mCurrentChapterSeq) {
                    if (loadedPages.size() > 0) {
                        int position = mPageAdapter.getItemPosition(loadedPages.get(0));
                        if (position >= 0) {
                            mPageRecyclerView.scrollToPosition(position);
                        }
                    }
                    preNextChapter();
                    preLastChapter();
                }
            } else {
                mDataPresenter.getPages(chapterInfo, pages -> {
                    if (chapterSeq == mCurrentChapterSeq) {
                        mChapterTitle.setText(chapterInfo.title);
                        mPageAdapter.setData(chapterSeq, pages);
                        mPageAdapter.notifyDataSetChanged();

                        //预加载前后一章
                        preLastChapter();
                        preNextChapter();
                    } else if (chapterSeq == mCurrentChapterSeq + 1) {
                        int count = mPageAdapter.getItemCount();
                        mPageAdapter.appendData(chapterSeq, pages);
                        mPageAdapter.notifyItemRangeInserted(count, pages.size());

                        mPageAdapter.removeChapterByRangeOut(mCurrentChapterSeq - 3, mCurrentChapterSeq + 3);
                    } else if (chapterSeq == mCurrentChapterSeq - 1) {
                        mPageAdapter.prependData(chapterSeq, pages);
                        mPageAdapter.notifyItemRangeInserted(0, pages.size());

                        mPageAdapter.removeChapterByRangeOut(mCurrentChapterSeq - 3, mCurrentChapterSeq + 3);
                    }
                    chapterInfo.pageList = pages;
                });
            }
        }
    }

    private void jumpToChapter(int chapterSeq) {
        if (chapterSeq <= 0 || chapterSeq > mBookInfo.chapterFiles.size()) {
            return;
        }
        mCurrentChapterSeq = chapterSeq;
        mDataPresenter.getChapterModel(chapterSeq);
        mChapterTitle.setText(mBookInfo.titles.get(chapterSeq - 1));
    }

    //章节切换
    private void onCurrentChapterChange(int chapterSeq) {
        if (chapterSeq <= 0 || chapterSeq > mBookInfo.chapterFiles.size()) {
            return;
        }
        mCurrentChapterSeq = chapterSeq;
        mChapterTitle.setText(mBookInfo.titles.get(chapterSeq - 1));
        preNextChapter();
        preLastChapter();
    }

    //预先拉取上一章节
    private void preLastChapter() {
        int chapterSeq = mCurrentChapterSeq - 1;
        int size = mBookInfo.chapterFiles.size();
        if (chapterSeq <= 0 || chapterSeq > size) {
            return;
        }
        mDataPresenter.getChapterModel(chapterSeq);
    }

    //预先拉取下一章
    private void preNextChapter() {
        int chapterSeq = mCurrentChapterSeq + 1;
        int size = mBookInfo.chapterFiles.size();
        if (chapterSeq <= 0 || chapterSeq > size) {
            return;
        }
        mDataPresenter.getChapterModel(chapterSeq);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataPresenter.onDestroy();
    }

    private void toggleMenuVisible() {
        if (mHeadMenu.getVisibility() == View.VISIBLE) {
            setMenuVisibility(View.GONE);
        } else {
            setMenuVisibility(View.VISIBLE);
        }
    }

    private void setMenuVisibility(int visibility) {
        mHeadMenu.setVisibility(visibility);
        mBottomMenu.setVisibility(visibility);
    }


    @Override
    public void clickArea(int area) {
        int position = mPageManager.findFirstCompletelyVisibleItemPosition();
        switch (area) {
            case PageArea.HORIZONTAL_LEFT:
                mPageRecyclerView.scrollToPosition(position - 1);
                break;
            case PageArea.HORIZONTAL_RIGHT:
                mPageRecyclerView.scrollToPosition(position + 1);
                break;
            case PageArea.HORIZONTAL_MIDDLE:
                toggleMenuVisible();
                break;
            default:
                break;
        }
    }
}
