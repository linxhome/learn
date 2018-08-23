package com.example.dai.categoryexample.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.crock.impl.IHolderFactory;
import com.crock.impl.annotation.InjectHolder;
import com.crock.impl.HolderUtils;
import com.example.dai.categoryexample.fragment.dummy.DummyContent;

import java.util.List;


public class TestMultiRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<DummyContent.DummyItem> mValue;

    IHolderFactory viewHolderFactory;

    @InjectHolder(item_type = 1)
    TypeOneViewHolder typeOneViewHolder = new TypeOneViewHolder();

    @InjectHolder(item_type = 2)
    TypeTwoViewHolder typeTwoViewHolder = new TypeTwoViewHolder();


    public TestMultiRecyclerAdapter(List<DummyContent.DummyItem> mValue) {
        this.mValue = mValue;
        viewHolderFactory = HolderUtils.createHolderFactory(this);
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
        return (position % 3 == 0) ? 1 : 2;
    }
}
