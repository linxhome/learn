package reader.newbird.com.activity.bookdetail;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import reader.newbird.com.R;
import reader.newbird.com.base.ReaderContext;
import reader.newbird.com.book.BookFileManager;
import reader.newbird.com.book.BookModel;
import reader.newbird.com.config.IntentConstant;

public class BookDetailActivity extends AppCompatActivity {
    private View mConverContainer;
    private ImageView mCoverImage;
    private TextView mAuthorView;
    private TextView mBookNameView;
    private TextView mDetailView;
    private SimpleRecyclerView mAdapter;

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
        mAdapter = new SimpleRecyclerView(this);
        recyclerView.setAdapter(mAdapter);

        initData();
    }

    public void initData() {
        BookModel bookData = getIntent().getParcelableExtra(IntentConstant.PARCEL_BOOK_MODEL);
        if (bookData == null || !bookData.isValid()) {
            finish();
            return;
        }
        Glide.with(ReaderContext.getInstance().get()).load(bookData.cover).into(mCoverImage);
        mAuthorView.setText(bookData.authorName);
        mBookNameView.setText(bookData.bookName);
        mDetailView.setText(bookData.detail);
        mAdapter.setData(bookData.chapterSeqs);
    }

    static class SimpleRecyclerView extends RecyclerView.Adapter<ViewHolder> {
        private Activity mActivity;
        private List<String> mDatas;

        public SimpleRecyclerView(Activity mActivity) {
            this.mActivity = mActivity;
        }

        public void setData(List<String> data) {
            this.mDatas = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(mActivity);
            textView.setTextSize(16);
            textView.setBackgroundResource(R.drawable.simple_item_background);
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String filename = mDatas.get(position);
            holder.txtView.setText(filename.replace(BookFileManager.CHAPTER_FILE_SUFFIX, ""));
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
