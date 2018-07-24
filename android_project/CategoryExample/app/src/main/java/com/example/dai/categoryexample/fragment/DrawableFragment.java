package com.example.dai.categoryexample.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dai.categoryexample.R;

public class DrawableFragment extends Fragment {

    private ImageView mShape1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_drawable, container, false);
        mShape1 = (ImageView) root.findViewById(R.id.p_shape1);
        setDrawable();
        return root;
    }

    private void setDrawable() {
        mShape1.setBackground(createShapeDrawable());

        mShape1.postDelayed(() -> mShape1.setSelected(true),3000);
    }

    private Drawable createShapeDrawable() {
        GradientDrawable pressShape = new GradientDrawable();
        pressShape.setCornerRadius(20);
        pressShape.setStroke(2,Color.YELLOW);

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[] {android.R.attr.state_selected},pressShape);
        stateListDrawable.addState(new int[] {-android.R.attr.state_selected},null);
        return stateListDrawable;
    }
}
