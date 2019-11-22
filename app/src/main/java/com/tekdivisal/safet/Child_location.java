package com.tekdivisal.safet;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Child_location extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, RoutingListener {

    private LinearLayout status_bottom_sheet, bus_bottom_sheet;
    private BottomSheetBehavior status_sheetBehavior, bus_sheetBehaviour;
    private BottomNavigationView bottomNavigationView;
    private Accessories child_location_accessor;

    final int LOCATION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private String school_code, parent_code;
    private DatabaseReference driverLocationref, arrived_databaseReference;
    private Marker mDriverMarker, muserMarker;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Location mLastLocation;
    private LatLng pickuplocation, driverlatlng;
    private String sdriver_code, sfirst_name, slastname, saddress, sphone_number,
            sbrand, schasis_no, sbus_code, smodel, snumber_plate, sschoolemail, ssechoollocation,
            sschoolphone, sschoolname, child_fname_from_home, child_lname_from_home;
    private TextView driver_name_tv, number_plate_tv, bus_model_tv, school_name_tv, school_number_tv,
    status_school_name, status_distance,status_time, status_date, status_start_time, status_end_time;
    private String TAG = "so47492459";
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark, R.color.colorPrimary, R.color.main_blue, R.color.colorAccent, R.color.primary_dark_material_light};

    private FrameLayout child_find_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_location);

        //child name intents from home
        child_fname_from_home = getIntent().getStringExtra("from_home_child_fname");
        child_lname_from_home = getIntent().getStringExtra("from_home_child_lname");

        getSupportActionBar().setTitle(child_fname_from_home + " " + child_lname_from_home);
        child_location_accessor = new Accessories(this);

        school_code = child_location_accessor.getString("school_code");
        parent_code = child_location_accessor.getString("user_phone_number");

        //layout for this activity
        child_find_layout = findViewById(R.id.child_find_layout);

        bottomNavigationView = findViewById(R.id.find_child_navigation);
        //status bottom sheet
        status_bottom_sheet = findViewById(R.id.status_bottom_sheet);
        status_sheetBehavior = BottomSheetBehavior.from(status_bottom_sheet);

        //bus bottom sheet
        bus_bottom_sheet = findViewById(R.id.bus_bottom_sheet);
        bus_sheetBehaviour = BottomSheetBehavior.from(bus_bottom_sheet);

        driver_name_tv = findViewById(R.id.the_driver_name);
        number_plate_tv = findViewById(R.id.the_licence_plate);
        bus_model_tv = findViewById(R.id.the_bus_model);
        school_name_tv = findViewById(R.id.the_school_name);
        school_number_tv = findViewById(R.id.theschool_number);

        //status items
        status_school_name  = findViewById(R.id.school_name);
        status_distance  = findViewById(R.id.distance);
        status_time  = findViewById(R.id._time);
        status_date  = findViewById(R.id.date);
        status_start_time  = findViewById(R.id.start_time);
        status_end_time = findViewById(R.id.end_time);

        //setting the date
        status_date.setText(DateFormat.getDateInstance(DateFormat.FULL).format(new Date()));

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (ActivityCompat.checkSelfPermission(Child_location.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Child_location.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Child_location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            assert mapFragment != null;
            mapFragment.getMapAsync(this);
        }
        if (isNetworkAvailable()) {
            getDriverID();
//            getBusDetails();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.l_location:
//                        fragmentManager.beginTransaction().replace(R.id.child_find_layout,new Bus_Location()).commit();
                        break;

                    case R.id.bus:
//                        child_location_accessor.put("vital_type","bus_vitals");
//                        fragmentManager.beginTransaction().replace(R.id.child_find_layout,new Vital_Info()).commit();
                        if (bus_sheetBehaviour.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                            bus_sheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                            status_sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        } else {
                            bus_sheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        break;

                    case R.id.status:
//                        child_location_accessor.put("vital_type","status_vitals");
//                        fragmentManager.beginTransaction().replace(R.id.child_find_layout,new Vital_Info()).commit();
                        if (status_sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                            status_sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            bus_sheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        } else {
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
            ActivityCompat.requestPermissions(Child_location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (muserMarker != null) {
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

    protected synchronized void buildGoogleApiClient() {
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
        try {
            DatabaseReference get_Driver_notifications = FirebaseDatabase.getInstance().getReference("bus_location").child(school_code);

            get_Driver_notifications.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            getDriverLocation(child.getKey());
                        }
                    } else {
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Child_location.this, "Cancelled", Toast.LENGTH_LONG).show();
                }
            });
        } catch (NullPointerException e) {

        }
    }

    private void getDriverLocation(final String key) {
        driverLocationref = FirebaseDatabase.getInstance().getReference().child("bus_location").child(school_code).child(key).child("l");
        driverLocationref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationlat = 0;
                    double locationlong = 0;
                    if (map.get(0) != null) {
                        locationlat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationlong = Double.parseDouble(map.get(1).toString());
                    }
                    driverlatlng = new LatLng(locationlat, locationlong);
                    if (mDriverMarker != null) {
                        mDriverMarker.remove();
                    }
//
//                    pickuplocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//
                    Location loc1 = new Location("");
                    try {
                        loc1.setLatitude(pickuplocation.latitude);
                        loc1.setLongitude(pickuplocation.longitude);
                    } catch (NullPointerException e) {

                    }

                    Location loc2 = new Location("");
                    try {
                        loc2.setLatitude(driverlatlng.latitude);
                        loc2.setLongitude(driverlatlng.longitude);
                    } catch (NullPointerException e) {

                    }

//                    drawLine();

                    float distance = loc1.distanceTo(loc2);
                    status_distance.setText(String.valueOf(distance)+"m away");
                    status_time.setText("00:00");
                    if (distance < 80) {
                        status_distance.setText("Bus arrived");
//                      // add to notifications
                        Bus_arrived_notification();
                    } else {
                        status_distance.setText(String.valueOf(distance)+"m away");
                    }

                    //drawing a line between the two destinations
//                    PolylineOptions options = new PolylineOptions().add(pickuplocation)
//                            .add(driverlatlng).width(5).color(Color.BLUE).geodesic(true);
//
//                    mMap.addPolyline(options);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(driverlatlng).tilt(5)
                            .zoom(17)
                            .build();
                    try {
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } catch (NullPointerException e) {

                    }

                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverlatlng).title("Bus").flat(true));//.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_)));
                    Fetch_Driver_Info(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void drawLine() {

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(pickuplocation, driverlatlng)
                .key("AIzaSyBqHR9F0VhEhOAXIgktB4lQJLD_HutD4fQ")
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int ih) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        // Add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

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

    private void Fetch_Driver_Info(final String key) {
        DatabaseReference getdriver_info = FirebaseDatabase.getInstance().getReference("drivers").child(school_code).child(key);
        getdriver_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("first_name")){
                            sfirst_name = child.getValue().toString();
                        }
                        if(child.getKey().equals("last_name")){
                            slastname = child.getValue().toString();
                        }
                        if(child.getKey().equals("address")){
                            saddress = child.getValue().toString();
                        }
                        if(child.getKey().equals("phone_number")){
                            sphone_number = child.getValue().toString();
                        }
                        sdriver_code = key;
                    }
                    driver_name_tv.setText(sfirst_name + " " + slastname);
                    getBusDetails(school_code,sdriver_code);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getBusDetails(final String schoolcode, String drivercode) {
        DatabaseReference school_details = FirebaseDatabase.getInstance().getReference("bus_details").child(schoolcode).child(drivercode);
        school_details.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("brand")){
                            sbrand = child.getValue().toString();
                        }
                        if(child.getKey().equals("bus_code")){
                            sbus_code = child.getValue().toString();
                        }
                        if(child.getKey().equals("chasis_no")){
                            schasis_no = child.getValue().toString();
                        }
                        if(child.getKey().equals("model")){
                            smodel = child.getValue().toString();
                        }
                        if(child.getKey().equals("number_plate")){
                            snumber_plate = child.getValue().toString();
                        }
                    }
                    number_plate_tv.setText(snumber_plate);
                    bus_model_tv.setText(sbrand + " " + smodel);
                    getSchoolinformation(schoolcode);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSchoolinformation(String key) {
        DatabaseReference school_details = FirebaseDatabase.getInstance().getReference("schools").child(key);
        school_details.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("email")){
                            sschoolemail = child.getValue().toString();
                            child_location_accessor.put("school_email",sschoolemail);
                        }
                        if(child.getKey().equals("location")){
                            ssechoollocation = child.getValue().toString();
                            child_location_accessor.put("school_location",ssechoollocation);
                        }
                        if(child.getKey().equals("name")){
                            sschoolname = child.getValue().toString();
                            child_location_accessor.put("school_name",sschoolname);
                        }
                        if(child.getKey().equals("telephone")){
                            sschoolphone = child.getValue().toString();
                            child_location_accessor.put("school_telephone",sschoolphone);
                        }
                    }
                    school_name_tv.setText(sschoolname);
                    status_school_name.setText(sschoolname);
                    school_number_tv.setText(sschoolphone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Bus_arrived_notification() {
        Random notifyrandom = new Random();
        int notify_no = notifyrandom.nextInt(2334);
        String notify_id = "arrived" + notify_no+"" + schasis_no;
        arrived_databaseReference = FirebaseDatabase.getInstance().getReference("bus_notification").child(parent_code).child(school_code).child(sdriver_code);
        arrived_databaseReference.child("image").setValue("BAN");
        arrived_databaseReference.child("message").setValue("Bus has arrived at the pickup/school location. If pickup is required, please proceed to bus stop to pickup child.");
        arrived_databaseReference.child("time").setValue(new Date());
        arrived_databaseReference.child("title").setValue("Bus Arrived");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
