package com.tekdivisal.safet;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdivisal.safet.Adapters.ChildrenAdapter;
import com.tekdivisal.safet.Adapters.ProfileChildrenAdapter;
import com.tekdivisal.safet.Helpers.HelperClass;
import com.tekdivisal.safet.Model.Children;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    private TextView parent_name_Textview, parent_phone_Textview, parent_email_Textview, parent_locationTextView;
    private String sparent_name,sparent_phone, sparent_email, sparent_location, child_first_name, child_last_name,
    child_gender, child_class, schild_code, is_assigned, the_assigned_bus;
    private String parent_fname, parent_lname, parent_email, parent_location,school_id_string ,phone_number_string;
    private Accessories profile_accessor;
    private ImageView edit_;


    //fetching child information from database
    private ArrayList childrenArray = new ArrayList<Children>();
    private RecyclerView children_RecyclerView;
    private RecyclerView.Adapter children_Adapter;

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
        edit_ = profile.findViewById(R.id.edit_);

        sparent_name = profile_accessor.getString("parent_fname") + " " + profile_accessor.getString("parent_lname");
        sparent_email = profile_accessor.getString("parent_email");
        sparent_phone = profile_accessor.getString("user_phone_number");
        sparent_location = profile_accessor.getString("parent_location");


        school_id_string = profile_accessor.getString("school_code");
        phone_number_string = profile_accessor.getString("user_phone_number");

//        Toast.makeText(getActivity(), sparent_email, Toast.LENGTH_LONG).show();

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

        //fetching children from database
        children_RecyclerView = profile.findViewById(R.id.child_recyclerView);
        if(new HelperClass(getActivity()).isNetworkAvailable()){
            getParent_children();
        }else{
            Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
        }

        children_RecyclerView.setHasFixedSize(true);
        children_RecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        children_Adapter = new ProfileChildrenAdapter(getFromDatabase(),getActivity());
        children_RecyclerView.setAdapter(children_Adapter);

        edit_.setOnClickListener(v -> {

            AlertDialog.Builder makechanges = new AlertDialog.Builder(getActivity(), R.style.Myalert);
            makechanges.setTitle("Edit")
                    .setIcon(android.R.drawable.ic_menu_edit)
                    .setMessage("Contact school administration to correct any mistakes with your details.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            makechanges.show();
        });

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
                        if(child.getKey().equals("the_email")){
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

    private void getParent_children() {
        try {
            DatabaseReference children = FirebaseDatabase.getInstance().getReference("children")
                    .child(school_id_string).child(sparent_phone);
            children.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Fetch_Child_info(child.getKey());
                        }
                    }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_Child_info(String key) {
        try{
            DatabaseReference childinfo = FirebaseDatabase.getInstance().getReference("children")
                    .child(school_id_string).child(sparent_phone).child(key);
            childinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("firstname")){
                                child_first_name = child.getValue().toString();
                            }
                            if(child.getKey().equals("lastname")){
                                child_last_name = child.getValue().toString();
                            }
                            if(child.getKey().equals("class")){
                                child_class = child.getValue().toString();
                            }
                            if(child.getKey().equals("gender")){
                                child_gender = child.getValue().toString();
                            }
                            if(child.getKey().equals("isAssigned_bus")){
                                is_assigned = child.getValue().toString();
                            }
                            if(child.getKey().equals("assigned_bus")){
                                the_assigned_bus = child.getValue().toString();
                            }

                        }
                        schild_code = key;
                        Children obj = new Children(sparent_phone,child_first_name,child_last_name,
                                child_class,child_gender, schild_code, is_assigned, the_assigned_bus);
                        childrenArray.add(obj);
                        children_RecyclerView.setAdapter(children_Adapter);
                        children_Adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){

        }
    }

    private ArrayList<Children> getFromDatabase() {
        return childrenArray;
    }


}

