package reader.newbird.com.activity.bookshelf;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import reader.newbird.com.R;
import reader.newbird.com.activity.chapter.PageActivity;
import reader.newbird.com.base.ReaderContext;
import reader.newbird.com.book.BookModel;
import reader.newbird.com.config.IntentConstant;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BookModel} and makes a call to the
 */
public class ShelfListAdapter extends RecyclerView.Adapter<ShelfListAdapter.ViewHolder> {

    private final List<BookModel> mValues = new ArrayList<>();
    private Activity mActivity;

    public ShelfListAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fragment_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.authorView.setText(holder.mItem.authorName);
        holder.bookNameView.setText(holder.mItem.bookName);
        Glide.with(ReaderContext.getInstance().get()).load(holder.mItem.cover).into(holder.coverView);

        holder.mView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(mActivity.getBaseContext(), PageActivity.class);
            intent.putExtra(IntentConstant.PARCEL_BOOK_MODEL,holder.mItem);
            intent.putExtra(IntentConstant.PARAM_CHAPTER_ID,1);
            mActivity.startActivity(intent);

        });
        holder.mView.setOnLongClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(IntentConstant.ACTION_DETAIL_PAGE);
            intent.putExtra(IntentConstant.PARCEL_BOOK_MODEL, holder.mItem);
            mActivity.startActivity(intent);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void addItem(BookModel oneBook) {
        boolean hasSame = false;
        for (BookModel item : mValues) {
            if (item.bookDir != null && item.bookDir.equals(oneBook.bookDir)) {
                hasSame = true;
            }
        }
        if (!hasSame) {
            mValues.add(oneBook);
        }
    }

    public void notifyAppend() {
        notifyItemInserted(getItemCount());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView coverView;
        final TextView bookNameView;
        final TextView authorView;
        BookModel mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            coverView = (ImageView) view.findViewById(R.id.item_cover);
            bookNameView = (TextView) view.findViewById(R.id.item_book_name);
            authorView = (TextView) view.findViewById(R.id.item_author_name);
        }
    }
}
