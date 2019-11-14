package com.tekdivisal.safet;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    private TextView parent_name_Textview, parent_phone_Textview, parent_email_Textview, parent_locationTextView;
    private String sparent_name,sparent_phone, sparent_email, sparent_location;
    private Accessories profile_accessor;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profile =  inflater.inflate(R.layout.fragment_profile, container, false);

        profile_accessor = new Accessories(getActivity());

        parent_name_Textview = profile.findViewById(R.id.parent_name);
        parent_phone_Textview = profile.findViewById(R.id.parent_number);
        parent_email_Textview = profile.findViewById(R.id.parent_email);
        parent_locationTextView = profile.findViewById(R.id.parent_location);

        sparent_name = profile_accessor.getString("parent_fname") + "" + profile_accessor.getString("parent_lname");
        sparent_email = profile_accessor.getString("parent_email");
        sparent_phone = profile_accessor.getString("user_phone_number");
        sparent_location = profile_accessor.getString("parent_location");

        try {
            parent_phone_Textview.setText(sparent_name);
            parent_phone_Textview.setText(sparent_phone);
            parent_email_Textview.setText(sparent_email);
            parent_locationTextView.setText(sparent_location);
        }catch (NullPointerException e){

        }


        return profile;
    }

}

