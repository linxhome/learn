package com.example.dai.categoryexample.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.fragment.CanvasFragment;
import com.example.dai.categoryexample.fragment.CoordinatorFragment;
import com.example.dai.categoryexample.fragment.DrawableFragment;
import com.example.dai.categoryexample.fragment.LayoutParamFragment;
import com.example.dai.categoryexample.fragment.NotificationFragment;
import com.example.dai.categoryexample.fragment.NotifyItemFragment;
import com.example.dai.categoryexample.fragment.StoreageFragment;
import com.example.dai.categoryexample.fragment.UIThreadFragment;
import com.example.dai.categoryexample.fragment.VisibleGoneFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * interface.
 */
public class FragmentManagerActivity extends FragmentActivity {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private HashMap<String, Fragment> mFragmentMap = new HashMap<>();

    public FragmentManagerActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_item_list);
        setTitle("FragmentManager");
        setTitleColor(Color.BLUE);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);

        //增加页面的位置
        add("View And Gone Problem", VisibleGoneFragment.newInstance("1","2"));
        add("Recycler View Test", NotifyItemFragment.newInstance(1));
        add("layout params test",new LayoutParamFragment());
        add("Notifycation Problem",new NotificationFragment());
        add("Non UI Thread Problem",new UIThreadFragment());
        add("Canvas draw bitmap",new CanvasFragment());
        add("Storage",new StoreageFragment());
        add("DrawableFragment",new DrawableFragment());
        add("CoordinatorFragment",new CoordinatorFragment());
    }

    private void add(String fragmentName, Fragment fragmentClass) {
        mFragmentMap.put(fragmentName, fragmentClass);
        mAdapter.addData(fragmentName);
    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<String> mData = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.fragment_item, parent, false);
            TextView idText = (TextView) view.findViewById(R.id.item_id);
            TextView contentText = (TextView) view.findViewById(R.id.item_content);
            return new ViewHolder(idText, contentText, view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            String name = mData.get(position);
            holder.mContentView.setText(name);
            holder.mTextView.setText(position + 1 + ":");

            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    String name = mData.get(position);
                    Fragment fragment = mFragmentMap.get(name);
                    if (fragment != null && !fragment.isAdded()) {
                        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.add(android.R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Log.i("FragmentManagerActivity", name + "is null or has added");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        void addData(String name) {
            mData.add(name);
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        View mContainer;
        TextView mTextView;
        TextView mContentView;

        ViewHolder(TextView textView, TextView contentView, View container) {
            super(container);
            mTextView = textView;
            mContentView = contentView;
            mContainer = contentView;
        }
    }


}
