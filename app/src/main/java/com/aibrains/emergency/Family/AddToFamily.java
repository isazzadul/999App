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

public class AddToFamily extends AppCompatActivity {

    private EditText uid  ;
    private Button submit ;

    DatabaseReference addToFamily = FirebaseDatabase.getInstance().getReference("family/members");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_add_to_family);

        uid = findViewById(R.id.af_userid);
        submit = findViewById(R.id.af_submit);

        SessionManager sessionManager = new SessionManager(getApplicationContext());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uid.getText().toString().isEmpty()){
                    Toast.makeText(AddToFamily.this,"Please Fill Everything",Toast.LENGTH_SHORT).show();
                }else {
                    String key1 = addToFamily.push().getKey();
                    addToFamily.child(key1).child("addedBy").setValue(sessionManager.getValue("key_session_id"));
                    addToFamily.child(key1).child("UserID").setValue(uid.getText().toString());
                    Toast.makeText(AddToFamily.this,"Submit Successfully",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}