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
import java.util.List;

import reader.newbird.com.R;
import reader.newbird.com.chapter.ChapterPresenter;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.ViewHolder> {
    private List<NBPage> mData = new ArrayList<>();
    private ChapterPresenter mPresenter;
    private PageClickListener mClickListener;

    public PageAdapter(ChapterPresenter mPresenter) {
        this.mPresenter = mPresenter;
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
                            if(mClickListener != null) {
                                if(x < leftLine) {
                                    mClickListener.clickArea(1);
                                } else if(x> rightLine) {
                                    mClickListener.clickArea(3);
                                } else {
                                    mClickListener.clickArea(2);
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

    public void setData(List<NBPage> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void appendData(List<NBPage> data) {
        mData.addAll(data);
    }

    public void prependData(List<NBPage> data) {
        mData.addAll(0, data);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pageView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
