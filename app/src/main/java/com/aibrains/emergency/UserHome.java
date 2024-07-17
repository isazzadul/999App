package com.aibrains.emergency;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.aibrains.emergency.Family.Family;
import com.aibrains.emergency.Family.FamilyMembers;
import com.aibrains.emergency.LostPhoneService.LostPhone;
import com.aibrains.emergency.broadcastreceiver.LocationBroadcastReceiver;
import com.aibrains.emergency.broadcastreceiver.NetworkBroadcastReceiver;
import com.aibrains.emergency.Family.AddiotDevice;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import android.content.IntentSender;




import com.aibrains.emergency.adapters.ArticleAdapter;
import com.aibrains.emergency.adapters.CustomAdsAdapter;
import com.aibrains.emergency.models.Article;
import com.aibrains.emergency.models.CustomAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.aibrains.emergency.databinding.UserHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserHome extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private static UserHome userHome ;

    private GoogleMap mMap;
    private UserHomeBinding binding;
    private LocationManager manager;
    private TextView speed , longitude , latitude , fullName , connection_warning ;
    private ImageButton emergency , safetyCheckIn, helpLine, profile , phoneModule , family;
    private LinearLayout gpsWarning;
    private int MIN_TIME = 1000;
    private int MIN_DISTANCE = 0 ;
    LatLng sydney = new LatLng(1,1);
    Functions functions = new Functions();
    Intent intent;
    Marker marker;
    static String key,userID;
    static String status = "false";
    MyLocation location;
    LocationManager locationManager ;
    ArrayList<Article>articles = new ArrayList<>();
    ArrayList<CustomAds> customAds = new ArrayList<>();
    private final HashMap<String, Marker> hm = new HashMap<String, Marker>();
    DatabaseReference users = FirebaseDatabase.getInstance().getReference("user");
    DatabaseReference subscriptions = FirebaseDatabase.getInstance().getReference("subscription_system");
    DatabaseReference articlesdb = FirebaseDatabase.getInstance().getReference("extras/articles");
    DatabaseReference adsdb = FirebaseDatabase.getInstance().getReference("extras/ads");
    DatabaseReference emergencyRequest = FirebaseDatabase.getInstance().getReference("emReq");
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int PERMISSION_SEND_SMS = 123;
    LocationBroadcastReceiver gpsLocationBroadcastReceiver = new LocationBroadcastReceiver();
    LocationBroadcastReceiver locationBroadcastReceiver = new LocationBroadcastReceiver();
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userHome = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding = UserHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sessionManager = new SessionManager(getApplicationContext());

        speed = findViewById(R.id.speed);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        emergency = findViewById(R.id.emergencyButton);
        safetyCheckIn = findViewById(R.id.safetyCheckInButton);
        phoneModule = findViewById(R.id.phoneModule);
        family = findViewById(R.id.family);
        helpLine = findViewById(R.id.helpLineButton);
        fullName = findViewById(R.id.fullName);
        profile = findViewById(R.id.profileButton);
        gpsWarning = findViewById(R.id.gps_warning);
        connection_warning = findViewById(R.id.connection_warning);


        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new NetworkBroadcastReceiver(), filter);
        //************* Using gps broadcast receiver to get realtime gps enable disable notification
        registerReceiver(gpsLocationBroadcastReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            gpsWarning.setVisibility(View.GONE);
        }
        else {
            gpsWarning.setVisibility(View.VISIBLE);

        }
        gpsWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions.buttonSwitchGPS_ON(UserHome.this);
            }
        });
        //************** end


        // FirebaseMessaging.getInstance().subscribeToTopic("all");



        RecyclerView article = findViewById(R.id.adminArticle);
        article.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        ArticleAdapter articleAdapter = new ArticleAdapter(this,articles);
        article.setAdapter(articleAdapter);


        RecyclerView ads = findViewById(R.id.adminAds);
        ads.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        CustomAdsAdapter customAdsAdapter = new CustomAdsAdapter(this,customAds);
        ads.setAdapter(customAdsAdapter);
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
        // startService(new Intent(UserHome.this, LocationService.class));




        SessionManager sessionManager = new SessionManager(getApplicationContext());
        userID = sessionManager.getValue("key_session_id");
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("user/"+userID);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationUpdates();

        showAll();
        requestSmsPermission();



        /** Add to shared preference **/

        try {
            user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    fullName.setText(snapshot.child("personal_info").child("fullname").getValue().toString());
                    sessionManager.userData(
                            snapshot.child("personal_info").child("fullname").getValue().toString(),
                            snapshot.child("email").getValue().toString(),
                            snapshot.child("personal_info").child("e_contact").getValue().toString(),
                            snapshot.child("personal_info").child("f_contact").getValue().toString(),
                            snapshot.child("personal_info").child("m_contact").getValue().toString(),
                            snapshot.child("personal_info").child("gender").getValue().toString(),
                            snapshot.child("personal_info").child("phone").getValue().toString()
                    );
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            });
        }catch (DatabaseException databaseException){}

        subscriptions.child("users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    sessionManager.subscriptionDetails(
                            snapshot.child("subscriptions_type").getValue().toString(),
                            snapshot.child("start_date").getValue().toString(),
                            snapshot.child("end_date").getValue().toString()
                    );
                    System.out.println(snapshot.child("subscriptions_type").getValue().toString());
                    System.out.println(userID);

            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        try {

            if (sessionManager.getValue("key_subscription_type").equals("paid")) {
                subscriptions.child("subscriptions").child("paid").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        sessionManager.subscriptionsFeatures(snapshot.child("features").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
            } else {
                subscriptions.child("subscriptions").child("free").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        sessionManager.subscriptionsFeatures(snapshot.child("features").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
            }
        }catch (Exception e){

        }





        checkStatus();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent accountPage = new Intent(UserHome.this,Account.class);
                startActivity(accountPage);
            }
        });

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.isOnEmergency("true");
                key = sessionManager.getValue("key_session_id");;
                functions.key = key ;
                user.child("activeReqToken").setValue(key);
                emergencyRequest.child(key).child("uniqueId").setValue(key);
                emergencyRequest.child(key).child("userId").setValue(key);
                emergencyRequest.child(key).child("name").setValue(sessionManager.getValue("key_name"));
                emergencyRequest.child(key).child("phone").setValue(sessionManager.getValue("key_phone"));
                emergencyRequest.child(key).child("status").setValue("pending");
                emergencyRequest.child(key).child("action_by").setValue("none");
                emergencyRequest.child(key).child("timeStamp").setValue("2/8/2023");
                emergencyRequest.child(key).child("comment").setValue("test comment");

                FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender("/topics/admins","Safe Me","Someone is on danger. Click for Action",
                        getApplicationContext(),UserHome.this);
                fcmNotificationsSender.SendNotifications();

                if(sessionManager.getValue("key_subscription_features").contains("parents_alert")) {
                    SMSthread smsThread = new SMSthread(sessionManager.getValue("key_fContact"), "I am on danger. Open 999App to track my location");
                    smsThread.run();
                }
                intent = new Intent(UserHome.this,UserMapView.class);
                startActivity(intent);
                //startService(new Intent(UserHome.this, LocationService.class));
            }
        });





        safetyCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.getValue("key_subscription_features").contains("safety_check_in")) {
                    intent = new Intent(UserHome.this, UnderConstructionPage.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"You need to be a pro user",Toast.LENGTH_SHORT).show();
                }
            }
        });
        phoneModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.getValue("key_subscription_features").contains("phoneModule")) {
                    intent = new Intent(UserHome.this, LostPhone.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"You need to be a pro user",Toast.LENGTH_SHORT).show();
                }
            }
        });
        family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.getValue("key_subscription_features").contains("family")) {
                    intent = new Intent(UserHome.this, Family.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"You need to be a pro user",Toast.LENGTH_SHORT).show();
                }
            }
        });
        helpLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.getValue("key_subscription_features").contains("helpline")) {
                    intent = new Intent(UserHome.this, SupportServices.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"You need to be a pro user",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(10);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("User")
                .icon(userIcon(UserHome.this,R.drawable.red)));




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
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION},101);
        }

        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));


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
                    Functions.buttonSwitchGPS_ON(UserHome.this);
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
                Functions.buttonSwitchGPS_ON(UserHome.this);
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
        String lat = String.valueOf(location.getLatitude());
        String longi = String.valueOf(location.getLongitude());
        String speeds = String.valueOf(location.getSpeed());
        latitude.setText(lat);
        longitude.setText(longi);
        speed.setText(speeds);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }

    private void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(UserHome.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(UserHome.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        } else {
        }
    }

    public void showAll(){
        Query query = (users.orderByChild("userType").equalTo("admin"));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(!dataSnapshot.getKey().equals(userID)){
                        String uid = dataSnapshot.getKey();
                        // helperId = dataSnapshot.child("userId").getValue().toString();
                        //Toast.makeText(UserMapView.this,helperId,Toast.LENGTH_SHORT).show();
                        location = dataSnapshot.child("realtimeLocation").getValue(MyLocation.class);
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            if (hm.containsKey(uid)) {
                                hm.get(uid).remove();
                            }
                            marker = mMap.addMarker(new MarkerOptions().position(userLocation).title(uid)
                                    .icon(userIcon(UserHome.this, R.drawable.green_small)));
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

    public void checkStatus(){
                if(sessionManager.getValue("key_is_on_emergency").equals("false")){

                }else {
                    //status = "true";
                    Intent intent = new Intent(UserHome.this,UserMapView.class);
                    startActivity(intent);
                }

        /**
        if(status.equals("true")){
            Intent intent = new Intent(UserHome.this,UserMapView.class);
            startActivity(intent);
        }
         **/
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




    public void buttonSwitchGPS_ON(){
        LocationRequest mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder();
        locationSettingsRequestBuilder.addLocationRequest(mLocationRequest);
        locationSettingsRequestBuilder.setAlwaysShow(true);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
               // textView.setText("Location settings (GPS) is ON.");
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure( Exception e) {
               // textView.setText("Location settings (GPS) is OFF.");

                if (e instanceof ResolvableApiException){
                    try {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        resolvableApiException.startResolutionForResult(UserHome.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });
    }




    static String getUserID(){
        return userID;
       // return "123";
    }
    static String getKey(){
        return userID;
       // return "101";
    }

    public static UserHome getInstance(){
        return userHome ;
    }
    public void setGpsWarning( int x) {
        UserHome.this.runOnUiThread(new Runnable() {
            public void run() {
                gpsWarning = findViewById(R.id.gps_warning);
                if(x == 0){
                    gpsWarning.setVisibility(View.GONE);
                }else if (x==1){
                    gpsWarning.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public void setNetWarning( int y) {
        UserHome.this.runOnUiThread(new Runnable() {
            public void run() {
                connection_warning = findViewById(R.id.connection_warning);
                if(y == 0){
                    connection_warning.setVisibility(View.GONE);
                }else if (y==1){
                    connection_warning.setVisibility(View.VISIBLE);
                }

            }
        });
    }



}