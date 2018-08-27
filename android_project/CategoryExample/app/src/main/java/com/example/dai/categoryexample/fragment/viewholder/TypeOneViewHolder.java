package com.example.dai.categoryexample.fragment.viewholder;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.barn.viewholder.impl.IViewHolder;
import com.barn.viewholder.annotation.BindItemView;
import com.example.dai.categoryexample.R;

@BindItemView(view_id = R.layout.item_multi_feed)
public class TypeOneViewHolder implements IViewHolder {

    @Override
    public void executeBind(View rootView, Object data) {
        TextView textView = (TextView) rootView.findViewById(R.id.item_number);
        TextView content = (TextView) rootView.findViewById(R.id.content);

        textView.setText("txt");
        content.setText("content type one");

        rootView.setOnClickListener(v -> Toast.makeText(rootView.getContext(),
                "Helleo", Toast.LENGTH_SHORT).show());
    }
}


