package com.example.dai.categoryexample.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dai.categoryexample.R;

/**
 * Created by dai on 2018/4/23.
 * Comment:
 */
public class NotifycationFragment extends Fragment implements View.OnClickListener {
    Button mButton1;
    Button mButton2;
    Button mButton3;
    Button mButton4;
    Button mButton5;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_notification_send, container, false);
        mButton1 = (Button) root.findViewById(R.id.btn1);
        mButton2 = (Button) root.findViewById(R.id.btn2);
        mButton3 = (Button) root.findViewById(R.id.btn3);
        mButton4 = (Button) root.findViewById(R.id.btn4);
        mButton5 = (Button) root.findViewById(R.id.btn5);
        return root;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                break;
            case R.id.btn2:
                break;
            case R.id.btn3:
                break;
            case R.id.btn4:
                break;
            case R.id.btn5:
                break;
            default:
                break;
        }
    }


    private void createNotifycation() {

    }
}
