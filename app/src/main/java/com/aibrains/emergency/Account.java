package com.aibrains.emergency;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

public class Account extends AppCompatActivity {

    private Button editProfile, settings , about , logout;
    private TextView name , type ;
    SessionManager sessionManager;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_account);

        editProfile = findViewById(R.id.editProfile);
        settings = findViewById(R.id.settings);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);
        name = findViewById(R.id.aboutPageUserName);
        type = findViewById(R.id.aboutPageUserType);

        sessionManager = new SessionManager(getApplicationContext());

        name.setText(sessionManager.getValue("key_name"));
        type.setText(sessionManager.getValue("key_session_type"));

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Account.this,UnderConstructionPage.class);
                startActivity(intent);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Account.this,UnderConstructionPage.class);
                startActivity(intent);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Account.this,UnderConstructionPage.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.clearSession();
                FirebaseMessaging.getInstance().unsubscribeFromTopic("user");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("admins");
                Intent accountPage = new Intent(Account.this,Login.class);
                startActivity(accountPage);
                finish();
            }
        });
    }
}