package com.crock.impl;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crock.impl.annotation.HolderView;
import com.crock.impl.annotation.InjectHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * read and parse the annotation #HoolderBindView and #InjectHolder about the recyclerview adapter
 * create the holder creater factory for the addpter
 */
public class HolderUtils {

    public static IHolderFactory createHolderFactory(Object injectObject) {
        if (injectObject == null) {
            throw new RuntimeException("object must not bu null");
        }
        //read view holder from the object
        InnerHolderFactory factory = new InnerHolderFactory();
        parseViewHolder(injectObject, factory.viewHolderMap);
        return factory;
    }

    static class InnerHolderFactory implements IHolderFactory {

        HashMap<Integer, HolderContainer> viewHolderMap = new HashMap<>();

        @Override
        public RecyclerView.ViewHolder getViewHolder(int viewType, ViewGroup parent) {
            if (viewHolderMap.containsKey(viewType)) {
                View rootView = LayoutInflater.from(parent.getContext())
                        .inflate((int) viewHolderMap.get(viewType).resourceId, parent, false);
                return new RecyclerView.ViewHolder(rootView) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            } else {
                return new RecyclerView.ViewHolder(new View(parent.getContext())) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }
        }

        @Override
        public void executeBind(int viewType, RecyclerView.ViewHolder holder, Object data) {
            int type = holder.getItemViewType();
            View rootView = holder.itemView;
            if (viewHolderMap.containsKey(type)) {
                viewHolderMap.get(type).holder.executeBind(rootView, data);
            }
        }
    }

    private static void registerViewHolder(int viewType, IHolderBind holder, HashMap<Integer, HolderContainer> map) {
        //read the id
        long resourceId = -1;

        HolderView annotationHolder = holder.getClass().getAnnotation(HolderView.class);
        if (annotationHolder == null) {
            return;
        }
        resourceId = annotationHolder.view_id();

        if (resourceId == -1 && viewType == -1) {
            return;
        }

        map.put(viewType, new HolderContainer(resourceId, holder));
    }


    private static void parseViewHolder(Object obj, HashMap<Integer, HolderContainer> map) {
        Field[] fields = obj.getClass().getFields();
        for (Field field : fields) {
            InjectHolder injectView = field.getAnnotation(InjectHolder.class);
            if (injectView != null) {
                try {
                    if (IHolderBind.class.isAssignableFrom(field.getType())) {
                        Object fieldObjec = field.get(obj);
                        if (fieldObjec != null) {
                            HolderUtils.registerViewHolder(injectView.item_type(), (IHolderBind) fieldObjec, map);
                        } else {
                            Object objectValue = field.getType().newInstance();
                            HolderUtils.registerViewHolder(injectView.item_type(), (IHolderBind) objectValue, map);
                        }
                    }
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class HolderContainer {
        long resourceId;
        IHolderBind holder;

        HolderContainer(long resourceId, IHolderBind holder) {
            this.resourceId = resourceId;
            this.holder = holder;
        }
    }

}
