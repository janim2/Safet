package com.tekdivisal.safet;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    private TextView parent_name_Textview, parent_phone_Textview, parent_email_Textview, parent_locationTextView;
    private String sparent_name,sparent_phone, sparent_email, sparent_location;
    private String parent_fname, parent_lname, parent_email, parent_location,school_id_string ,phone_number_string;
    private Accessories profile_accessor;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profile =  inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle("Profile");
        profile_accessor = new Accessories(getActivity());

        parent_name_Textview = profile.findViewById(R.id.parent_name);
        parent_phone_Textview = profile.findViewById(R.id.parent_number);
        parent_email_Textview = profile.findViewById(R.id.parent_email);
        parent_locationTextView = profile.findViewById(R.id.parent_location);

        sparent_name = profile_accessor.getString("parent_fname") + " " + profile_accessor.getString("parent_lname");
        sparent_email = profile_accessor.getString("parent_email");
        sparent_phone = profile_accessor.getString("user_phone_number");
        sparent_location = profile_accessor.getString("parent_location");


        school_id_string = profile_accessor.getString("school_code");
        phone_number_string = profile_accessor.getString("user_phone_number");

        Toast.makeText(getActivity(), sparent_email, Toast.LENGTH_LONG).show();

        try {
            parent_name_Textview.setText(sparent_name);
            parent_phone_Textview.setText(sparent_phone);
            parent_email_Textview.setText(sparent_email);
            parent_locationTextView.setText(sparent_location);
        }catch (NullPointerException e){

        }

        if(sparent_email.equals("")){
            getUserInformation();
        }
        return profile;
    }

    private void getUserInformation() {
        DatabaseReference get_parent_info = FirebaseDatabase.getInstance().getReference("parents").child(school_id_string).child(phone_number_string);
        get_parent_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){ //getting the parent details
                        if(child.getKey().equals("firstname")){
                            parent_fname = child.getValue().toString();
                            profile_accessor.put("parent_fname", parent_fname);
                        }
                        if(child.getKey().equals("lastname")){
                            parent_lname = child.getValue().toString();
                            profile_accessor.put("parent_lname", parent_lname);
                        }
                        if(child.getKey().equals("email")){
                            parent_email = child.getValue().toString();
                            profile_accessor.put("parent_email", parent_email);
                        }
                        if(child.getKey().equals("location")){
                            parent_location = child.getValue().toString();
                            profile_accessor.put("parent_location", parent_location);
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

