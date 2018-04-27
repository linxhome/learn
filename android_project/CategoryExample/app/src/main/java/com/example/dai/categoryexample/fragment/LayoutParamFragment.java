package com.example.dai.categoryexample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dai.categoryexample.R;

/**
 * Created by dai on 2018/4/25.
 * Comment:
 */
public class LayoutParamFragment extends Fragment {

    private LinearLayout mLinearLayout;
    private RelativeLayout mRelativeLayout;
    private View mRoot;
    private TextView mTextViewLl, mTextViewRl;
    private Button mBtnLL, mBtnRl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.activity_second, container, false);

        mLinearLayout = (LinearLayout) mRoot.findViewById(R.id.linear_layout);
        mRelativeLayout = (RelativeLayout) mRoot.findViewById(R.id.relative_layout);
        mTextViewLl = (TextView) mRoot.findViewById(R.id.ll_textview);
        mTextViewRl = (TextView) mRoot.findViewById(R.id.rl_textview);
        mBtnLL = (Button) mRoot.findViewById(R.id.linear_layout_btn);
        mBtnRl = (Button) mRoot.findViewById(R.id.relative_layout_btn);

        mBtnLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLinearLayoutParams();
            }
        });

        mBtnRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testRelativeLayoutParams();
            }
        });
        return mRoot;
    }

    private String singleLine = "sldfjdlsk 1123123123";
    private String twoLine = "x22234243455dxsfsdfesdfwerl2j3lj23oj2op3j9023 02iu03j2 ljkl dfsfsdfsdsdfsd ";
    private String threeLine = twoLine + twoLine;

    @Override
    public void onStart() {
        super.onStart();
        final TextView textview = (TextView) mRoot.findViewById(R.id.multi_line_text);
        final TextView showInfo = (TextView) mRoot.findViewById(R.id.show_info);

        //test the post runnable to get the line number
        textview.setMaxLines(2);
        textview.post(new Runnable() {
            @Override
            public void run() {
                int count = textview.getLayout().getLineCount();
                if (count > 0) {
                    int ellipse = textview.getLayout().getEllipsisCount(count - 1);
                    //String st = textview.getEllipsize().toString();
                    showInfo.setText("count = " + count + " ellipse count = " + ellipse);
                }
            }
        });
        textview.setText(threeLine);
    }

    private void testLinearLayoutParams() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTextViewLl.getLayoutParams();
        String output = layoutParams.debug("otestLinearLayoutParamsutput");
        Toast.makeText(getActivity(), output, Toast.LENGTH_SHORT).show();

        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.leftMargin = 20;
        layoutParams.weight = 1;//分配完控件自身的宽稿后，剩余的空间按照weight比例来分配，当width都为0的时候，就是所有空间用来按比例分配

        mTextViewLl.setLayoutParams(layoutParams);
    }

    private void testRelativeLayoutParams() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTextViewRl.getLayoutParams();
        //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.removeRule(RelativeLayout.END_OF);
        layoutParams.addRule(RelativeLayout.END_OF, R.id.rl_sample_textview);
        mTextViewRl.setLayoutParams(layoutParams);
    }
}
