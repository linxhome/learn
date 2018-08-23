package com.example.dai.categoryexample.autoBind;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crock.impl.IHolderFactory;
import com.example.dai.categoryexample.fragment.adapter.TypeOneViewHolder;
import com.example.dai.categoryexample.fragment.adapter.TypeTwoViewHolder;
import com.example.dai.categoryexample.fragment.dummy.DummyContent;


/**
 * this will by generator by apt
 */
public class ExampleHolderFactory implements IHolderFactory {

    TypeOneViewHolder typeOneViewHolder;
    TypeTwoViewHolder typeTwoViewHolder;

    public ExampleHolderFactory() {
        typeOneViewHolder = new TypeOneViewHolder();
        typeTwoViewHolder = new TypeTwoViewHolder();
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(int type, ViewGroup parent) {
        View rootView;
        int resouceId = 0;
        switch (type) {
            case 1:
                resouceId = android.R.drawable.alert_dark_frame;
                break;
            case 2:
                resouceId = android.R.drawable.alert_dark_frame;
                break;
            default:
                break;
        }
        if (resouceId == 0) {
            rootView = new View(parent.getContext());
        } else {
            rootView = LayoutInflater.from(parent.getContext()).inflate(resouceId, parent, false);
        }
        return new RecyclerView.ViewHolder(rootView) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
    }

    @Override
    public void executeBind(int viewType, RecyclerView.ViewHolder holder, Object data) {
        switch (viewType) {
            case 1:
                typeOneViewHolder.executeBind(holder.itemView, (DummyContent.DummyItem) data);
                break;
            case 2:
                typeTwoViewHolder.executeBind(holder.itemView, (DummyContent.DummyItem) data);
                break;
            default:
                break;
        }
    }

}
