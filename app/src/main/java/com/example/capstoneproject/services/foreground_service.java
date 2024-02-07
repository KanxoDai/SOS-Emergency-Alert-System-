package com.example.capstoneproject.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.capstoneproject.R;
//import com.example.capstoneproject.Shake.shake_service;

public class foreground_service extends Service {

    public static final String CHANNEL_ID = "exampleServiceChannel";

   @RequiresApi(Build.VERSION_CODES.O)
    public int onStartCommand(Intent intent, int flags, int startId)
   {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

       NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Safety First is Running .")
                .setContentText("We are there for you")

                //this is important, otherwise the notification will show the way
                //you want i.e. it will show some default notification
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {

        //create an Intent to call the Broadcast receiver
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
       // broadcastIntent.setClass(this, shake_service.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }


 /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);*/


//         PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);


    /*    PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getBroadcast
                    (this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE );
        }
        else
        {
            pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }*/


    /*    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Women Security Running in Background")
                .setContentText(input)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.ic_security)
                .setContentIntent(pendingIntent)
                .build();

        startService(new Intent(getApplicationContext(), shake_service.class));
        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), shake_service.class));
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}