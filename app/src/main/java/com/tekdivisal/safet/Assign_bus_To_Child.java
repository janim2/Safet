package com.tekdivisal.safet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assign_bus_To_Child extends AppCompatActivity {

    private String childcode, child_fname, child_lname;
    private Intent intent;
    private TextView childname, status, no_internet;
    private Spinner bus_spinner;
    private Button assign_button;
    private Accessories assign_accessor;
    private String school_code, parent_code, driverfirst_name, driverlastname, driveraddress,
            driverphone_number, sdriver_code, sbrand, sbus_code, schasis_no, smodel, snumber_plate,
            sbus_capacity, snumber_left, thebusRoute,sbus_route, selected_bus, is_assigned_bus;
    final List<String> titleList = new ArrayList<String>();
    private ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_bus__to__child);
        getSupportActionBar().setTitle("Bus Assignment");
        assign_accessor = new Accessories(Assign_bus_To_Child.this);

        intent = getIntent();

        school_code = assign_accessor.getString("school_code");
        parent_code = assign_accessor.getString("user_phone_number");

        childcode   = assign_accessor.getString("child_code");
        child_fname = assign_accessor.getString("from_home_child_fname");
        child_lname = assign_accessor.getString("from_home_child_lname");

        childname = findViewById(R.id.child_name);
        bus_spinner = findViewById(R.id.bus_spinner);
        assign_button = findViewById(R.id.assign_button);
        status = findViewById(R.id.status);
        no_internet = findViewById(R.id.no_internet);
        loading = findViewById(R.id.loading);

        childname.setText(child_fname + " " + child_lname);

        if(isNetworkAvailable()){
            Fetch_Bus_ID();
        }else{
            loading.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
            status.setVisibility(View.GONE);
        }

        no_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    Fetch_Bus_ID();
                }else{
                    loading.setVisibility(View.GONE);
                    no_internet.setVisibility(View.VISIBLE);
                    status.setVisibility(View.GONE);
                }
            }
        });

        //setting the adapter for the spinner
        bus_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_bus = titleList.get(position);
//                Toast.makeText(Assign_bus_To_Child.this, sdriver_code, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_bus = titleList.get(0);
            }
        });

        assign_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    Get_Assigned_Driver_Code(selected_bus);
                }
            }
        });
    }

    private void Get_Assigned_Driver_Code(final String selected_bus) {
        loading.setVisibility(View.VISIBLE);
        no_internet.setVisibility(View.GONE);
        status.setVisibility(View.GONE);
        try{
            DatabaseReference bus_details = FirebaseDatabase.getInstance().getReference("bus_details")
                    .child(school_code);
            bus_details.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Check_assigned_number(child.getKey(),selected_bus);
                        }
                    }
                    else {
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Assign_bus_To_Child.this, "Cancelled", Toast.LENGTH_LONG).show();
                }
            });
        }catch(NullPointerException e){

        }
    }

    private void Check_assigned_number(String key, final String selected_bus) {

        //finding parent node from child value
        Query getDriver_code_query = FirebaseDatabase.getInstance().getReference("bus_details").child(school_code).orderByChild("bus_route").equalTo(selected_bus);
        getDriver_code_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    sdriver_code = key;
                    DatabaseReference bus_details = FirebaseDatabase.getInstance().getReference("bus_details")
                                        .child(school_code).child(sdriver_code);
                                bus_details.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                                if(child.getKey().equals("assigned_no_left")){
                                                    snumber_left = child.getValue().toString();
                                                    if(Integer.valueOf(snumber_left) > 0){
                                                        Assign_Child_To_Bus(sdriver_code, snumber_left);
                                                    }else{
                                                        loading.setVisibility(View.GONE);
                                                        no_internet.setVisibility(View.GONE);
                                                        status.setText("Bus is full");
                                                        status.setVisibility(View.VISIBLE);
                                                    }
                                                }

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        Toast.makeText(Assign_bus_To_Child.this, "selected bus " + selected_bus, Toast.LENGTH_LONG).show();
//        DatabaseReference bus_details = FirebaseDatabase.getInstance().getReference("bus_details")
//                .child(school_code).child(sdriver_code);
//        bus_details.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    for(DataSnapshot child : dataSnapshot.getChildren()){
//                        if(child.getKey().equals("bus_route")){
//                            thebusRoute = child.getValue().toString();
//                            if(thebusRoute.equals(selected_bus)){
//                                String thedriver_code = sdriver_code;
//                                DatabaseReference bus_details = FirebaseDatabase.getInstance().getReference("bus_details")
//                                        .child(school_code).child(thedriver_code);
//                                bus_details.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        if(dataSnapshot.exists()){
//                                            for(DataSnapshot child : dataSnapshot.getChildren()){
//                                                if(child.getKey().equals("assigned_no_left")){
//                                                    snumber_left = child.getValue().toString();
//
//                                                    if(Integer.valueOf(snumber_left) > 0){
//                                                        Assign_Child_To_Bus(sdriver_code, snumber_left);
//                                                    }else{
//                                                        loading.setVisibility(View.GONE);
//                                                        no_internet.setVisibility(View.GONE);
//                                                        status.setText("Bus is full");
//                                                        status.setVisibility(View.VISIBLE);
//                                                    }
//                                                }
//
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//                        }
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void Assign_Child_To_Bus(final String sdriver_code, final String number_left) {
        try{
            final DatabaseReference assign_child = FirebaseDatabase.getInstance().getReference("children")
                    .child(school_code).child(parent_code).child(childcode);

            assign_child.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("isAssigned_bus")){
                                is_assigned_bus = child.getValue().toString();
                                if(is_assigned_bus.equals("No")){
                                    assign_child.child("isAssigned_bus").setValue("Yes");
                                    assign_child.child("assigned_bus").setValue(sdriver_code);

                                    DatabaseReference reduce_bus_assigned_ = FirebaseDatabase.getInstance().getReference("bus_details")
                                            .child(school_code).child(sdriver_code);
                                    int nu_left  = Integer.valueOf(number_left) - 1;
                                    reduce_bus_assigned_.child("assigned_no_left").setValue(String.valueOf(nu_left)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loading.setVisibility(View.GONE);
                                            no_internet.setVisibility(View.GONE);
                                            status.setText("Child assignment complete");
                                            status.setVisibility(View.VISIBLE);
                                            finish();
                                            startActivity(new Intent(Assign_bus_To_Child.this, Child_location.class));
                                        }
                                    });

                                }else{
                                    loading.setVisibility(View.GONE);
                                    no_internet.setVisibility(View.GONE);
                                    status.setText("Child is already assigned");
                                    status.setVisibility(View.VISIBLE);
                                }
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_Bus_ID() {
        try {
            DatabaseReference get_Driver_ID = FirebaseDatabase.getInstance().getReference("bus_details")
                    .child(school_code);

            get_Driver_ID.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            FetchBusDetails(child.getKey());
                        }
                    } else {
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Assign_bus_To_Child.this, "Cancelled", Toast.LENGTH_LONG).show();
                }
            });
        } catch (NullPointerException e) {

        }
    }

    private void FetchBusDetails(final String driver_code) {
        DatabaseReference bus_details = FirebaseDatabase.getInstance().getReference("bus_details")
                .child(school_code).child(driver_code);
        bus_details.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        if(child.getKey().equals("bus_capacity")){
                            sbus_capacity = child.getValue().toString();
                        }
                        if(child.getKey().equals("assigned_no_left")){
                            snumber_left = child.getValue().toString();
                        }
                        if(child.getKey().equals("bus_route")){
                            sbus_route = child.getValue().toString();
                        }
                    }
                    titleList.add(sbus_route);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Assign_bus_To_Child.this, android.R.layout.simple_spinner_item, titleList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    bus_spinner.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
