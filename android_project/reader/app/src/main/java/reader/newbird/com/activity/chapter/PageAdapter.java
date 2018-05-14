package reader.newbird.com.activity.chapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

    public PageAdapter(ChapterPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_page, parent,false);
        ViewHolder holder = new ViewHolder(view);
        holder.pageView = (ImageView) view.findViewById(R.id.page_view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NBPage page = mData.get(position);
        Bitmap bitmap = NBParserCore.initDefault(holder.pageView.getContext()).getBitmap(page);
        holder.pageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return mData.size();
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
