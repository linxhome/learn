package com.example.dai.categoryexample.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;


public abstract class RecyclerLoadMoreAdapter extends RecyclerView.Adapter {
    private View mFooter;
    private boolean isLoadingEnable = false;
    private int LOADING_TYPE = -1000;


    public void setLoadingEnable(boolean loadEnable) {
        isLoadingEnable = loadEnable;
    }

    public void setLoadingView(View footer) {
        mFooter = footer;
    }

    abstract int doGetItemCount();

    abstract void doOnBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position);

    abstract RecyclerViewAdapter.ViewHolder doOnCreateViewHolder(@NotNull ViewGroup parent, int viewType);

    abstract int doGetItemViewType(int position);

    @Override
    public final int getItemCount() {
        if (isLoadingEnable && mFooter != null) {
            return doGetItemCount() + 1;
        } else {
            return doGetItemCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoadingEnable && mFooter != null && position == getItemCount() - 1) {
            return LOADING_TYPE;
        } else {
            return doGetItemViewType(position);
        }
    }

    @Override
    public final void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (position < doGetItemCount()) {
            doOnBindViewHolder(holder, position);
        }
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == LOADING_TYPE) {
            return new EmptyViewHolder(mFooter);
        } else {
            return doOnCreateViewHolder(parent, viewType);
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

}
