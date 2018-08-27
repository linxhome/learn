package com.example.dai.categoryexample.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.fragment.CanvasFragment;
import com.example.dai.categoryexample.fragment.CoordinateFragment;
import com.example.dai.categoryexample.fragment.LaunchModeFragment;
import com.example.dai.categoryexample.fragment.MultiFeedTypeFragment;
import com.example.dai.categoryexample.fragment.NestScrollFragment;
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //增加页面的位置
        add("View And Gone Problem", VisibleGoneFragment.newInstance("1", "2"));
        add("Recycler View Test", NotifyItemFragment.newInstance(1));
        add("Layout params test", new LayoutParamFragment());
        add("Notification Problem", new NotificationFragment());
        add("Non UI Thread Problem", new UIThreadFragment());
        add("Canvas draw bitmap", new CanvasFragment());
        add("Storage", new StoreageFragment());
        add("Drawable Example", new DrawableFragment());
//        add("NestScroll Example", new NestScrollFragment());
        add("Coordinate Example", new CoordinateFragment());
        add("Activity Launch Mode Example", LaunchModeFragment.newInstance("start", "no"));
        add("Multi Feed Type", MultiFeedTypeFragment.newInstance(1));

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void add(String fragmentName, Fragment fragmentClass) {
        mFragmentMap.put(fragmentName, fragmentClass);
        mAdapter.addData(fragmentName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

            holder.mContainer.setOnClickListener(v -> {
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                String name1 = mData.get(position);
                Fragment fragment = mFragmentMap.get(name1);
                if (fragment != null && !fragment.isAdded()) {
                    android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.add(android.R.id.content, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Log.i("FragmentManagerActivity", name1 + "is null or has added");
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
