package com.aibrains.emergency.Family;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aibrains.emergency.LostPhoneService.AddLostPhone;
import com.aibrains.emergency.LostPhoneService.CheckActivity;
import com.aibrains.emergency.LostPhoneService.LostPhone;
import com.aibrains.emergency.LostPhoneService.SearchPhone;
import com.aibrains.emergency.R;

public class Family extends AppCompatActivity {
    CardView addiot, addMember , check ;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_family);

        addiot = findViewById(R.id.f_iot);
        addMember = findViewById(R.id.f_member);
        check = findViewById(R.id.f_check);

        addiot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Family.this, AddiotDevice.class);
                startActivity(intent);
            }
        });
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Family.this, AddToFamily.class);
                startActivity(intent);
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Family.this, FamilyMembers.class);
                startActivity(intent);
            }
        });
    }
}