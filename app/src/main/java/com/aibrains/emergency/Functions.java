package com.aibrains.emergency;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.Executor;

public class Functions {
      String test;
      static String fcmToken;
        //for distance calculate
      double lat1, lat2 , long1, long2 ;
        //for distance calculate Admin
      double lat, longs ;
        //for UserMapView
      double userLat , userLong , policeLat , policeLong ;
        //for key
      String key;
        //for user map camera update
      int isPoliceAccept ;
      public static boolean isGpsEnable = false ;


      //phone list
      public static String getID , imei;

      // family list
    public static String addedBy , memberUserID , name;


      int img;

    Marker marker;

    public Functions() {
    }

    public Functions(String test) {
        this.test = test;
    }

    public double getDistance(double startLat, double startLong, double endLat, double endLong){
        Location start = new Location("Distance");
        Location end = new Location("User");
        start.setLatitude(startLat);
        start.setLongitude(startLong);
        end.setLatitude(endLat);
        end.setLongitude(endLong);
        double distance = start.distanceTo(end)/1000;
        return distance;
    }

    public static String getFcmKey(String userid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
        databaseReference.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
               fcmToken = snapshot.child("fcmToken").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        return fcmToken ;
    }


    public static void buttonSwitchGPS_ON(Activity activity){

        LocationRequest mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder();
        locationSettingsRequestBuilder.addLocationRequest(mLocationRequest);
        locationSettingsRequestBuilder.setAlwaysShow(true);
        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build());
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // textView.setText("Location settings (GPS) is ON.");
            }
        });
        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure( Exception e) {
                // textView.setText("Location settings (GPS) is OFF.");

                if (e instanceof ResolvableApiException){
                    try {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        resolvableApiException.startResolutionForResult(activity,0x1);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });
    }





}
