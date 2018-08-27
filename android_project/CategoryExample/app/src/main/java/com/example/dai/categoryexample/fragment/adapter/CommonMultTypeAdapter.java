package com.example.dai.categoryexample.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barn.viewholder.annotation.BindHolder;
import com.barn.viewholder.impl.HolderUtils;
import com.barn.viewholder.impl.IHolderDelegate;
import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.fragment.dummy.DummyContent;
import com.example.dai.categoryexample.fragment.viewholder.TypeOneViewHolder;
import com.example.dai.categoryexample.fragment.viewholder.TypeThreeViewHolder;
import com.example.dai.categoryexample.fragment.viewholder.TypeTwoViewHolder;

import java.util.List;


public class CommonMultTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<DummyContent.DummyItem> mValue;


    public CommonMultTypeAdapter(List<DummyContent.DummyItem> mValue) {
        this.mValue = mValue;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case 1:
                rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_feed, parent, false);
                viewHolder = new ViewHolder1(rootView);
                break;
            case 2:
                rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_feed, parent, false);
                viewHolder = new ViewHolder1(rootView);
                break;
            default:
                rootView = new View(parent.getContext());
                viewHolder = new RecyclerView.ViewHolder(rootView) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 1) {
            ViewHolder1 holder1 = (ViewHolder1) holder;
            holder1.sequence.setText(mValue.get(position).getDetails());
            holder1.content.setText(mValue.get(position).getDetails());
        } else if (getItemViewType(position) == 2) {
            ViewHolder2 holder2 = (ViewHolder2) holder;
            holder2.content.setText(mValue.get(position).getDetails());
        }
    }

    @Override
    public int getItemCount() {
        return mValue.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mValue.get(position).getType();
    }

    static class ViewHolder1 extends RecyclerView.ViewHolder {
        TextView sequence;
        TextView content;

        public ViewHolder1(View itemView) {
            super(itemView);
            sequence = (TextView) itemView.findViewById(R.id.item_number);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }

    static class ViewHolder2 extends RecyclerView.ViewHolder {
        TextView content;

        public ViewHolder2(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.item_content);
        }
    }
}
