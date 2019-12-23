package com.tekdivisal.safet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdivisal.safet.Adapters.NotifyAdapter;
import com.tekdivisal.safet.Adapters.SchoolAdapter;
import com.tekdivisal.safet.Model.Notify;
import com.tekdivisal.safet.Model.School;

import java.util.ArrayList;

public class Select_School extends AppCompatActivity {

    private ArrayList schoolsArray = new ArrayList<School>();
    private RecyclerView schools_RecyclerView;
    private RecyclerView.Adapter schools_Adapter;
    private String school_code, parent_code,sschoolemail, ssechoollocation,sschoolname,sschoolphone;
    private FirebaseAuth mauth;
    private TextView no_internet,fetching_indicator,change_number;
    private ImageView goBack;

    private Accessories select_school_accessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__school);

        select_school_accessor = new Accessories(Select_School.this);

        mauth = FirebaseAuth.getInstance();

        parent_code = select_school_accessor.getString("user_phone_number");

        schools_RecyclerView = findViewById(R.id.schools_recyclerView);
        no_internet = findViewById(R.id.nointernet);
        fetching_indicator = findViewById(R.id.fetching_indicator);
        goBack = findViewById(R.id.back);
        change_number = findViewById(R.id.change_number);

        //reviews adapter settings starts here
        if(isNetworkAvailable()){
            getAll_School_IDs();
        }else{
            fetching_indicator.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }
        schools_RecyclerView.setHasFixedSize(true);
        schools_Adapter = new SchoolAdapter(getSchoolNameFromDatabase(),Select_School.this);
        schools_RecyclerView.setAdapter(schools_Adapter);

        no_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    getAll_School_IDs();
                }else{
                    fetching_indicator.setVisibility(View.GONE);
                    no_internet.setVisibility(View.VISIBLE);
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        change_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_school_accessor.put("isverified",false);
                startActivity(new Intent(Select_School.this, Number_Verification.class));
            }
        });

    }

    private void getAll_School_IDs() {
        fetching_indicator.setVisibility(View.VISIBLE);
        no_internet.setVisibility(View.GONE);
        try{
            DatabaseReference get_school_ids = FirebaseDatabase.getInstance().getReference("schools");

            get_school_ids.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            getSchoolinformation(child.getKey());
                        }
                    }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Select_School.this,"Cancelled",Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){

        }
    }

    private void getSchoolinformation(final String key) {
        DatabaseReference school_details = FirebaseDatabase.getInstance().getReference("schools").child(key);
        school_details.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("email")){
                            sschoolemail = child.getValue().toString();
                        }
                        if(child.getKey().equals("location")){
                            ssechoollocation = child.getValue().toString();
                        }
                        if(child.getKey().equals("name")){
                            sschoolname = child.getValue().toString();
                        }
                        if(child.getKey().equals("telephone")){
                            sschoolphone = child.getValue().toString();
                        }
                    }
                    school_code = key;
                    School obj = new School(school_code,sschoolname,sschoolemail,ssechoollocation,sschoolphone);
                    schoolsArray.add(obj);
                    schools_RecyclerView.setAdapter(schools_Adapter);
                    schools_Adapter.notifyDataSetChanged();
                    no_internet.setVisibility(View.GONE);
                    fetching_indicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<School> getSchoolNameFromDatabase(){
        return schoolsArray;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
