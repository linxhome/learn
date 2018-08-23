package com.example.dai.categoryexample.fragment.adapter;

import android.view.View;
import android.widget.TextView;

import com.crock.impl.IHolderBind;
import com.crock.impl.annotation.HolderView;
import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.fragment.dummy.DummyContent;

@HolderView(view_id = R.layout.item_multi_feed2)
public class TypeTwoViewHolder implements IHolderBind<DummyContent.DummyItem> {

    @Override
    public void executeBind(View rootView, DummyContent.DummyItem data) {
        TextView textView = (TextView) rootView.findViewById(R.id.item_number);

        textView.setText("feed2 text");

    }

}
