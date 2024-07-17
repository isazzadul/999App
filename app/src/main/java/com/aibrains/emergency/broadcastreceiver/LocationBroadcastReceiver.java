package com.aibrains.emergency.broadcastreceiver;

import static android.content.Context.LOCATION_SERVICE;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import com.aibrains.emergency.UserHome;

public class LocationBroadcastReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                UserHome.getInstance().setGpsWarning(0);
            } else {
                UserHome.getInstance().setGpsWarning(1);
            }
        }catch (Exception ex){
        }
    }
}
