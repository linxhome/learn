package reader.newbird.com.activity.recommand;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import reader.newbird.com.R;
import reader.newbird.com.base.ThreadManager;
import reader.newbird.com.book.BookModel;
import reader.newbird.com.book.BookPresenter;
import reader.newbird.com.book.IGetBook;

/**
 * A fragment representing a list of Items.
 * interface.
 */
public class RecommendFragment extends Fragment implements IGetBook {
    @BindView(R.id.recommend_recycler)
    RecyclerView recycler;
    @BindView(R.id.recommend_swipe)
    SwipeRefreshLayout swipeLayout;

    private RecommendAdapter mAdapter;
    BookPresenter mDataPresenter;

    public RecommendFragment() {

    }

    public static RecommendFragment newInstance() {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        ButterKnife.bind(this,view);
        initView();
        initData();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(recycler == null) {

        }
    }

    private void initData() {
        mDataPresenter = new BookPresenter(this);
        mDataPresenter.getRecommendBooks();
    }


    private void initView() {
        mAdapter = new RecommendAdapter();
        recycler.setAdapter(mAdapter);
        swipeLayout.setOnRefreshListener(() -> mDataPresenter.getRecommendBooks());
    }

    @Override
    public void updateBook(BookModel data) {

    }

    @Override
    public void updateBooks(List<BookModel> data) {
        mAdapter.setBooks(data);
    }
}
