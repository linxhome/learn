package reader.newbird.com.activity.bookshelf;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import reader.newbird.com.R;
import reader.newbird.com.book.BookModel;
import reader.newbird.com.book.BookPresenter;
import reader.newbird.com.book.IGetBook;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class ShelfFragment extends Fragment implements IGetBook {

    private int mColumnCount = 1;
    private BookPresenter mBookPresenter;
    private ShelfListAdapter mDataAdapter;

    public static ShelfFragment newInstance() {
        ShelfFragment fragment = new ShelfFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mBookPresenter = new BookPresenter(fragment);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mColumnCount = 3;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mDataAdapter = new ShelfListAdapter(getActivity());
            recyclerView.setAdapter(mDataAdapter);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.common_action_bar, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBookPresenter.getBook();
    }

    @Override
    public void updateBook(BookModel data) {
        mDataAdapter.addItem(data);
        mDataAdapter.notifyAppend();
    }

    @Override
    public void updateBooks(List<BookModel> data) {
        if (data != null) {
            for (BookModel book : data) {
                mDataAdapter.addItem(book);
                mDataAdapter.notifyAppend();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mBookPresenter != null) {
            mBookPresenter.destroy();
        }
    }
}
