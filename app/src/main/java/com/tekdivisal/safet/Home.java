package com.tekdivisal.safet;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tekdivisal.safet.Adapters.Facilities_Adapter;
import com.tekdivisal.safet.Adapters.image_slider_adapter;
import com.tekdivisal.safet.Model.Facilities;
import com.tekdivisal.safet.PiccassoImageProcessor.PicassoImageLoadingService;

import java.util.ArrayList;

import ss.com.bannerslider.Slider;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private Slider slider;
    private TextView school_nameTextView;
    private Accessories homeaccessor;

    //facilities items
    private ArrayList facilitiesArray = new ArrayList<Facilities>();
    private RecyclerView facilities_RecyclerView;
    private RecyclerView.Adapter facilities_Adapter;
    private String school_id,facility_id,facility_name, facility_image,
            image_one_image, image_two_image, image_three_image, language_string, range_string, school_logo_string,
            mission_string, vision_string, admission_status_string;
    private TextView no_facilities, facilies_no_internet, language_range_textview, mission_text,
            vision_text, admission_status;

    private ImageView school_logo;
    private LinearLayout mission_layout, vision_layout;

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View unverified =  inflater.inflate(R.layout.fragment_home, container, false);
        homeaccessor = new Accessories(getActivity());

        school_id = homeaccessor.getString("school_code");

        Slider.init(new PicassoImageLoadingService(getActivity()));
        slider = unverified.findViewById(R.id.banner_slider1);
        slider.setAdapter(new image_slider_adapter(image_one_image, image_two_image, image_three_image));

         school_nameTextView = unverified.findViewById(R.id.school_name);

         school_nameTextView.setText(homeaccessor.getString("school_name"));

        //        facilities initializations
        facilities_RecyclerView = unverified.findViewById(R.id.facilities_recyclerView);
        facilies_no_internet = unverified.findViewById(R.id.facilities_no_internet);
        no_facilities = unverified.findViewById(R.id.no_facilities);

        language_range_textview = unverified.findViewById(R.id.language_range);
        mission_text = unverified.findViewById(R.id.mission_text);
        vision_text = unverified.findViewById(R.id.vision_text);
        admission_status = unverified.findViewById(R.id.admission_status);
        school_logo = unverified.findViewById(R.id.school_logo);

        //layouts
        mission_layout = unverified.findViewById(R.id.mission_layout);
        vision_layout = unverified.findViewById(R.id.vision_layout);

        facilities_RecyclerView.setHasFixedSize(true);
        facilities_Adapter = new Facilities_Adapter(getFacilitiesFromDatabase(),getActivity());
        facilities_RecyclerView.setAdapter(facilities_Adapter);

        facilies_no_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    Fetch_Image_One();
//                    Fetch_Facilities_IDS();
                }else{
                    facilies_no_internet.setVisibility(View.VISIBLE);
                    no_facilities.setVisibility(View.GONE);
//                    mission_text.setText("No internet connection");
//                    vision_text.setText("No internet connection");
                }
            }
        });
        return  unverified;
    }


    private void Fetch_Facilities_IDS() {
        facilies_no_internet.setVisibility(View.GONE);
        try{
            DatabaseReference get_faciility_urlID = FirebaseDatabase.getInstance().getReference("images").child("facilities").child(school_id);//.child(mauth.getCurrentUser().getUid());
            get_faciility_urlID.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Fetch_Facility_Url(child.getKey());
                        }
                    }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_Facility_Url(final String key) {
        DatabaseReference getFacilityUrls = FirebaseDatabase.getInstance().getReference("images").child("facilities").child(school_id).child(key);
        getFacilityUrls.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("image")){
                            facility_image = child.getValue().toString();
                        }
                        if(child.getKey().equals("facility_name")){
                            facility_name = child.getValue().toString();
                        }
                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                        }
                    }

                    Facilities obj = new Facilities(key,facility_name, facility_image);
                    facilitiesArray.add(obj);
                    facilities_RecyclerView.setAdapter(facilities_Adapter);
                    facilities_Adapter.notifyDataSetChanged();
                    no_facilities.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

            }
        });
    }

    private void Fetch_Image_One() {
        try {
            DatabaseReference image_one = FirebaseDatabase.getInstance().getReference("images").child("school_images").child(school_id).child("image_one");
            image_one.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("image")){
                                image_one_image = child.getValue().toString();
                                if(!image_one_image.equals("")){
                                }else{
                                    image_one_image = "https://www.cdnis.edu.hk/sites/default/files/styles/1200x795/public/CDNIS%20solar%20panel_1.jpg?itok=qCVODOAq";
                                }
                            }
                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                            }
                        }
                        Fetch_Image_Two();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_Image_Two() {
        try {
            DatabaseReference image_two = FirebaseDatabase.getInstance().getReference("images").child("school_images").child(school_id).child("image_two");
            image_two.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("image")){
                                image_two_image = child.getValue().toString();
                                if(!image_two_image.equals("")){
                                }else{
                                    image_two_image = "https://via.placeholder.com/550/FFFFFF/808080%20?Text=Digital.comC/O%20https://placeholder.com/";
                                }
                            }

                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                            }
                        }
                        Fetch_Image_Three();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_Image_Three() {
        try {
            DatabaseReference image_three = FirebaseDatabase.getInstance().getReference("images").child("school_images").child(school_id).child("image_three");
            image_three.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("image")){
                                image_three_image = child.getValue().toString();
                                if(!image_three_image.equals("")){
                                }else{
                                    image_three_image = "https://via.placeholder.com/550/FFFFFF/808080%20?Text=Digital.comC/O%20https://placeholder.com/";
                                }
                            }

                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                            }
                        }
                        slider.setAdapter(new image_slider_adapter(image_one_image, image_two_image, image_three_image));
                        Fetch_Facilities_IDS();
                        Fetch_school_logo();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_school_logo() {
        try {
            DatabaseReference image_three = FirebaseDatabase.getInstance().getReference("images").child("school_images").child(school_id).child("logo");
            image_three.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("image")){
                                school_logo_string = child.getValue().toString();
                                if(!school_logo_string.equals("")){
                                    Picasso.with(getActivity()).load(Uri.parse(school_logo_string)).into(school_logo);
                                }else{
                                    image_three_image = "https://via.placeholder.com/550/FFFFFF/808080%20?Text=Digital.comC/O%20https://placeholder.com/";
                                }
                            }

                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_lang_and_status() {
        try {
            DatabaseReference get_lang = FirebaseDatabase.getInstance().getReference("schools")
                    .child(school_id);
            get_lang.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("language")){
                                language_string = child.getValue().toString();
                            }

                            if(child.getKey().equals("range")){
                                range_string = child.getValue().toString();
                                if(range_string.equals("")){
                                    language_range_textview.setText("None");
                                }else{
                                    language_range_textview.setText(language_string + " | " + range_string);
                                }
                            }

                            if(child.getKey().equals("mission")){
                                mission_string = child.getValue().toString();
                                if(mission_string.equals("")){
                                    mission_text.setText("None");
                                }else{
                                    mission_layout.setVisibility(View.VISIBLE);
                                    mission_text.setText(mission_string);
                                }
                            }

                            if(child.getKey().equals("vision")){
                                vision_string = child.getValue().toString();
                                if(vision_string.equals("")){
                                    vision_text.setText("None");
                                }else{
                                    vision_layout.setVisibility(View.VISIBLE);
                                    vision_text.setText(vision_string);
                                }
                            }

                            if(child.getKey().equals("admission_status")){
                                admission_status_string = child.getValue().toString();
                                if(admission_status_string.equals("")){
                                    admission_status.setText("Close");
                                }else{
                                    admission_status.setText(admission_status_string);
                                }
                            }

                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){

        }
    }

    public ArrayList<Facilities> getFacilitiesFromDatabase(){
        return  facilitiesArray;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isNetworkAvailable()){
            Fetch_Image_One();
            Fetch_lang_and_status();
        }else{
            facilies_no_internet.setVisibility(View.VISIBLE);
            no_facilities.setVisibility(View.GONE);
        }

    }
}
