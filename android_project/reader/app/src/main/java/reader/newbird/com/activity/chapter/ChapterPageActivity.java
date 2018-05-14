package reader.newbird.com.activity.chapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import reader.newbird.com.R;
import reader.newbird.com.book.BookModel;
import reader.newbird.com.chapter.ChapterModel;
import reader.newbird.com.chapter.ChapterPresenter;
import reader.newbird.com.chapter.IGetChapter;
import reader.newbird.com.config.IntentConstant;
import reader.newbird.com.utils.Logs;

public class ChapterPageActivity extends AppCompatActivity implements IGetChapter {

    private RecyclerView mPageRecyclerView;
    private View mHeadMenu;
    private View mBottomMenu;
    private View mBackPress;
    private TextView mChapterTitle;
    private View mSettingIcon;
    private NavigationView mDrawerNavi;

    private int mCurrentChapterSeq;
    private BookModel mBookInfo;
    private ChapterPresenter mDataPresenter;

    private PageAdapter mPageAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chapter_page);
        initData();
        initView();
    }

    private void initView() {
        mPageRecyclerView = (RecyclerView) findViewById(R.id.page_list);
        mHeadMenu = findViewById(R.id.menu_header);
        mBottomMenu = findViewById(R.id.menu_bottom);
        mBackPress = findViewById(R.id.back_press);
        mChapterTitle = (TextView) findViewById(R.id.chapter_title);
        mSettingIcon = findViewById(R.id.setting_icon);
        mDrawerNavi = (NavigationView) findViewById(R.id.content_navi);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mPageRecyclerView);
        mPageAdapter = new PageAdapter(mDataPresenter);
        mPageRecyclerView.setAdapter(mPageAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPageRecyclerView.setLayoutManager(manager);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mHeadMenu.setVisibility(View.GONE);
            mBottomMenu.setVisibility(View.GONE);
        }, 3000);
    }

    private void initData() {
        Intent intent = getIntent();
        mBookInfo = intent.getParcelableExtra(IntentConstant.PARCEL_BOOK_MODEL);
        if (mBookInfo == null) {
            Logs.e("ChapterPageActivity", "empty book");
            finish();
            return;
        }
        mCurrentChapterSeq = intent.getIntExtra(IntentConstant.PARAM_CHAPTER_ID, 0);
        mDataPresenter = new ChapterPresenter(this, mBookInfo);
        mDataPresenter.setViewCallback(this);
        mDataPresenter.getChapterModel(mCurrentChapterSeq);
        initCategory();
    }

    //初始化目录
    private void initCategory() {
        if (mBookInfo == null) {
            return;
        }
        /*ImageView cover = (ImageView) mDrawerNavi.getHeaderView(0).findViewById(R.id.item_cover);
        Glide.with(ReaderContext.getInstance().get()).load(mBookInfo.cover).into(cover);
        TextView authorText = (TextView) mDrawerNavi.getHeaderView(0).findViewById(R.id.item_author_name);
        authorText.setText(mBookInfo.authorName);
        TextView bookText = (TextView) mDrawerNavi.getHeaderView(0).findViewById(R.id.item_book_name);
        bookText.setText(mBookInfo.bookName);*/

        if (mBookInfo.titles == null || mBookInfo.chapterPaths == null) {
            return;
        }
        int chapterSize = mBookInfo.chapterPaths.size();
        int titleSize = mBookInfo.titles.size();
        if (chapterSize > titleSize) {
            mBookInfo.chapterPaths = mBookInfo.chapterPaths.subList(0, titleSize);
        } else if (chapterSize < titleSize) {
            mBookInfo.titles = mBookInfo.titles.subList(0, chapterSize);
        }

        mDrawerNavi.getMenu().clear();
        for (String title : mBookInfo.titles) {
            mDrawerNavi.getMenu().add(title);
        }
        mDrawerNavi.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int order = item.getOrder();
                String chapterSeq = mBookInfo.chapterPaths.get(order);
                //todo jump to the order size
                return false;
            }
        });
    }

    @Override
    public void onGetChapterInfo(ChapterModel chapterInfo) {
        if (chapterInfo != null) {
            mDataPresenter.getPages(chapterInfo, pages -> {
                updateAttachViewInfo(chapterInfo);
                int count = mPageAdapter.getItemCount();
                mPageAdapter.appendData(pages);
                mPageAdapter.notifyItemInserted(count);
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataPresenter.onDestroy();
    }

    private void updateAttachViewInfo(ChapterModel info) {
        //todo update the title
    }
}
