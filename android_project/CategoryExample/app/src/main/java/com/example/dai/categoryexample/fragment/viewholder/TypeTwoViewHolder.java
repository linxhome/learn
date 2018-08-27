package com.example.dai.categoryexample.fragment.viewholder;

import android.view.View;
import android.widget.TextView;

import com.barn.viewholder.impl.IViewHolder;
import com.barn.viewholder.annotation.BindItemView;
import com.example.dai.categoryexample.R;

@BindItemView(view_id = R.layout.item_multi_feed2)
public class TypeTwoViewHolder implements IViewHolder {

    @Override
    public void executeBind(View rootView, Object data) {
        TextView textView = (TextView) rootView.findViewById(R.id.item_contents);

        textView.setText("feed2 text");
    }

}
