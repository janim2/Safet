package com.tekdivisal.safet;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.DrawableRes;
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
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
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
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;
import com.tekdivisal.safet.Model.Constants;
import com.tekdivisal.safet.Model.KeyValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static java.text.DateFormat.getDateTimeInstance;

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
            sschoolphone, sschoolname, child_fname_from_home, child_lname_from_home, child_code,
            assigned_driver_code, isassigned_bus;
    private TextView driver_name_tv, number_plate_tv, bus_model_tv, school_name_tv, school_number_tv,
    status_school_name, status_distance,status_time, status_date, status_start_time, status_end_time,
    location_name, change_or_set_location_;
    private String TAG = "so47492459";
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark, R.color.colorPrimary, R.color.main_blue, R.color.colorAccent, R.color.primary_dark_material_light};

    List<Address> addresses;
    private FrameLayout child_find_layout;
    private Geocoder geocoder;

    VolleyRequest request;
    private String bus_status,bus_starttime_hrs, bus_status_mins, bus_starttime_secs, theduration, end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            setContentView(R.layout.activity_child_location);
        }catch (InflateException e){

        }

        polylines = new ArrayList<>();

        request = new VolleyRequest(this);

        child_location_accessor = new Accessories(this);
        //child name intents from home
        child_fname_from_home = child_location_accessor.getString("from_home_child_fname");
        child_lname_from_home = child_location_accessor.getString("from_home_child_lname");
        child_code = child_location_accessor.getString("child_code");
        isassigned_bus = child_location_accessor.getString("isAssigned_bus");

        getSupportActionBar().setTitle(child_fname_from_home + " " + child_lname_from_home);

        geocoder = new Geocoder(Child_location.this, Locale.getDefault());

        school_code = child_location_accessor.getString("school_code");
        parent_code = child_location_accessor.getString("user_phone_number");

        //layout for this activity
        child_find_layout = findViewById(R.id.child_find_layout);

        bottomNavigationView = findViewById(R.id.find_child_navigation);

        //status bottom sheet
        try{
            status_bottom_sheet = findViewById(R.id.status_bottom_sheet);
            status_sheetBehavior = BottomSheetBehavior.from(status_bottom_sheet);
        }catch (NullPointerException e){

        }

        //bus bottom sheet
        try {
            bus_bottom_sheet = findViewById(R.id.bus_bottom_sheet);
            bus_sheetBehaviour = BottomSheetBehavior.from(bus_bottom_sheet);
        }catch (NullPointerException e){

        }

        //setting location items
        location_name = findViewById(R.id.location_name);
        change_or_set_location_ = findViewById(R.id.change_set_location);

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
        try{
            status_date.setText(DateFormat.getDateInstance(DateFormat.FULL).format(new Date()));
        }catch (NullPointerException e){

        }

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
            if(isassigned_bus.equals("Yes")){
                if(child_location_accessor.getBoolean("has_set_location")){
                    change_or_set_location_.setText("Change");
                    getDriverID();
//                location_name.setText("");
                }else{
                    try{
                        change_or_set_location_.setText("Set");
                        location_name.setText("Location not set");
                    }catch (NullPointerException e){

                    }
                    getDriverID();
                }
            }else{
                change_or_set_location_.setVisibility(View.GONE);
                location_name.setText("Unassigned");
                AlertDialog.Builder notassigned = new AlertDialog.Builder(this);
                notassigned.setTitle("Bus Assignment");
                notassigned.setMessage(child_fname_from_home + " has not been assigned to a bus. As" +
                        " such no bus can pick him up from or to school. \n\nContact school administration for " +
                        "assistance");
                notassigned.setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                });
                notassigned.show();
            }

//            getBusDetails();
        }

        try{
            change_or_set_location_.setOnClickListener(v -> startActivity(new Intent(Child_location.this, Set_Pickuplocation.class)));
        }catch (NullPointerException e){

        }

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
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
//        mMap.setMyLocationEnabled(true);

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
//            DatabaseReference get_Driver_notifications = FirebaseDatabase.getInstance()
//                    .getReference("bus_location").child(school_code);

            DatabaseReference get_Driver_Id = FirebaseDatabase.getInstance().getReference("children")
                    .child(school_code).child(parent_code).child(child_code);

            get_Driver_Id.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if(child.getKey().equals("assigned_bus")){
                                assigned_driver_code = child.getValue().toString();
//                                Toast.makeText(Child_location.this, "assigned: "+assigned_driver_code, Toast.LENGTH_LONG).show();
                                getDriverLocation(assigned_driver_code);
                            }
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

//
//                    pickuplocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    final Location loc1 = new Location("");

                    if(child_location_accessor.getBoolean("has_set_location")){
                        DatabaseReference user_pickup_location = FirebaseDatabase.getInstance().getReference("user_location")
                                .child(school_code).child(parent_code).child("l");
                        user_pickup_location.addListenerForSingleValueEvent(new ValueEventListener() {
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

                                    loc1.setLatitude(locationlat);
                                    loc1.setLongitude(locationlong);

                                    try {
                                        addresses = geocoder.getFromLocation(loc1.getLatitude(),loc1.getLongitude(),1);
                                        location_name.setText(addresses.get(0).getLocality());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }else{
                        try {
                            loc1.setLatitude(pickuplocation.latitude);
                            loc1.setLongitude(pickuplocation.longitude);
                            location_name.setText("Current location");
                        } catch (NullPointerException e) {

                        }
                    }


                    Location loc2 = new Location("");
                    try {
                        loc2.setLatitude(driverlatlng.latitude);
                        loc2.setLongitude(driverlatlng.longitude);
                    } catch (NullPointerException e) {

                    }

                    float distance = loc1.distanceTo(loc2);
                    status_distance.setText(String.valueOf(distance)+"m away");
                    if (distance < 50) {
                        status_distance.setText("Bus arrived");
//                      // add to notifications
                        Bus_arrived_notification();
                    } else {
                        status_distance.setText(String.valueOf(distance)+"m away");
                    }

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(driverlatlng).tilt(5)
                            .zoom(8)
                            .build();
                    try {
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } catch (NullPointerException e) {

                    }

                    if (mDriverMarker != null) {
                        mDriverMarker.remove();
                    }

                    try{
                        mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverlatlng)
                                .title(child_fname_from_home).flat(true).icon(BitmapDescriptorFactory
                                        .fromBitmap(getMarkerBitmapFromView(getResources(),R.drawable.schoolbus,30,30))));//.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_)));
//                        drawPolyLine(loc1, loc2);
                        drawLine();

                    }catch (NullPointerException e){

                    }

                    Fetch_Driver_Info(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void drawLine() {
        try {
            if(pickuplocation != null && driverlatlng != null){
                Routing routing = new Routing.Builder()
                        .travelMode(Routing.TravelMode.DRIVING)
                        .withListener(this)
                        .waypoints(pickuplocation, driverlatlng)
                        .key(getResources().getString(R.string.google_maps_key))
                        .build();
                routing.execute();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
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
            try {
                status_time.setText(String.valueOf(route.get(i).getDurationText()) + " away");
                theduration = route.get(i).getDurationText();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
//            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
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
                    FetchDriver_Starttime(school_code, sdriver_code);

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
                        if(child.getKey().equals("the_email")){
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

    private void FetchDriver_Starttime(String sch_code, String drivercode) {
        try {
            DatabaseReference timereference = FirebaseDatabase.getInstance().getReference("trip_status")
                    .child(sch_code).child(drivercode);
            timereference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.getKey().equals("status")) {
                                bus_status = child.getValue().toString();
                            }

                            if (!bus_status.equals("Arrived")) {
                                DatabaseReference timereference = FirebaseDatabase.getInstance().getReference("trip_status")
                                        .child(sch_code).child(drivercode).child("time");
                                timereference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                if (child.getKey().equals("hours")) {
                                                    bus_starttime_hrs = child.getValue().toString();
                                                }

                                                if (child.getKey().equals("minutes")) {
                                                    bus_status_mins = child.getValue().toString();
                                                }

                                                if (child.getKey().equals("seconds")) {
                                                    bus_starttime_secs = child.getValue().toString();
                                                } else {
//                                                  Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                                                }
                                            }

                                            try {
                                                status_start_time.setText(bus_starttime_hrs+":"+bus_status_mins);
                                            }catch (Exception e){

                                            }

                                            //setting end time
                                            try {
                                                String remove_hours  = theduration.replace("hours", " ");
                                                String remove_mins = remove_hours.replace("mins", "");
                                                String[] splited_time = remove_mins.split("",2);
                                                String[] nextst = splited_time[1].split(" ",2);
                                                String hr = nextst[0];
                                                String min = nextst[1].replaceFirst(" ", "").trim();

                                                int add_mins = Integer.valueOf(min) + Integer.valueOf(bus_status_mins);
                                                if(add_mins > 60){
                                                    int submins = add_mins - 60;
                                                    int add_hrs = Integer.valueOf(hr) + Integer.valueOf(bus_starttime_hrs);
                                                    int total_hrs = add_hrs + 1;
                                                    end_time = String.valueOf(total_hrs)+":"+String.valueOf(submins);
                                                    status_end_time.setText(end_time);

                                                }else{
                                                    int add_hrs = Integer.valueOf(hr) + Integer.valueOf(bus_starttime_hrs);
                                                    end_time = String.valueOf(add_hrs)+":"+String.valueOf(add_mins);
                                                    status_end_time.setText(end_time);

                                                }
                                            }catch (NullPointerException e){

                                            }

//                                            end_time ends here

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }else {
                                status_start_time.setText("Trip not started");
                                status_end_time.setText("Arrived at " + end_time);
                            }
                        }
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

    private void Bus_arrived_notification() {
        try {
            Random notifyrandom = new Random();
            int notify_no = notifyrandom.nextInt(2334);
            String notify_id = "arrived" + notify_no+"" + schasis_no;
            arrived_databaseReference = FirebaseDatabase.getInstance().getReference("bus_notification")
                    .child(parent_code).child(school_code).child(sdriver_code);
            arrived_databaseReference.child("image").setValue("BAN");
            arrived_databaseReference.child("message").setValue("Bus has arrived at the pickup/school location. If pickup is required, please proceed to bus stop to pickup child.");
            arrived_databaseReference.child("time").setValue(new Date());
            arrived_databaseReference.child("title").setValue("Bus Arrived");
        }catch (NullPointerException e){

        }

    }

//    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {
//            View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
//            ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
//            markerImageView.setImageResource(resId);
//            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
//            customMarkerView.buildDrawingCache();
//            Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
//                    Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(returnedBitmap);
//            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
//            Drawable drawable = customMarkerView.getBackground();
//            if (drawable != null)
//                drawable.draw(canvas);
//            customMarkerView.draw(canvas);
//        return returnedBitmap;
//
//    }


    private Bitmap getMarkerBitmapFromView(Resources res, @DrawableRes int resId,
                                           int requiredWidth, int requiredHeight){
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
//
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId, options);

        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Drawable drawable = customMarkerView.getBackground();
            if (drawable != null)
                drawable.draw(canvas);
            customMarkerView.draw(canvas);
        return BitmapFactory.decodeResource(res, resId, options);

    }


    private int calculateInSampleSize(BitmapFactory.Options options, int requiredWidth, int requiredHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > requiredWidth || width > requiredWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= requiredHeight
                    && (halfWidth / inSampleSize) >= requiredHeight) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

//    @Override
//    public void getResponse(String response, int REQUEST_ID) throws JSONException {
//        JSONObject object = new JSONObject(response);
//        switch(REQUEST_ID){
//            case 2:
//                Toast.makeText(Child_location.this, "loation", Toast.LENGTH_LONG).show();
//                JSONArray routes = object.getJSONArray("routes");
//                JSONObject route = routes.getJSONObject(0);
////                JSONObject overview_polyline = route.getJSONObject("overview_polyline");
//                JSONArray legs = route.getJSONArray("legs");
//                JSONObject leg = legs.getJSONObject(0);
//                JSONObject start = leg.getJSONObject("start_location");
//                JSONObject end = leg.getJSONObject("end_location");
//                Toast.makeText(Child_location.this, end.toString(), Toast.LENGTH_LONG).show();
//
////                String polyLineString = overview_polyline.getString("points");
//
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                        new LatLng(end.getDouble("lat"), end.getDouble("lng")), 20));
//
//                drawPath(legs.getJSONObject(0).getJSONArray("steps"),
//                        new LatLng(start.getDouble("lat"), start.getDouble("lng")),
//                        new LatLng(end.getDouble("lat"), end.getDouble("lng")));
//                break;
//        }
//
//    }

}
