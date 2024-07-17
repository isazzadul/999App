package com.aibrains.emergency;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationService extends Service implements LocationListener {

    private static final String TAG = "LocationService";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    private LocationManager mLocationManager;
    private DatabaseReference mDatabase;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    SessionManager sessionManager ;
    private Handler handler;
    private Runnable smsRunnable;
    int i = 0;
    double latitude , longitude;

    @Override
    public void onCreate() {
        super.onCreate();

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mDatabase =  FirebaseDatabase.getInstance().getReference("user");
        sessionManager = new SessionManager(getApplicationContext());
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notification channel --> foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("location_service", "Location Service", NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            mNotificationManager.createNotificationChannel(channel);
        }

        // create notification
        mNotification = new NotificationCompat.Builder(this, "location_service")
                .setContentTitle("Hey! ")
                .setContentText("Tracking your location...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        getLocationUpdates();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(1, mNotification);
        sms();
        return START_STICKY;

        /**
        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForeground(1, mNotification);
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForeground(true);
                    stopSelf();
                    break;
            }
        }

        return START_STICKY;
         **/
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        try{
            mDatabase.child(sessionManager.getValue("key_session_id")).child("realtimeLocation").setValue(location);
        }catch (Exception e){

        }

       // mNotification.setContentText = "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude();
        mNotificationManager.notify(1, mNotification);
    }
    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    public void sms(){

            handler = new Handler();        // Create a handler and a runnable to send SMS messages periodically
            smsRunnable = new Runnable() {
                @Override
                public void run() {
                    try{
                        if(sessionManager.getValue("key_is_internet_on").equals("false")) {
                            sendSMS();
                        }
                    }catch (Exception e){

                    }
                    handler.postDelayed(this, 4000);        // Send message after 4 seconds
                }
            };
            handler.postDelayed(smsRunnable, 4000);

    }
    private void sendSMS() {
        System.out.println("messages : "+ i++);
        SMSthread smsThread = new SMSthread(sessionManager.getValue("key_eContact"), "Hello, My last location is "+"https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude);
        smsThread.run();
    }


    private void getLocationUpdates() {
        if(mLocationManager!=null){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                } else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
                } else {
                    Toast.makeText(this, "Please Enable the GPS", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the message sending when the service is destroyed
        handler.removeCallbacks(smsRunnable);
        stopForegroundService();
    }

    private void stopForegroundService() {
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
    }


}