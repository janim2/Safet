package com.tekdivisal.safet;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class About extends Fragment {

    private Accessories about_accessor;
    private String school_code,parent_code,sschoolemail, ssechoollocation,sschoolname,sschoolphone;
    private TextView the_school_name_tv, the_school_number_tv, the_school_email_tv, the_school_address_tv;
    private EditText subject_eText, message_eText;
    private Button submit;
    private String subject_string, message_string;

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

        //editText
        subject_eText = about.findViewById(R.id.subject);
        message_eText = about.findViewById(R.id.message);

        //buttons
        submit = about.findViewById(R.id.submit_button);

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

        //setting onclick for the submit button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject_string = subject_eText.getText().toString().trim();
                message_string = message_eText.getText().toString().trim();
                if(!subject_string.equals("") && !message_string.equals("")){
                    if(isNetworkAvailable()){
                        add_message(subject_string, message_string);
                    }else{
                        Toast.makeText(getActivity(), "No internet connection",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Fields required",Toast.LENGTH_LONG).show();
                }
            }
        });
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

    private void add_message(String subject_string, String message_string) {
        Random radn = new Random();
        int add = radn.nextInt(455);
        int otehr = radn.nextInt(33);
        String message_code = school_code+otehr+""+"me"+add+"";
        DatabaseReference add_message_reference = FirebaseDatabase.getInstance().getReference("messages")
                .child(school_code).child(parent_code).child(message_code);
        add_message_reference.child("subject").setValue(subject_string);
        add_message_reference.child("message").setValue(message_string).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(),"Message sent",Toast.LENGTH_LONG).show();
                subject_eText.setText("");
                message_eText.setText("");
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
