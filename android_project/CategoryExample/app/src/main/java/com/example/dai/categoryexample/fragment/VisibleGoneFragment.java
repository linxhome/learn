package com.example.dai.categoryexample.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dai.categoryexample.R;

public class VisibleGoneFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public VisibleGoneFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visible_gone, container, false);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();

        final View parent = getView().findViewById(R.id.parent_layout);
        final View child = getView().findViewById(R.id.child_text);
        View childBtn = getView().findViewById(R.id.change_child_visible);
        View parentBtn = getView().findViewById(R.id.set_parent_visible);

        parentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parent.getVisibility() == View.GONE) {
                    parent.setVisibility(View.VISIBLE);
                } else {
                    parent.setVisibility(View.GONE);
                }
                showResult(parent, child);
            }
        });

        childBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (child.getVisibility() == View.GONE) {
                    child.setVisibility(View.VISIBLE);
                } else {
                    child.setVisibility(View.GONE);
                }
                showResult(parent, child);
            }
        });

        getView().findViewById(R.id.test_receiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.test.oreo");
                getActivity().sendBroadcast(intent);
            }
        });
    }

    private void showResult(View parent, View child) {
        String result = "";
        if (parent.getVisibility() == View.VISIBLE) {
            result += "parent is visible ";
        } else {
            result += "parent is gone ";
        }

        if (child.getVisibility() == View.VISIBLE) {
            result += "child is visible ";
        } else {
            result += "child is gone ";
        }
        TextView txtView = (TextView) getView().findViewById(R.id.result_txt);
        txtView.setText(result);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static VisibleGoneFragment newInstance(String param1, String param2) {
        VisibleGoneFragment fragment = new VisibleGoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

}
