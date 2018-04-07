package reader.newbird.com.activity.chapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import reader.newbird.com.R;
import reader.newbird.com.book.BookModel;
import reader.newbird.com.config.IntentConstant;
import reader.newbird.com.utils.Logs;

public class ChapterPageActivity extends AppCompatActivity {

    private RecyclerView mPageRecyclerView;
    private View mHeadMenu;
    private View mBackPress;
    private TextView mChapterTitle;
    private View mSettingIcon;

    private int mCurrentChapterSeq;
    private BookModel mBookInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chapter_page);
        initView();
        initData();
    }

    private void initView() {
        mPageRecyclerView = (RecyclerView) findViewById(R.id.page_list);
        mHeadMenu = findViewById(R.id.menu_header);
        mBackPress = findViewById(R.id.back_press);
        mChapterTitle = (TextView) findViewById(R.id.chapter_title);
        mSettingIcon = findViewById(R.id.setting_icon);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mPageRecyclerView);
    }

    private void initData() {
        Intent intent = getIntent();
        mBookInfo = intent.getParcelableExtra(IntentConstant.PARCEL_BOOK_MODEL);
        if (mBookInfo == null) {
            Logs.e("ChapterPageActivity", "empty book");
            finish();
            return;
        }
        mCurrentChapterSeq = intent.getIntExtra(IntentConstant.PARAM_CHAPTER_ID,0);
    }




}
