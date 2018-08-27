package com.example.dai.categoryexample.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.barn.viewholder.annotation.BindHolder;
import com.barn.viewholder.impl.IHolderDelegate;
import com.barn.viewholder.impl.HolderUtils;
import com.example.dai.categoryexample.fragment.dummy.DummyContent;
import com.example.dai.categoryexample.fragment.viewholder.TypeOneViewHolder;
import com.example.dai.categoryexample.fragment.viewholder.TypeThreeViewHolder;
import com.example.dai.categoryexample.fragment.viewholder.TypeTwoViewHolder;

import java.util.List;


public class TestMultiRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<DummyContent.DummyItem> mValue;

    IHolderDelegate viewHolderFactory;

    @BindHolder(item_type = 1)
    TypeOneViewHolder typeOneViewHolder = new TypeOneViewHolder();

    @BindHolder(item_type = 2)
    TypeTwoViewHolder typeTwoViewHolder = new TypeTwoViewHolder();

    @BindHolder(item_type = 3)
    TypeThreeViewHolder typeThreeViewView = new TypeThreeViewHolder();


    public TestMultiRecyclerAdapter(List<DummyContent.DummyItem> mValue) {
        this.mValue = mValue;
        viewHolderFactory = HolderUtils.createHolderDelegate(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewHolderFactory.getViewHolder(viewType, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        viewHolderFactory.executeBind(getItemViewType(position), holder, mValue.get(position));
    }

    @Override
    public int getItemCount() {
        return mValue.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mValue.get(position).getType();
    }
}
