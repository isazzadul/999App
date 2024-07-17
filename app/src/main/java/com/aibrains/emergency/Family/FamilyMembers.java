package com.aibrains.emergency.Family;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.aibrains.emergency.Functions;
import com.aibrains.emergency.LostPhoneService.LostPhoneActivityAdapter;
import com.aibrains.emergency.LostPhoneService.PhoneListAdapter;
import com.aibrains.emergency.R;
import com.aibrains.emergency.SessionManager;
import com.aibrains.emergency.models.FamilyList;
import com.aibrains.emergency.models.LostPhoneActivity;
import com.aibrains.emergency.models.PhoneList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FamilyMembers extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private MapView mapView;
    GoogleMap googleMap ;

    static RecyclerView recyclerView ;
    Button show , found ;
    ArrayList<FamilyList> arrayList = new ArrayList<>();

    static String  name , addedBy , userID ;

    DatabaseReference familyMembers = FirebaseDatabase.getInstance().getReference("family/members");
    DatabaseReference user = FirebaseDatabase.getInstance().getReference("user");
    static FamilyListAdapter familyListAdapter ;

    public FamilyMembers() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_family_members);

        checkAndRequestPermissions();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference location = firebaseDatabase.getReference("user/101/realtimeLocation");

        // Initialize MapView
        mapView = findViewById(R.id.f_mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        SessionManager sessionManager = new SessionManager(getApplicationContext());

        show = findViewById(R.id.f_show);
        // found = findViewById(R.id.ac_markAsFound);

        recyclerView = findViewById(R.id.familyMemberList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        familyListAdapter = new FamilyListAdapter(this,arrayList);
        recyclerView.setAdapter(familyListAdapter);
        familyMembers.addValueEventListener(new ValueEventListener() {
            FamilyList familyList = new FamilyList();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.child("addedBy").getValue().toString().equals(sessionManager.getValue("key_session_id"))) {
                        addedBy = dataSnapshot.child("addedBy").getValue().toString();
                        userID = dataSnapshot.child("UserID").getValue().toString();
                        user.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Functions.name = snapshot.child("personal_info").child("fullname").getValue().toString();
                                familyList = new FamilyList(addedBy,userID,Functions.name);
                                arrayList.add(familyList);
                                familyListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Functions.memberUserID!=null) {
                    showLocation(Functions.memberUserID,Functions.name);
                }
            }
        });




    }
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

    }
    void showLocation(String id,String name){
        user.child(id).child("realtimeLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double latitude = snapshot.child("latitude").getValue(Double.class);
                Double longitude = snapshot.child("longitude").getValue(Double.class);

                if (latitude != null && longitude != null) {
                    LatLng busLocation = new LatLng(latitude, longitude);

                    googleMap.clear();

                    // Create a MarkerOptions with a custom bus marker image
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(busLocation)
                            .title(name)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.green));

                    // Rotate the marker based on the direction
                    //markerOptions.rotation(direction);

                    // Add marker to the map
                    googleMap.addMarker(markerOptions);

                    // Move camera to the bus location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(busLocation));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // Permission is granted, proceed with the app
                // You can initialize the map or perform other tasks here
            }
        } else {
            // For devices below Marshmallow, permissions are granted at installation time
            // Proceed with the app
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the app
                // You can initialize the map or perform other tasks here
            } else {
                // Permission denied, handle accordingly
                // You might want to show a message or disable features that require this permission
            }
        }
    }
    public void trigger(){
        if(Functions.memberUserID!=null) {
            showLocation(Functions.memberUserID,Functions.name);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}