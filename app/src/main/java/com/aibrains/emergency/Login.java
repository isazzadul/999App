package com.aibrains.emergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class Login extends AppCompatActivity {

    private Button register, forget_password;
    private EditText login_nid, login_password;
    private AppCompatButton login;
    static String userID, activeToken;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
    DatabaseReference subscriptions = FirebaseDatabase.getInstance().getReference("subscription_system");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //code for hide action bar & title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.login);

        SessionManager sessionManager = new SessionManager(getApplicationContext());

        register = (Button) findViewById(R.id.register);
        login_nid = (EditText) findViewById(R.id.login_nid);
        login_password = (EditText) findViewById(R.id.login_password);
        forget_password = (Button) findViewById(R.id.forget_password);
        login = (AppCompatButton) findViewById(R.id.login);
        sessionManager.isOnEmergency("false");



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(Login.this,SignUp.class);
                startActivity(signup);
            }
        });
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nidtxt = login_nid.getText().toString();
                final String pass = login_password.getText().toString();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.hasChild(nidtxt)){
                            if(pass.equals(snapshot.child(nidtxt).child("password").getValue())){
                                Toast.makeText(Login.this,"Login Success",Toast.LENGTH_SHORT).show();
                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(Task<String> task) {
                                                if (!task.isSuccessful()) {
                                                    return;
                                                }
                                                String token = task.getResult();
                                                databaseReference.child(nidtxt).child("fcmToken").setValue(token);

                                            }
                                        });
                                if(snapshot.child(nidtxt).child("userType").getValue().equals("admin")) {
                                    FirebaseMessaging.getInstance().subscribeToTopic("admins");
                                    sessionManager.createSession(nidtxt,pass,"admin");
                                    Toast.makeText(Login.this,"Admin Login Success",Toast.LENGTH_SHORT).show();
                                    Intent adminHome = new Intent(Login.this, AdminHome.class);
                                    startActivity(adminHome);
                                    finish();

                                }else{
                                    userID = nidtxt;
                                    FirebaseMessaging.getInstance().subscribeToTopic("user");
                                    sessionManager.createSession(nidtxt,pass,"user");

                                    /** Add to shared preference **/

                                    subscriptions.child("users").child(userID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            sessionManager.subscriptionDetails(
                                                    snapshot.child("subscriptions_type").getValue().toString(),
                                                    snapshot.child("start_date").getValue().toString(),
                                                    snapshot.child("end_date").getValue().toString()
                                            );
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError error) {}
                                    });

                                    Intent map = new Intent(Login.this, UserHome.class);
                                    startActivity(map);
                                    finish();
                                }

                            }else {
                                Toast.makeText(Login.this,"Wrong Password, Please try again",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(Login.this,"No User Found",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
            }
        });

    }
    public static String getUserID(){
        return userID;
    }
    public static String getActiveToken(){
        return userID;
    }
}