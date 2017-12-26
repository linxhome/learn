package com.example.dai.categoryexample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dai.categoryexample.R;

/**
 * Created by dai
 * activity for the layotu params test
 * on 17/9/15.
 */

public class SecondActivity extends Activity {
    private LinearLayout mLinearLayout;
    private RelativeLayout mRelativeLayout;
    private TextView mTextViewLl, mTextViewRl;
    private Button mBtnLL,mBtnRl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        mTextViewLl = (TextView) findViewById(R.id.ll_textview);
        mTextViewRl = (TextView) findViewById(R.id.rl_textview);
        mBtnLL = (Button) findViewById(R.id.linear_layout_btn);
        mBtnRl = (Button) findViewById(R.id.relative_layout_btn);

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

    }

    private String singleLine = "sldfjdlsk 1123123123";
    private String twoLine = "x22234243455dxsfsdfesdfwerl2j3lj23oj2op3j9023 02iu03j2 ljkl dfsfsdfsdsdfsd ";
    private String threeLine = twoLine + twoLine;

    @Override
    protected void onStart() {
        super.onStart();
        final TextView textview = (TextView) findViewById(R.id.multi_line_text);
        final TextView showInfo = (TextView) findViewById(R.id.show_info);

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
        Toast.makeText(this, output, Toast.LENGTH_SHORT).show();

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
