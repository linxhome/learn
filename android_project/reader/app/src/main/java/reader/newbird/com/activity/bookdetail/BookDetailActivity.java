package reader.newbird.com.activity.bookdetail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import reader.newbird.com.R;
import reader.newbird.com.activity.chapter.PageActivity;
import reader.newbird.com.base.ReaderContext;
import reader.newbird.com.book.BookManager;
import reader.newbird.com.book.BookModel;
import reader.newbird.com.config.IntentConstant;

public class BookDetailActivity extends AppCompatActivity {
    private View mConverContainer;
    private ImageView mCoverImage;
    private TextView mAuthorView;
    private TextView mBookNameView;
    private TextView mDetailView;
    private SimpleRecyclerViewAdapter mAdapter;

    private static final int SPAN_COUNT = 3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        mConverContainer = findViewById(R.id.cover_container);
        mCoverImage = (ImageView) findViewById(R.id.cover_image);
        mAuthorView = (TextView) findViewById(R.id.author_name);
        mBookNameView = (TextView) findViewById(R.id.book_name);
        mDetailView = (TextView) findViewById(R.id.book_intro);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chapter_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mAdapter = new SimpleRecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new MarginItemDecro(10));

        initData();
    }

    private BookModel mBookData;

    public void initData() {
        mBookData = getIntent().getParcelableExtra(IntentConstant.PARCEL_BOOK_MODEL);
        if (mBookData == null || !mBookData.isValid()) {
            finish();
            return;
        }
        Glide.with(ReaderContext.getInstance().get()).load(mBookData.cover).into(mCoverImage);
        mAuthorView.setText(mBookData.authorName);
        mBookNameView.setText(mBookData.bookName);
        mDetailView.setText(mBookData.detail);
        mAdapter.setData(mBookData.chapterFiles);
        mAdapter.setBookModel(mBookData);
    }

    static class MarginItemDecro extends  RecyclerView.ItemDecoration {
        int margin;

        public MarginItemDecro(int margin) {
            this.margin = margin;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(margin,0,margin,margin);
        }
    }

    static class SimpleRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Activity mActivity;
        private BookModel mBookModel;
        private List<String> mDatas;

        public SimpleRecyclerViewAdapter(Activity mActivity) {
            this.mActivity = mActivity;
        }

        public void setData(List<String> data) {
            this.mDatas = data;
        }

        public void setBookModel(BookModel mBookModel) {
            this.mBookModel = mBookModel;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(mActivity);
            textView.setTextSize(16);
            textView.setBackgroundResource(R.drawable.simple_item_background);
            textView.setGravity(Gravity.CENTER);
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String filename = mDatas.get(position);
            holder.txtView.setText(filename.replace(BookManager.CHAPTER_FILE_SUFFIX, ""));
            holder.txtView.setOnClickListener(v -> {
                Intent intent = new Intent();
                int chapterSeq = Integer.valueOf(filename.replace(BookManager.CHAPTER_FILE_SUFFIX
                        ,""));
                intent.setClass(mActivity.getBaseContext(), PageActivity.class);
                intent.putExtra(IntentConstant.PARCEL_BOOK_MODEL,mBookModel);
                intent.putExtra(IntentConstant.PARAM_CHAPTER_ID,chapterSeq);
                mActivity.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtView;

        public ViewHolder(TextView itemView) {
            super(itemView);
            this.txtView = itemView;
        }
    }


}
