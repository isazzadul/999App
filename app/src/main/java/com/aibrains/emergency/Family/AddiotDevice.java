package com.aibrains.emergency.Family;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aibrains.emergency.R;
import com.aibrains.emergency.SessionManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddiotDevice extends AppCompatActivity {
    private EditText sn , uid , id , name , gender , phone ;
    private Button submit ;
    DatabaseReference iotDevice = FirebaseDatabase.getInstance().getReference("family/iot/devices");
    DatabaseReference addToFamily = FirebaseDatabase.getInstance().getReference("family/members");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_add_device);

        name = findViewById(R.id.ad_name);
        phone = findViewById(R.id.ad_number);
        sn = findViewById(R.id.ad_sn);
        uid = findViewById(R.id.ad_userid);
        id = findViewById(R.id.ad_id);
        gender = findViewById(R.id.ad_gender);
        submit = findViewById(R.id.ad_submit);

        SessionManager sessionManager = new SessionManager(getApplicationContext());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty() || phone.getText().toString().isEmpty() ||
                        sn.getText().toString().isEmpty() ||uid.getText().toString().isEmpty() ||
                        gender.getText().toString().isEmpty() ||id.getText().toString().isEmpty()){
                    Toast.makeText(AddiotDevice.this,"Please Fill Everything",Toast.LENGTH_SHORT).show();
                }else {

                    String key1 = addToFamily.push().getKey();
                    iotDevice.child(sn.getText().toString()).child("sn").setValue(sn.getText().toString());
                    iotDevice.child(sn.getText().toString()).child("addedBy").setValue(sessionManager.getValue("key_session_id"));
                    iotDevice.child(sn.getText().toString()).child("name").setValue(name.getText().toString());
                    iotDevice.child(sn.getText().toString()).child("phone").setValue(phone.getText().toString());
                    iotDevice.child(sn.getText().toString()).child("userID").setValue(id.getText().toString());
                    iotDevice.child(sn.getText().toString()).child("gender").setValue(gender.getText().toString());

                    addToFamily.child(key1).child("addedBy").setValue(sessionManager.getValue("key_session_id"));
                    addToFamily.child(key1).child("UserID").setValue(id.getText().toString());
                    Toast.makeText(AddiotDevice.this,"Submit Successfully",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}