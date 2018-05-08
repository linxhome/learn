package com.example.dai.categoryexample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dai.categoryexample.R;

/**
 * Created by dai on 2018/5/3.
 * Comment:
 */
public class UIThreadFragment extends Fragment {
    private View mRoot;
    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private Button mButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_ui_thread,container,false);
        mText1 = (TextView) mRoot.findViewById(R.id.text1);
        mText2 = (TextView) mRoot.findViewById(R.id.text2);
        mText3 = (TextView) mRoot.findViewById(R.id.text3);
        mButton = (Button) mRoot.findViewById(R.id.test_thread);
        return mRoot;
    }

    @Override
    public void onStart() {
        super.onStart();
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(200);//这里有个神奇的问题，非ui线程访问ui，catch住抛出的异常之后，部分元素不会relayout了
                    mText2.setText("change in thread");
                } catch(Exception e) {
                    Log.e("UIThreadFragment","catch",e);
                }
            }
        };
        thread.start();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mText1.getVisibility() == View.VISIBLE) {
                    mText1.setVisibility(View.GONE);
                    mText2.setVisibility(View.GONE);
                    mText3.setVisibility(View.GONE);
                } else {
                    mText1.setVisibility(View.VISIBLE);
                    mText2.setVisibility(View.VISIBLE);
                    mText3.setVisibility(View.VISIBLE);
                }
            }
        });



    }
}
