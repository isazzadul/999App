package com.aibrains.emergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignUp extends AppCompatActivity {
    private LinearLayout profile;
    private EditText name;
    private EditText phone;
    private EditText email;
    private TextView textView4;
    private RadioGroup gender;
    private RadioButton radioButton;
    private EditText mnum;
    private EditText fnum;
    private EditText emnum;
    private AppCompatButton signup;
    private LinearLayout first;
    private EditText id;
    private EditText pass;
    private AppCompatButton next;
    private ImageButton goNext;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
    DatabaseReference subscriptiondb = FirebaseDatabase.getInstance().getReference("subscription_system/users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.signup);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        profile = (LinearLayout) findViewById(R.id.profile);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        textView4 = (TextView) findViewById(R.id.textView4);
        gender = (RadioGroup) findViewById(R.id.gender);
        int RadioId = gender.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(RadioId);
        mnum = (EditText) findViewById(R.id.mnum);
        fnum = (EditText) findViewById(R.id.fnum);
        emnum = (EditText) findViewById(R.id.emnum);
        signup = (AppCompatButton) findViewById(R.id.signup);
        first = (LinearLayout) findViewById(R.id.first);
        id = (EditText) findViewById(R.id.id);
        pass = (EditText) findViewById(R.id.pass);
        next = (AppCompatButton) findViewById(R.id.next);
        goNext = (ImageButton) findViewById(R.id.goNext);



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                first.setVisibility(View.GONE);
                profile.setVisibility(View.VISIBLE);
                profile.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
                }
        });
        goNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile.setVisibility(View.GONE);
                first.setVisibility(View.VISIBLE);
                first.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
            }
        });



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int RadioId = gender.getCheckedRadioButtonId();
                radioButton = findViewById(RadioId);
                final String nametxt = name.getText().toString();
                final String nidtxt = id.getText().toString();
                final String phonetxt = phone.getText().toString();
                final String emailtxt = email.getText().toString();
                final String passwordtxt = pass.getText().toString();
                final String f_contact = fnum.getText().toString();
                final String m_contact = mnum.getText().toString();
                final String e_contact = emnum.getText().toString();



                if(nametxt.isEmpty() || nidtxt.isEmpty() || phonetxt.isEmpty() || emailtxt.isEmpty() || passwordtxt.isEmpty()
                        || f_contact.isEmpty() || m_contact.isEmpty() || e_contact.isEmpty()){
                    Toast.makeText(SignUp.this,"Please Fill Everything",Toast.LENGTH_SHORT).show();
                }else {
                    // added to sub database
                    subscriptiondb.child(nidtxt).child("subscriptions_type").setValue("free");
                    subscriptiondb.child(nidtxt).child("start_date").setValue("01/01/2023");
                    subscriptiondb.child(nidtxt).child("end_date").setValue("01/01/3031");

                    // added to user database
                    databaseReference.child(nidtxt).child("nid").setValue(nidtxt);
                    databaseReference.child(nidtxt).child("password").setValue(passwordtxt);
                    databaseReference.child(nidtxt).child("email").setValue(emailtxt);
                    databaseReference.child(nidtxt).child("userType").setValue("user");
                    databaseReference.child(nidtxt).child("status").setValue("offline");
                    databaseReference.child(nidtxt).child("activeReqToken").setValue("null");

                    databaseReference.child(nidtxt).child("personal_info").child("fullname").setValue(nametxt);
                    databaseReference.child(nidtxt).child("personal_info").child("phone").setValue(phonetxt);
                    databaseReference.child(nidtxt).child("personal_info").child("gender").setValue(radioButton.getText());
                    databaseReference.child(nidtxt).child("personal_info").child("f_contact").setValue(f_contact);
                    databaseReference.child(nidtxt).child("personal_info").child("m_contact").setValue(m_contact);
                    databaseReference.child(nidtxt).child("personal_info").child("e_contact").setValue(e_contact);
                    finish();
                }
            }
        });

    }
}