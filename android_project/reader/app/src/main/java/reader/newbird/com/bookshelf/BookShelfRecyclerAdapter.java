package reader.newbird.com.bookshelf;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import reader.newbird.com.R;
import reader.newbird.com.base.ReaderContext;
import reader.newbird.com.bookshelf.dummy.BookDetail.BookDetailItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BookDetailItem} and makes a call to the
 */
public class BookShelfRecyclerAdapter extends RecyclerView.Adapter<BookShelfRecyclerAdapter.ViewHolder> {

    private final List<BookDetailItem> mValues;

    public BookShelfRecyclerAdapter(List<BookDetailItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.fragment_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.authorView.setText(holder.mItem.authorName);
        holder.bookNameView.setText(holder.mItem.bookName);
        Glide.with(ReaderContext.getInstance().get()).load(holder.mItem.coverUrl).into(holder.coverView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView coverView;
        public final TextView bookNameView;
        public final TextView authorView;
        public BookDetailItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            coverView = (ImageView) view.findViewById(R.id.item_cover);
            bookNameView = (TextView) view.findViewById(R.id.item_book_name);
            authorView = (TextView) view.findViewById(R.id.item_author_name);
        }
    }
}
