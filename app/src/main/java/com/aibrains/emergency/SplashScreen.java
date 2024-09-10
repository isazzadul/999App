package com.aibrains.emergency;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.aibrains.emergency.LostPhoneService.AddLostPhone;
import com.aibrains.emergency.LostPhoneService.CheckActivity;
import com.aibrains.emergency.LostPhoneService.SearchPhone;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //code for hide action bar & title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.splash_screen);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        boolean sessionCheck = sessionManager.checkSession();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent home;
                if(sessionCheck == true){
                    if(sessionManager.getValue("key_session_type").equals("user")) {
                        home = new Intent(SplashScreen.this, UserHome.class);
                    }else {
                        home = new Intent(SplashScreen.this, AdminHome.class);
                    }
                }else{
                    home = new Intent(SplashScreen.this, Login.class);
                }
                //home = new Intent(SplashScreen.this, CheckActivity.class);
                startActivity(home);
                finish();
            }
        },2000);
    }
}