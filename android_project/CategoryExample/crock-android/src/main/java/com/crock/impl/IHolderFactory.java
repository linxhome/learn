package com.crock.impl;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface IHolderFactory {

    RecyclerView.ViewHolder getViewHolder(int viewType, ViewGroup parent);

    void executeBind(int viewType, RecyclerView.ViewHolder holder, Object data);

}
