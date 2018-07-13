package com.example.dai.categoryexample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button mToastButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_ui_thread,container,false);
        mText1 = (TextView) mRoot.findViewById(R.id.text1);
        mText2 = (TextView) mRoot.findViewById(R.id.text2);
        mText3 = (TextView) mRoot.findViewById(R.id.text3);
        mButton = (Button) mRoot.findViewById(R.id.test_thread);
        mToastButton = mRoot.findViewById(R.id.toast_btn);
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

        mButton.setOnClickListener(v -> {
            if(mText1.getVisibility() == View.VISIBLE) {
                mText1.setVisibility(View.GONE);
                mText2.setVisibility(View.GONE);
                mText3.setVisibility(View.GONE);
            } else {
                mText1.setVisibility(View.VISIBLE);
                mText2.setVisibility(View.VISIBLE);
                mText3.setVisibility(View.VISIBLE);
            }
        });

        mToastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast to = new Toast(getActivity());
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_for_toast,null);
                ImageView imageView = view.findViewById(R.id.toast_img);
                TextView txtview = view.findViewById(R.id.toast_txt);
                imageView.setImageResource(R.drawable.btn_delete_big_light);
                txtview.setText("收到了房价肯定是疯了看见了空垃圾四大佛教收到了房价多少发拉家带口司法解释的反馈收到了反g倒是");

                v.post(new Runnable() {
                    @Override
                    public void run() {
                        to.setView(view);
                        to.setGravity(Gravity.CENTER,0,0);
                        to.show();
                    }
                });

            }
        });



    }
}
