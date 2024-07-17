package com.aibrains.emergency;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.aibrains.emergency.databinding.AdminHomeBinding;
import com.aibrains.emergency.models.AllRequestList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminMapView extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private AdminHomeBinding binding;
    private LocationManager manager;
    private TextView speed , longitude , latitude , fullName , VictimName , VictimDistance;
    private ImageButton  callToVictim ;
    private Button startAction ;
    private int MIN_TIME = 1000;
    private int MIN_DISTANCE = 0 ;
    LatLng sydney = new LatLng(1,1);
    Functions var = new Functions();
    Intent intent;
    Marker marker;
    static int image;
    static String userID ="123" ;// Login.getUserID();
    static String key ;
    private String victimPhoneNumber , victimUserID;
    MyLocation location;
    private final HashMap<String, Marker> hm=new HashMap<String, Marker>();

    //code for dialog
    Dialog dialog;
    //end code for dialog

    DatabaseReference user = FirebaseDatabase.getInstance().getReference("user/"+userID);
    DatabaseReference users = FirebaseDatabase.getInstance().getReference("user");
    DatabaseReference emergencyRequest = FirebaseDatabase.getInstance().getReference("emReq");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding = AdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        speed = findViewById(R.id.speed);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        fullName = findViewById(R.id.fullName);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.adminMap);
        mapFragment.getMapAsync(this);

        //code for dialog
        dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.custom_marker_window_admin,null);
        VictimName = view.findViewById(R.id.cVictimName);
        VictimDistance = view.findViewById(R.id.cVictimDistance);
        callToVictim = view.findViewById(R.id.callToVictim);
        startAction = view.findViewById(R.id.startAction);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dialog.getWindow().setBackgroundBlurRadius(20);
        }
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        //end code for dialog

        callToVictim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AdminMapView.this, victimPhoneNumber,Toast.LENGTH_SHORT).show();
            }
        });
        startAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emergencyRequest.child(victimUserID).child("status").setValue("On Action");
                emergencyRequest.child(victimUserID).child("action_by").setValue(userID);
               // emergencyRequest.child(victimUserID).child("status").setValue("On Action");
                Toast.makeText(AdminMapView.this, "Tracking Started",Toast.LENGTH_SHORT).show();
                dialog.hide();
            }
        });


        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
               // fullName.setText(snapshot.child("fullname").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(8);
        showAllOnActionVictim();
        showAllPendingVictim();
        mMap.getUiSettings().setAllGesturesEnabled(true);
        LatLng bd = new LatLng(23.6850, 90.3563);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bd));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(AdminMapView.this, (CharSequence) marker.getTag(),Toast.LENGTH_SHORT).show();
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                users.child(marker.getTitle()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        VictimName.setText(snapshot.child("personal_info").child("fullname").getValue().toString());
                        victimPhoneNumber = snapshot.child("personal_info").child("phone").getValue().toString();
                        victimUserID = snapshot.child("nid").getValue().toString();
                        double endlat = (double) snapshot.child("realtimeLocation").child("latitude").getValue();
                        double endlong = (double) snapshot.child("realtimeLocation").child("longitude").getValue();
                        /**
                        LatLng user = new LatLng(endlat, endlong);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
                        **/
                        double distance = var.getDistance(var.lat, var.longs, endlat ,endlong );
                        VictimDistance.setText(String.format("%.2f",distance)+" KM Away");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
                dialog.show();
                return false;
            }
        });

        // Add a marker in Sydney and move the camera

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            enableUserLocation();
            if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            }else if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER ,MIN_TIME,MIN_DISTANCE,this);
            }else {
                Toast.makeText(this,"No Provider Enable",Toast.LENGTH_SHORT).show();
            }
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
        }

        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));


    }


    private void showRouteOnMap(LatLng startPoint, LatLng endPoint) {
        if (mMap == null) {

            return;
        }

        // Add markers for start and end points
        mMap.addMarker(new MarkerOptions().position(startPoint).title("Start Point"));
        mMap.addMarker(new MarkerOptions().position(endPoint).title("End Point"));

        // Create a PolylineOptions to draw the route
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .color(android.R.color.holo_blue_dark) // Set the color of the route line
                .width(10); // Set the width of the route line

        // Add the Polyline to the map
        Polyline polyline = mMap.addPolyline(polylineOptions);

        // Move the camera to show both markers and the route line
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(startPoint);
        builder.include(endPoint);
        LatLngBounds bounds = builder.build();
        int padding = 100; // Padding around the route line
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                enableUserLocation();

            }else {
                Toast.makeText(this,"No Provider Enable",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void enableUserLocation(){
        mMap.setMyLocationEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onLocationChanged(Location location) {
        LatLng myLocation = new LatLng(location.getLatitude(),location.getLongitude());
        var.lat = location.getLatitude();
        var.longs = location.getLongitude();

    }


    public void showAllOnActionVictim(){
        emergencyRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AllRequestList list = dataSnapshot.getValue(AllRequestList.class);
                    String id = list.uniqueId;
                    String uid = list.userId;
                    String status = list.status;
                    if (dataSnapshot.child("status").getValue().equals("complete")) {
                    } else {
                        users.child(String.valueOf(uid)).child("realtimeLocation").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                location = snapshot.getValue(MyLocation.class);
                                if (location != null) {
                                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    if (hm.containsKey(uid)) {
                                        hm.get(uid).remove();
                                    }
                                    marker = mMap.addMarker(new MarkerOptions().position(userLocation).title(uid)
                                            .icon(userIcon(AdminMapView.this, R.drawable.red)));
                                    marker.setTag(String.valueOf(uid));
                                    marker.hideInfoWindow();
                                    hm.put(uid, marker);

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
    public void showAllPendingVictim(){
        emergencyRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AllRequestList list = dataSnapshot.getValue(AllRequestList.class);
                    String id = list.uniqueId;
                    String uid = list.userId;
                    String status = list.status;
                    if (dataSnapshot.child("status").getValue().equals("complete")) {
                    } else if(dataSnapshot.child("status").getValue().equals("On Action")){
                        users.child(String.valueOf(uid)).child("realtimeLocation").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                location = snapshot.getValue(MyLocation.class);
                                if (location != null) {
                                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    if (hm.containsKey(uid)) {
                                        hm.get(uid).remove();
                                    }
                                    marker = mMap.addMarker(new MarkerOptions().position(userLocation).title(uid)
                                            .icon(userIcon(AdminMapView.this, R.drawable.orange)));
                                    marker.setTag(String.valueOf(uid));
                                    marker.hideInfoWindow();
                                    hm.put(uid, marker);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
    private BitmapDescriptor userIcon(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, vectorResId);
        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    static String getUserID(){
        return userID;
    }
    static String getKey(){
        return key;
    }

}