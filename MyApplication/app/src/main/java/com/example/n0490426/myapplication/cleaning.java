package com.example.n0490426.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static android.content.Context.NOTIFICATION_SERVICE;

public class cleaning extends Fragment {
    Button NotificationButton;
    NotificationCompat.Builder notification;
    private static final int uniqueID = 6758374;//hard coded ID for now, needs to be unique

    public cleaning() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cleaning, container, false);
        NotificationButton = (Button) view.findViewById(R.id.notificationButton);
        NotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNotificationButtonClicked();
            }
        });

        notification = new NotificationCompat.Builder(getActivity());
        notification.setAutoCancel(true); //Used to delete the notification when on this screen
        return view;
    }

    public void setNotificationButtonClicked() {
        NotificationManager nm = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        notification.setSmallIcon(R.drawable.houselogo2);
//        notification.setTicker("This is the ticker");
//        notification.setWhen(System.currentTimeMillis());
//        notification.setContentTitle("MyHouse is the title");
//        notification.setContentText("This is the notifications main text");
//        Intent intent = new Intent(getActivity(), MainActivity.class);
//        PendingIntent pi = PendingIntent.getActivities(getActivity(), 0, new Intent[]{intent}, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.setContentIntent(pi);
//        nm.notify(uniqueID, notification.build());

//        Intent intent = new Intent(getContext(), reminder.class);
//        PendingIntent pi = PendingIntent.getActivity(getActivity(), (int) System.currentTimeMillis(), intent, 0);
//        Notification notification = new Notification.Builder(getActivity()).setSmallIcon(R.drawable.houselogo2).setContentText("This is a test").setContentTitle("This is the notification").setContentIntent(pi).build();
//        nm.notify(0, notification);
    }
}
