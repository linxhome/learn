package com.barn.viewholder.impl;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barn.viewholder.annotation.BindHolder;
import com.barn.viewholder.annotation.BindItemView;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * read the annotation #BindHolder
 * create the view holder delegate for the multi type recycler view list
 */
public class HolderUtils {

    public static IHolderDelegate createHolderDelegate(Object injectObject) {
        if (injectObject == null) {
            throw new RuntimeException("object must not bu null");
        }
        //read view holder from the object
        InnerDelegate delegate = new InnerDelegate();
        createViewHolderMap(injectObject, delegate.holderMap);
        return delegate;
    }

    private static class InnerDelegate implements IHolderDelegate {

        HashMap<Integer, HolderContainer> holderMap = new HashMap<>();

        @Override
        public RecyclerView.ViewHolder getViewHolder(int viewType, ViewGroup parent) {
            if (holderMap.containsKey(viewType)) {
                HolderContainer holderContainer = holderMap.get(viewType);
                View rootView;
                if (holderContainer.isDataBinding) {
                    ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), (int) holderContainer.resourceId, parent, false);
                    rootView = dataBinding.getRoot();
                    return new DelegateRViewHolder(rootView, dataBinding);
                } else {
                    rootView = LayoutInflater.from(parent.getContext())
                            .inflate((int) holderMap.get(viewType).resourceId, parent, false);
                    return new DelegateRViewHolder(rootView);
                }
            } else {
                return new DelegateRViewHolder(new View(parent.getContext()));
            }
        }

        @Override
        public void executeBind(int viewType, RecyclerView.ViewHolder holder, Object data) {
            View rootView = holder.itemView;
            if (holderMap.containsKey(viewType)) {
                HolderContainer holderContainer = holderMap.get(viewType);
                if (holderContainer.isDataBinding && holder instanceof DelegateRViewHolder) {
                    ViewDataBinding dataBinding = ((DelegateRViewHolder) holder).dataBinding;
                    if (dataBinding != null) {
                        holderContainer.databindingHolder.executeBind(dataBinding, data);
                    }
                } else {
                    holderContainer.viewHolder.executeBind(rootView, data);
                }
            }
        }
    }

    private static class DelegateRViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding dataBinding;

        DelegateRViewHolder(View itemView) {
            super(itemView);
        }

        public DelegateRViewHolder(View itemView, ViewDataBinding dataBinding) {
            super(itemView);
            this.dataBinding = dataBinding;
        }
    }

    private static void registerViewHolder(int viewType, Object holder, HashMap<Integer, HolderContainer> map) {
        //read the id
        long resourceId = -1;

        BindItemView annotationHolder = holder.getClass().getAnnotation(BindItemView.class);
        if (annotationHolder == null) {
            return;
        }
        resourceId = annotationHolder.view_id();
        if (resourceId == -1 && viewType == -1) {
            return;
        }
        if (holder instanceof IDataBindingHolder) {
            map.put(viewType, new HolderContainer(resourceId, (IDataBindingHolder) holder));
        } else if (holder instanceof IViewHolder) {
            map.put(viewType, new HolderContainer(resourceId, (IViewHolder) holder));
        }
    }


    private static void createViewHolderMap(Object obj, HashMap<Integer, HolderContainer> map) {
        Field[] fields = obj.getClass().getFields();
        for (Field field : fields) {
            BindHolder injectView = field.getAnnotation(BindHolder.class);
            if (injectView != null) {
                try {
                    if (IViewHolder.class.isAssignableFrom(field.getType())
                            || IDataBindingHolder.class.isAssignableFrom(field.getType())) {
                        Object fieldObj = field.get(obj);
                        if (fieldObj == null) {
                            fieldObj = field.getType().newInstance();
                        }
                        registerViewHolder(injectView.item_type(), fieldObj, map);
                    }

                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class HolderContainer {
        long resourceId;
        boolean isDataBinding = false;

        IDataBindingHolder databindingHolder;//支持databinding的view layout
        IViewHolder viewHolder; //支持普通的view layout

        HolderContainer(long resourceId, IViewHolder holder) {
            this.resourceId = resourceId;
            this.viewHolder = holder;
        }

        HolderContainer(long resourceId, IDataBindingHolder databindingHolder) {
            this.resourceId = resourceId;
            this.databindingHolder = databindingHolder;
            isDataBinding = true;
        }
    }


}
