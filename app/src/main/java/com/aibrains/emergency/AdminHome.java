


package com.aibrains.emergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aibrains.emergency.adapters.ArticleAdapter;
import com.aibrains.emergency.adapters.CustomAdsAdapter;
import com.aibrains.emergency.models.Article;
import com.aibrains.emergency.models.CustomAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminHome extends AppCompatActivity {

    private ImageButton emergency , ambulance , fireService , mapView , allRequest , profile ;
    private TextView adminFullName;
    String userID;

    ArrayList<Article> articles = new ArrayList<>();
    ArrayList<CustomAds> customAds = new ArrayList<>();
    DatabaseReference articlesdb = FirebaseDatabase.getInstance().getReference("extras/articles");
    DatabaseReference adsdb = FirebaseDatabase.getInstance().getReference("extras/ads");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_admin_home);

        emergency = findViewById(R.id.adminEmergencyButton);
        ambulance = findViewById(R.id.adminAmbulanceButton);
        fireService = findViewById(R.id.adminFireServiceButton);
        mapView = findViewById(R.id.adminMapView);
        allRequest = findViewById(R.id.adminAllList);
        profile = findViewById(R.id.adminProfileButton);
        adminFullName = findViewById(R.id.adminFullName);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        userID = sessionManager.getValue("key_session_id");
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("user/"+userID);

        try {
            user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    adminFullName.setText(snapshot.child("personal_info").child("fullname").getValue().toString());
                    sessionManager.userData(snapshot.child("personal_info").child("fullname").getValue().toString(),
                            snapshot.child("email").getValue().toString(),
                            snapshot.child("personal_info").child("e_contact").getValue().toString(),
                            snapshot.child("personal_info").child("f_contact").getValue().toString(),
                            snapshot.child("personal_info").child("m_contact").getValue().toString(),
                            snapshot.child("personal_info").child("gender").getValue().toString(),
                            snapshot.child("personal_info").child("phone").getValue().toString());

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }catch (DatabaseException databaseException){

        }


        // ************** Ads and Article Panel
        RecyclerView article = findViewById(R.id.adminArticle);
        article.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        ArticleAdapter articleAdapter = new ArticleAdapter(this,articles);
        article.setAdapter(articleAdapter);

        RecyclerView ads = findViewById(R.id.adminAds);
        ads.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        CustomAdsAdapter customAdsAdapter = new CustomAdsAdapter(this,customAds);
        ads.setAdapter(customAdsAdapter);
        // startService(new Intent(AdminHome.this, LocationService.class));
        adsdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CustomAds customAds1 = dataSnapshot.getValue(CustomAds.class);
                    customAds.add(customAds1);
                }
                customAdsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        articlesdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Article article = dataSnapshot.getValue(Article.class);
                    articles.add(article);
                }
                articleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        // ************** Ads and Article Panel

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent accountPage = new Intent(AdminHome.this,Account.class);
                startActivity(accountPage);
            }
        });



        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent map = new Intent(AdminHome.this, AdminMapView.class);
                startActivity(map);
            }
        });
        allRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHome.this, AllRequest.class);
                startActivity(intent);
            }
        });


    }
}