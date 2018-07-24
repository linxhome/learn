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
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dai.categoryexample.R;
import com.example.dai.categoryexample.activity.EmptyActivity;
import com.example.dai.categoryexample.receiver.OreoReceiver;

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
                sendNotifycation();
                break;
            case R.id.btn2:
                updateNotification();
                break;
            case R.id.btn3:
                normalActivity();
                break;
            case R.id.btn4:
                specialActivity();
                break;
            case R.id.btn5:
                changeIntent();
                break;
            default:
                break;
        }
    }

    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    int notifyId = 1;


    private void createNotification() {
        builder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.navbar_ic_set)
                .setContentTitle("ContentTitle")
                .setContentText("Content Text is here");

        Intent intent = new Intent(getActivity(), EmptyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent boardIntent = new Intent(getActivity(), OreoReceiver.class);
        PendingIntent cancelIntent = PendingIntent.getBroadcast(getActivity(), 0, boardIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(cancelIntent);
        builder.setAutoCancel(true);
        builder.setSubText("sub text");
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void sendNotifycation() {
        createNotification();
        notificationManager.notify(notifyId++, builder.build());
    }

    private void updateNotification() {
        createNotification();
        //builder.setNumber(10);
        builder.setContentText("update a content");
        notificationManager.notify(notifyId, builder.build()); // notifyId唯一表示了一个通知项，可以通过id来更新或者cancel一个通知消息
    }

    private void changeIntent() {
        Intent intent = new Intent(getActivity(), EmptyActivity.class);
        PendingIntent content = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(content);
    }

    /**
     * notification 常规activity，启动全新任务,并提供应用的返回栈，返回栈定义在parent activity中
     */
    private void normalActivity() {
        Intent result = new Intent(getActivity(), EmptyActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        stackBuilder.addParentStack(EmptyActivity.class);
        stackBuilder.addNextIntent(result);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_CANCEL_CURRENT);
        builder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.navbar_ic_set)
                .setContentTitle("normal activity")
                .setContentText("start normal activity")
                .setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyId, builder.build());
    }


    /**
     * 独立的task栈
     */
    private void specialActivity() {
        Intent intent = new Intent(getActivity(), EmptyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.navbar_ic_set)
                .setContentTitle("normal activity")
                .setContentText("start normal activity");
        builder.setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyId,builder.build());
    }


}
