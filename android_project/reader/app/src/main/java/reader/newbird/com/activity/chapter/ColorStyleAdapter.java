package reader.newbird.com.activity.chapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.newbird.parse.config.ColorConfig;

import java.util.ArrayList;
import java.util.List;

import reader.newbird.com.R;

/**
 * Created by bird on 2018/5/17.
 * Comment: 背景颜色选择器的adapter
 */
public class ColorStyleAdapter extends RecyclerView.Adapter<ColorStyleAdapter.ViewHolder> {
    private List<Integer> mColors = new ArrayList<>();
    private int mSelectColor = ColorConfig.BackgroundColor.SHEEP_SKIN;
    private ColorStyleListener mColorChangeListener;

    public ColorStyleAdapter(int selectColor, ColorStyleListener colorChangeListener) {
        mSelectColor = selectColor;
        mColorChangeListener = colorChangeListener;
    }

    public void setColors(List<Integer> colors) {
        mColors = colors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_style, parent, false);
        ViewHolder holder = new ViewHolder(rootView);
        holder.imgView = (ImageView) rootView.findViewById(R.id.color_item);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = holder.imgView.getContext();

        GradientDrawable shapeDrawable;

        int color = mColors.get(position);
        if (color == mSelectColor) {
            shapeDrawable = (GradientDrawable) context.getDrawable(R.drawable.shape_rectange_color_select);
        } else {
            shapeDrawable = (GradientDrawable) context.getDrawable(R.drawable.shape_rectange_color_unselect);
        }
        shapeDrawable.setColor(color);
        holder.imgView.setBackground(shapeDrawable);

        holder.imgView.setOnClickListener(v -> {
            mSelectColor = color;
            if (mColorChangeListener != null) {
                mColorChangeListener.onColorStyleChange(mSelectColor);
            }
            notifyDataSetChanged();
        });
    }


    @Override
    public int getItemCount() {
        return mColors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }


    public interface ColorStyleListener {
        void onColorStyleChange(int newColor);
    }
}
