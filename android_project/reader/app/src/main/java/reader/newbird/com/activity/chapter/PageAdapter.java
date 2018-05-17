package reader.newbird.com.activity.chapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.newbird.parse.core.NBParserCore;
import com.newbird.parse.model.NBPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import reader.newbird.com.R;
import reader.newbird.com.chapter.ChapterPresenter;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.ViewHolder> {
    private List<NBPage> mData = new ArrayList<>();
    private PageClickListener mClickListener;
    private ChapterPresenter mPresenter;
    private HashMap<Integer, List<NBPage>> mLoadedChapter = new HashMap<>();

    public PageAdapter(ChapterPresenter presenter) {
        this.mPresenter = presenter;
    }

    public void setClickListener(PageClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_page, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.pageView = (ImageView) view.findViewById(R.id.page_view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NBPage page = mData.get(position);
        Bitmap bitmap = NBParserCore.initDefault(holder.pageView.getContext()).getBitmap(page);
        holder.pageView.setImageBitmap(bitmap);
        holder.pageView.setOnTouchListener(new View.OnTouchListener() {
            float x, y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getRawX();
                        y = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (x >= 0 && y >= 0 && Math.abs(event.getRawX() - x) < 10 && Math.abs(event.getRawY() - y) < 10) {
                            int width = v.getWidth();
                            int leftLine = width / 3;
                            int rightLine = leftLine * 2;
                            if (mClickListener != null) {
                                if (x < leftLine) {
                                    mClickListener.clickArea(PageArea.HORIZONTAL_LEFT);
                                } else if (x > rightLine) {
                                    mClickListener.clickArea(PageArea.HORIZONTAL_RIGHT);
                                } else {
                                    mClickListener.clickArea(PageArea.HORIZONTAL_MIDDLE);
                                }
                            }
                        }
                        x = y = -1;
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<NBPage> getLoadedPages(int chapterSeq) {
        return mLoadedChapter.get(chapterSeq);
    }

    public int getItemPosition(NBPage page) {
        return mData.indexOf(page);
    }

    public NBPage getPage(int position) {
        if (position >= 0 && position < getItemCount()) {
            return mData.get(position);
        } else {
            return null;
        }
    }

    public void setData(int chapterSeq, List<NBPage> data) {
        mLoadedChapter.clear();
        mData.clear();

        mData.addAll(data);
        mLoadedChapter.put(chapterSeq, data);
    }

    public void appendData(int chapterSeq, List<NBPage> data) {
        mData.addAll(data);
        mLoadedChapter.put(chapterSeq, data);
    }

    public void prependData(int chapterSeq, List<NBPage> data) {
        mData.addAll(0, data);
        mLoadedChapter.put(chapterSeq, data);
    }

    public void clear() {
        mData.clear();
        mLoadedChapter.clear();
    }

    //移除某个范围之外的章节，包含参数代表的章节
    public void removeChapterByRangeOut(int smallestSeq, int biggestSeq) {
        if (smallestSeq > biggestSeq) {
            return;
        }
        Set<Integer> seqs = mLoadedChapter.keySet();
        for (int chapterSeq : seqs) {
            if (chapterSeq >= biggestSeq || chapterSeq <= smallestSeq) {
                List<NBPage> pages = mLoadedChapter.get(chapterSeq);
                for (NBPage page : pages) {
                    int position = mData.indexOf(page);
                    if (position >= 0) {
                        mData.remove(page);
                        notifyItemRemoved(position);
                    }
                }
            }
        }
    }

    public int findChapterSeqByPosition(int position) {
        if (position < 0 || position >= mData.size()) {
            return -1;
        }
        NBPage currentPage = mData.get(position);
        Set<Integer> seqs = mLoadedChapter.keySet();
        for (int chapterSeq : seqs) {
            List<NBPage> pages = mLoadedChapter.get(chapterSeq);
            for (NBPage page : pages) {
                if (page.equals(currentPage)) {
                    return chapterSeq;
                }
            }
        }
        return -1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pageView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
