package com.tekdivisal.safet;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class About extends Fragment {

    private Accessories about_accessor;
    private String school_code,parent_code,sschoolemail, ssechoollocation,sschoolname,sschoolphone;
    private TextView the_school_name_tv, the_school_number_tv, the_school_email_tv, the_school_address_tv;

    public About() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View about =  inflater.inflate(R.layout.fragment_about, container, false);
        getActivity().setTitle("About");

        about_accessor =  new Accessories(getActivity());

        //textviews
        the_school_name_tv = about.findViewById(R.id.the_school_name);
        the_school_number_tv = about.findViewById(R.id.the_school_number);
        the_school_email_tv = about.findViewById(R.id.the_school_email);
        the_school_address_tv = about.findViewById(R.id.the_school_location);

        //vitals

        school_code = about_accessor.getString("school_code");
        parent_code = about_accessor.getString("user_phone_number");

        sschoolname = about_accessor.getString("school_name");
        sschoolphone = about_accessor.getString("school_telephone");
        sschoolemail = about_accessor.getString("school_email");
        ssechoollocation = about_accessor.getString("school_location");

        if(sschoolname.equals("") || sschoolphone.equals("") || sschoolemail.equals("") || ssechoollocation.equals("")){
            getSchoolinformation(school_code);
        }else{
            the_school_name_tv.setText(sschoolname);
            the_school_email_tv.setText(sschoolemail);
            the_school_number_tv.setText(sschoolphone);
            the_school_address_tv.setText(ssechoollocation);
        }
        return about;
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
                            about_accessor.put("school_email",sschoolemail);
                        }
                        if(child.getKey().equals("location")){
                            ssechoollocation = child.getValue().toString();
                            about_accessor.put("school_location",ssechoollocation);
                        }
                        if(child.getKey().equals("name")){
                            sschoolname = child.getValue().toString();
                            about_accessor.put("school_name",sschoolname);
                        }
                        if(child.getKey().equals("telephone")){
                            sschoolphone = child.getValue().toString();
                            about_accessor.put("school_telephone",sschoolphone);
                        }
                    }
                    the_school_name_tv.setText(sschoolname);
                    the_school_email_tv.setText(sschoolemail);
                    the_school_number_tv.setText(sschoolphone);
                    the_school_address_tv.setText(ssechoollocation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
