package com.example.dai.categoryexample.fragment;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.activity.EmptyActivity;

/**
 * Created by dai on 2018/4/23.
 * Comment:
 */
public class NotificationFragment extends Fragment implements View.OnClickListener {
    Button mButton1;
    Button mButton2;
    Button mButton3;
    Button mButton4;
    Button mButton5;
    Handler mhandler;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_notification_send, container, false);
        mButton1 = (Button) root.findViewById(R.id.btn1);
        mButton2 = (Button) root.findViewById(R.id.btn2);
        mButton3 = (Button) root.findViewById(R.id.btn3);
        mButton4 = (Button) root.findViewById(R.id.btn4);
        mButton5 = (Button) root.findViewById(R.id.btn5);

        mButton1.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton5.setOnClickListener(this);



        mhandler = new Handler(Looper.getMainLooper());
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
                createNotification();
                break;
            default:
                break;
        }
    }


    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.navbar_ic_set)
                .setContentTitle("ContentTitle")
                .setContentText("Content Text is here");
        Intent intent = new Intent(getActivity(), EmptyActivity.class);

        /*TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getActivity());
        taskStackBuilder.addParentStack(EmptyActivity.class);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
*/
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(11, builder.build());


    }


}
