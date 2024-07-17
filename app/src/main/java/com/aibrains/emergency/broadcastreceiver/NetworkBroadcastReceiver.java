package com.aibrains.emergency.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.aibrains.emergency.SMSthread;
import com.aibrains.emergency.SessionManager;
import com.aibrains.emergency.UserHome;

import android.os.Handler;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    private Handler handler;
    private Runnable smsRunnable;
    int i = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        SessionManager sessionManager = new SessionManager(context.getApplicationContext());
        // Check network connectivity status
        if (isNetworkConnected(context)) {
            UserHome.getInstance().setNetWarning(0);
            sessionManager.isOnInternet("true");
/**
            handler = new Handler();        // Create a handler and a runnable to send SMS messages periodically
            smsRunnable = new Runnable() {
                @Override
                public void run() {
                    sendSMS();

                    handler.postDelayed(this, 4000);        // Send message after 4 seconds
                }
            };
            handler.postDelayed(smsRunnable, 4000);
//            if(sessionManager.getValue("key_is_on_emergency").equals("true")){
            // sms runner
            // }
 **/
        } else {
            UserHome.getInstance().setNetWarning(1);
            sessionManager.isOnInternet("false");
            /**
            try{
                handler.removeCallbacks(smsRunnable);
            }catch (Exception e){

            }
             **/

            //handler.removeCallbacks(smsRunnable);
        }
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    private void sendSMS() {
        System.out.println("messages : "+ i++);
        SMSthread smsThread = new SMSthread("12356", "message");
        smsThread.run();
    }
}
