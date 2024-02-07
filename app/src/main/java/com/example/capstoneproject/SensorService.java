package com.example.capstoneproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
public class SensorService extends Service implements SensorEventListener {
    // TAG to identify notification
    private static final int NOTIFICATION = 007;
    // IBinder object to allow Activity to connect
    private final IBinder mBinder = new LocalBinder();
    // Sensor Objects
    private Sensor accelerometer,gyrosensor;
    private SensorManager mSensorManager,gyrosensorManager;
    private double accelerationX, accelerationY, accelerationZ;
    private double gyrox, gyroy, gyroz;
    private float threshold = 40f; //gravity rakheko 1g= 10, 4g= 40
    private float gyrovalue= 3f;
    // Notification Manager
    private NotificationManager mNotificationMa3nager;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    public class LocalBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //for gyroscope
        gyrosensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyrosensor = gyrosensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyrosensorManager.registerListener(this, gyrosensor, SensorManager.SENSOR_DELAY_NORMAL);

        showNotification();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        mSensorManager.unregisterListener(this);// Unregister sensor when not in use
        gyrosensorManager.unregisterListener(this);
        mNotificationManager.cancel(NOTIFICATION);
        stopSelf();
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        accelerationX = (Math.round(sensorEvent.values[0]*1000)/1000.0);
        accelerationY = (Math.round(sensorEvent.values[1]*1000)/1000.0);
        accelerationZ = (Math.round(sensorEvent.values[2]*1000)/1000.0);

        gyrox = (Math.round(sensorEvent.values[0]*1000)/1000.0);
        gyroy = (Math.round(sensorEvent.values[1]*1000)/1000.0);
        gyroz = (Math.round(sensorEvent.values[0]*1000)/1000.0);

        /*** Detect Accident ***/
        if ((accelerationX > threshold || accelerationY > threshold || accelerationZ > threshold) /* &&
                ((gyrox > gyrovalue || gyroy>gyrovalue  || gyroz >gyrovalue)
                && (gyrox <-gyrovalue || gyroy <-gyrovalue  || gyroz <-gyrovalue))*/) {

            Intent mIntent = new Intent();
            mIntent.setClass(this, send_sms.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mSensorManager.unregisterListener(this);
            gyrosensorManager.unregisterListener(this); // Unregister sensor when not in use
            mNotificationManager.cancel(NOTIFICATION);
            stopSelf();
            startActivity(mIntent);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
    private void showNotification() {
        Log.d("SERVICE DEBUG", "Notification Shown");
        CharSequence text = "Started Data Collection";

        Intent resultIntent = new Intent(this, dashboard.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Notification mNotification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.alarm)
                    .setTicker(text)
                    .setContentTitle("Hello there!")
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .build();

            mNotificationManager.notify(NOTIFICATION, mNotification);
        }
    }
}