package com.example.dai.categoryexample.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.activity.launchmode.SingleInstanceActivity;
import com.example.dai.categoryexample.activity.launchmode.SingleTaskActivity;
import com.example.dai.categoryexample.activity.launchmode.SingleTopActivity;
import com.example.dai.categoryexample.activity.launchmode.StandardActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LaunchModeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LaunchModeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    @BindView(R.id.standardBtn)
    Button standardBtn;
    @BindView(R.id.singleTaskBtn)
    Button singleTaskBtn;
    @BindView(R.id.singleTopBtn)
    Button singleTopBtn;
    @BindView(R.id.singleInstanceBtn)
    Button singleInstanceBtn;
    @BindView(R.id.name_txt)
    TextView nameTxtView;

    @BindView(R.id.FClearTopBtn)
    Button clearTopBtn;
    @BindView(R.id.FNewTaskBtn)
    Button newtaskBtn;
    @BindView(R.id.FNewTaskClearTopBtn)
    Button newTaskClearTopBtn;
    @BindView(R.id.FSingleTopClearTopBtn)
    Button singleTopClearTopBtn;


    public LaunchModeFragment() {
        // Required empty public constructor
    }

    public static LaunchModeFragment newInstance(String param1, String param2) {
        LaunchModeFragment fragment = new LaunchModeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (TextUtils.isEmpty(mParam1)) {
            mParam1 = "start activity";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_launch_model, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        nameTxtView.setText("name: " + mParam1 + " flag:" + mParam2);
        singleTaskBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SingleTaskActivity.class);
            getActivity().startActivity(intent);
        });
        singleInstanceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SingleInstanceActivity.class);
            getActivity().startActivity(intent);
        });
        singleTopBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SingleTopActivity.class);
            getActivity().startActivity(intent);
        });
        standardBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StandardActivity.class);
            getActivity().startActivity(intent);
        });

        //flag test area
        clearTopBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StandardActivity.class);
            intent.putExtra("flag", "clear top");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        });
        newtaskBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), StandardActivity.class);
            intent.putExtra("flag", "new task");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
        });
        newTaskClearTopBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), StandardActivity.class);
            intent.putExtra("flag", "new task and clear top");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        });
        singleTopClearTopBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), StandardActivity.class);
            intent.putExtra("flag", "single top clear top");
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        });


    }
}
