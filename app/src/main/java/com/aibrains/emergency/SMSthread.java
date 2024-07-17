package com.aibrains.emergency;

import android.telephony.SmsManager;

public class SMSthread implements Runnable {
    private String phoneNumber;
    private String message;

    public SMSthread(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
