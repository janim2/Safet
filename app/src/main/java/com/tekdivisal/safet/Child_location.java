package com.tekdivisal.safet;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.PolyUtil;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Child_location extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private LinearLayout status_bottom_sheet, bus_bottom_sheet;
    private BottomSheetBehavior status_sheetBehavior, bus_sheetBehaviour;
    private BottomNavigationView bottomNavigationView;
    private Accessories child_location_accessor;

    final int LOCATION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private String school_code, parent_code;
    private DatabaseReference driverLocationref;
    private Marker mDriverMarker, muserMarker;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Location mLastLocation;
    private LatLng pickuplocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_location);

        getSupportActionBar().setTitle("Student Name");

        child_location_accessor = new Accessories(this);

        school_code = child_location_accessor.getString("school_code");
        parent_code = child_location_accessor.getString("user_phone_number");

        bottomNavigationView = findViewById(R.id.find_child_navigation);
        //status bottom sheet
        status_bottom_sheet = findViewById(R.id.status_bottom_sheet);
        status_sheetBehavior = BottomSheetBehavior.from(status_bottom_sheet);

        //bus bottom sheet
        bus_bottom_sheet = findViewById(R.id.bus_bottom_sheet);
        bus_sheetBehaviour = BottomSheetBehavior.from(bus_bottom_sheet);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (ActivityCompat.checkSelfPermission(Child_location.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Child_location.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Child_location.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }else{
            assert mapFragment != null;
            mapFragment.getMapAsync(this);
        }
        if(isNetworkAvailable()){
            getDriverID();
//            getBusDetails();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.l_location:
//                        fragmentManager.beginTransaction().replace(R.id.child_find_layout,new Bus_Location()).commit();
                        break;

                    case R.id.bus:
//                        child_location_accessor.put("vital_type","bus_vitals");
//                        fragmentManager.beginTransaction().replace(R.id.child_find_layout,new Vital_Info()).commit();
                        if(bus_sheetBehaviour.getState() != BottomSheetBehavior.STATE_EXPANDED){
                            bus_sheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                            status_sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }else{
                            bus_sheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        break;

                    case R.id.status:
//                        child_location_accessor.put("vital_type","status_vitals");
//                        fragmentManager.beginTransaction().replace(R.id.child_find_layout,new Vital_Info()).commit();
                        if(status_sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                            status_sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            bus_sheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }else{
                            status_sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(Child_location.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Child_location.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Child_location.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        if(muserMarker != null){
            muserMarker.remove();
        }
        muserMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Me").flat(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        pickuplocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(Child_location.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Child_location.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

//        muserMarker = mMap.addMarker(new MarkerOptions().position(pickuplocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(Child_location.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API
                ).build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayLocationSettingsRequest(Child_location.this);
    }

    private void getDriverID() {
        try{
            DatabaseReference get_Driver_notifications = FirebaseDatabase.getInstance().getReference("bus_location").child(school_code);

            get_Driver_notifications.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            getDriverLocation(child.getKey());
                        }
                    }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Child_location.this,"Cancelled",Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){

        }
    }

    private void getDriverLocation(String key) {
        driverLocationref = FirebaseDatabase.getInstance().getReference().child("bus_location").child(school_code).child(key).child("l");
        driverLocationref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationlat = 0;
                    double locationlong = 0;
                    if(map.get(0) != null){
                        locationlat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationlong = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverlatlng = new LatLng(locationlat,locationlong);
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }
//
//                    pickuplocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//
                    Location loc1  = new Location("");
                    try{
                        loc1.setLatitude(pickuplocation.latitude);
                        loc1.setLongitude(pickuplocation.longitude);
                    }catch (NullPointerException e){

                    }

                    Location loc2  = new Location("");
                    try{
                        loc2.setLatitude(driverlatlng.latitude);
                        loc2.setLongitude(driverlatlng.longitude);
                    }catch (NullPointerException e){

                    }

                    float distance = loc1.distanceTo(loc2);
                    Toast.makeText(Child_location.this,"Bus Distance: "+ String.valueOf(distance), Toast.LENGTH_LONG).show();
                    if(distance<100){
                        Toast.makeText(Child_location.this, "bus arrived", Toast.LENGTH_SHORT).show();

//                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Whereto.this)
//                                .setSmallIcon(R.drawable.pickbot_logo)
//                                .setContentTitle("Bus Arrived")
//                                .setContentText("School Bus Has Arrived.")
//                                .setStyle(new NotificationCompat.BigTextStyle()
//                                        .bigText("School Bus Has Arrived."))
//                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        // notificationID allows you to update the notification later on.
//                        mNotificationManager.notify(1, mBuilder.build());

                    }else{
                        Toast.makeText(Child_location.this,"Bus Distance: "+ String.valueOf(distance), Toast.LENGTH_LONG).show();
                    }

                    //drawing a line between the two destinations
                    PolylineOptions options = new PolylineOptions().add(pickuplocation)
                            .add(driverlatlng).width(5).color(Color.BLUE).geodesic(true);

                    mMap.addPolyline(options);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(driverlatlng).tilt(5)
                            .zoom(17)
                            .build();
                    try {
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }catch (NullPointerException e){

                    }

                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverlatlng).title("Bus").flat(true));//.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            public static final String TAG = "";

            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(Child_location.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
