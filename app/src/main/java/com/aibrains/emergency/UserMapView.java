package com.aibrains.emergency;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aibrains.emergency.models.AllRequestList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.aibrains.emergency.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class UserMapView extends FragmentActivity implements OnMapReadyCallback, LocationListener {


    private GoogleMap mMap;
    private TextView res , distances , teamName, helperName , helperDistance;
    private  View team , searching , contactLayout;
    private ActivityMapsBinding binding;
    private LocationManager manager;
    private int MIN_TIME = 1000;
    private int MIN_DISTANCE = 0 ;
    private String response = "demo";
    private String uid , helperId , helperNumber , fcmToken , helperID ;
    Marker userMarker, teamMarker, marker ;
    private LatLng userLocation, teamLocation ;
    private Button safeButton, askHelpButton, call999;
    private ImageButton HelperCallButton, HelperTxtButton, callActionButton , callF , callM , callEm;
    int i = 0;
    MyLocation location;
    private final HashMap<String, Marker> hm=new HashMap<String, Marker>();

    //code for dialog
    Dialog dialog;
    //end code for dialog

    String userID, key, policeID ;
    DatabaseReference UserMap,requests;
    SessionManager sessionManager;


    DatabaseReference users = FirebaseDatabase.getInstance().getReference("user");
    Functions var = new Functions();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        sessionManager = new SessionManager(getApplicationContext());
        //need data to perform activity
        userID = sessionManager.getValue("key_session_id");
        key = sessionManager.getValue("key_session_id");
        UserMap = FirebaseDatabase.getInstance().getReference("user/"+sessionManager.getValue("key_session_id"));
        requests = FirebaseDatabase.getInstance().getReference("emReq/"+key);

        Intent intent = new Intent(UserMapView.this, LocationService.class);
        intent.setAction(LocationService.ACTION_START_FOREGROUND_SERVICE);
        startService(intent);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
       // res = findViewById(R.id.textView5);
        distances = findViewById(R.id.distance);
        safeButton = findViewById(R.id.safe);
        call999 = findViewById(R.id.call999);
        callF = findViewById(R.id.father_num);
        callM = findViewById(R.id.mother_num);
        callEm = findViewById(R.id.emergency_contact);
        teamName = findViewById(R.id.TeamName);
        team = findViewById(R.id.team);
        searching = findViewById(R.id.searching);
        contactLayout = findViewById(R.id.contactLayout);
        callActionButton = findViewById(R.id.callActionButton);
        var.isPoliceAccept = 0;

        mapFragment.getMapAsync(this);
        getLocationUpdates();

        //************ code for dialog ***********
        dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.helper_dialog_usermapview,null);
        helperName = view.findViewById(R.id.helperName);
        helperDistance = view.findViewById(R.id.helperDistance);
        askHelpButton = view.findViewById(R.id.askHelpButton);
        HelperCallButton = view.findViewById(R.id.HelperCallButton);
        HelperTxtButton = view.findViewById(R.id.HelperTxtButton);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dialog.getWindow().setBackgroundBlurRadius(20);
        }
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        //************* end code for dialog **************

        showAll();
        askHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(Functions.getFcmKey("102"),"I'm on danger","Please Help me",
                                getApplicationContext(),UserMapView.this);
                        fcmNotificationsSender.SendNotifications();

            }
        });


        requests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child("action_by").getValue().equals("none")){
                    var.isPoliceAccept = 0;
                }else {
                    policeID = snapshot.child("action_by").getValue().toString();
                    DatabaseReference policeMap = FirebaseDatabase.getInstance().getReference("user/"+policeID);
                    readPoliceLocationChanges(policeMap);
                    var.isPoliceAccept = 1;
                    searching.setVisibility(View.GONE);
                    team.setVisibility(View.VISIBLE);
                    team.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        readUserLocationChanges(UserMap);
        Intent from = getIntent();



        safeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requests.child("status").setValue("complete");
                users.child(userID).child("activeReqToken").setValue("null");
                sessionManager.isOnEmergency("false");
                /**
                Intent intent = new Intent(UserMapView.this, LocationService.class);
                intent.setAction(LocationService.ACTION_STOP_FOREGROUND_SERVICE);
                startService(intent);
                 **/

                Intent serviceIntent = new Intent(UserMapView.this, LocationService.class);
                stopService(serviceIntent);

                Intent home = new Intent(UserMapView.this, UserHome.class);
                startActivity(home);
                finish();
            }
        });
        call999.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:999"));
                startActivity(intent);
            }
        });
        callF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+sessionManager.getValue("key_fContact")));
                startActivity(intent);
            }
        });
        callM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+sessionManager.getValue("key_mContact")));
                startActivity(intent);
            }
        });
        callEm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+sessionManager.getValue("key_eContact")));
                startActivity(intent);
            }
        });
        callActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contactLayout.isShown() == false){
                    contactLayout.setVisibility(View.VISIBLE);
                }else if(contactLayout.isShown() == true){
                    contactLayout.setVisibility(View.GONE);
                }
            }
        });




    }

    private void readPoliceLocationChanges(DatabaseReference reference) {
        reference.child("realtimeLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    MyLocation location = snapshot.getValue(MyLocation.class);

                    if (location != null){
                        teamLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        var.lat1 = location.getLatitude();
                        var.long1 = location.getLongitude();

                        Location dist = new Location("Distance");
                        Location location1 = new Location("User");
                        dist.setLatitude(var.lat1);
                        dist.setLongitude(var.long1);
                        location1.setLatitude(var.lat2);
                        location1.setLongitude(var.long2);
                        double distance = location1.distanceTo(dist)/1000;
                        distances.setText(String.format("%.2f",distance)+" KM Away");
                        teamMarker.setPosition(teamLocation);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(teamLocation));
                    }
                }catch (Exception e){
                    Toast.makeText(UserMapView.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void readUserLocationChanges(DatabaseReference reference) {
        reference.child("realtimeLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                try {
                    MyLocation location = snapshot.getValue(MyLocation.class);

                    if (location != null){
                        var.lat2 = location.getLatitude();
                        var.long2 = location.getLongitude();
                        userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        userMarker.setPosition(userLocation);
                        if(i<=1) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                        }
                        i++;
                    }
                }catch (Exception e){
                    Toast.makeText(UserMapView.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void getLocationUpdates() {
        if(manager!=null){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            }else if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER ,MIN_TIME,MIN_DISTANCE,this);
            }else {
                Toast.makeText(this,"No Provider Enable",Toast.LENGTH_SHORT).show();
            }
        }else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLocationUpdates();
                enableUserLocation();
            }else {
                Toast.makeText(this,"No Provider Enable",Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(var.lat2, var.long2);
        userMarker = mMap.addMarker(new MarkerOptions().position(sydney).title(userID)
        .icon(userIcon(UserMapView.this,R.drawable.red)));
        teamMarker = mMap.addMarker(new MarkerOptions().position(sydney).title(userID)
                .icon(userIcon(UserMapView.this,R.drawable.green)));
         mMap.setMinZoomPreference(10);

        mMap.getUiSettings().setAllGesturesEnabled(true);
        enableUserLocation();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                helperID = marker.getTitle();

                if(String.valueOf(marker.getTitle()).equals(userID)){
                }else {
                    users.child(Objects.requireNonNull(marker.getTitle())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            helperName.setText(snapshot.child("personal_info").child("fullname").getValue().toString());
                            helperNumber = snapshot.child("personal_info").child("phone").getValue().toString();
                            double endlat = (double) snapshot.child("realtimeLocation").child("latitude").getValue();
                            double endlong = (double) snapshot.child("realtimeLocation").child("longitude").getValue();
                            double distance = var.getDistance(var.lat, var.longs, endlat, endlong);
                            helperDistance.setText(String.format("%.2f", distance) + " KM Away");
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                    dialog.show();
                }
                return false;
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void enableUserLocation(){
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            saveLocation(location);
            var.lat = location.getLatitude();
            var.longs = location.getLongitude();
        }
    }

    private void saveLocation(Location location) {
        UserMap.child("realtimeLocation").setValue(location);
       // demo.child(functions.key).child("realtimeLocation").setValue(location);
        //
      //  policeMap.setValue(location);
    }


    public void showAll(){
        Query query = (users.orderByChild("userType").equalTo("admin"));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(!dataSnapshot.getKey().equals(userID)){
                    uid = dataSnapshot.getKey();
                    // helperId = dataSnapshot.child("userId").getValue().toString();
                    //Toast.makeText(UserMapView.this,helperId,Toast.LENGTH_SHORT).show();
                    location = dataSnapshot.child("realtimeLocation").getValue(MyLocation.class);
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (hm.containsKey(uid)) {
                            hm.get(uid).remove();
                        }
                        marker = mMap.addMarker(new MarkerOptions().position(userLocation).title(uid)
                                .icon(userIcon(UserMapView.this, R.drawable.green)));
                        marker.hideInfoWindow();
                        hm.put(uid, marker);

                    }

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

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(UserMapView.this, UserMainActivity.class));
//        this.finish();
//    }





}