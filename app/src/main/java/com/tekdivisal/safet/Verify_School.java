package com.tekdivisal.safet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Random;

public class Verify_School extends Activity {

    private ImageView goback;
    private EditText code_one,code_two,code_three,code_four;
    private Button nextbutton;
    private ProgressBar loading;
    private TextView status_message;
    private String school_code, parent_code, parent_fname, parent_lname, parent_email, parent_location;
    private Accessories verify_school_accesssrs;
    private DatabaseReference addto__Dataabse_ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify__school);

        goback = findViewById(R.id.back);

        //initialization of accessor
        verify_school_accesssrs = new Accessories(Verify_School.this);

        //code editTexts
        code_one = findViewById(R.id.code_number_one);
        code_two = findViewById(R.id.code_number_two);
        code_three = findViewById(R.id.code_number_three);
        code_four = findViewById(R.id.code_number_four);
        nextbutton = findViewById(R.id.next_button);
        loading = findViewById(R.id.loading);
        status_message = findViewById(R.id.status_message);

        school_code = verify_school_accesssrs.getString("school_code");
        parent_code = verify_school_accesssrs.getString("user_phone_number");

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    finish();
                }
            }
        });

//        nextbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String scode_one = code_one.getText().toString();
//                String scode_two = code_two.getText().toString();
//                String scode_three = code_three.getText().toString();
//                String scode_four = code_four.getText().toString();
//                if(!scode_one.equals("") && !scode_two.equals("") && !scode_three.equals("")
//                        && !scode_four.equals("")){
//                    String full_code = scode_one + scode_two + scode_three + scode_four;
//                    if(isNetworkAvailable()){
//                        loading.setVisibility(View.VISIBLE);
////                                    verification complete state here
//                        if(full_code.equals(school_code)){
//                            verify_school_accesssrs.put("isverified", true);
//                            Intent gotoMain = new Intent(Verify_School.this, MainActivity.class);
//                            startActivity(gotoMain);
//                        }else{
//                            loading.setVisibility(View.GONE);
//                            status_message.setText("School verification failed");
//                            status_message.setTextColor(getResources().getColor(R.color.main_blue));
//                            status_message.setVisibility(View.VISIBLE);
//                        }
//                    }else{
//                        loading.setVisibility(View.GONE);
//                        status_message.setText("No internet connection");
//                        status_message.setTextColor(getResources().getColor(R.color.colorAccent));
//                        status_message.setVisibility(View.VISIBLE);                                }
//                }else{
//                    loading.setVisibility(View.GONE);
//                    status_message.setText("Code Required");
//                    status_message.setTextColor(getResources().getColor(R.color.colorAccent));
//                    status_message.setVisibility(View.VISIBLE);
//                }
//            }
//        });



        //setting auto jump for code editTexts
        code_one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(code_one.getText().toString().length()==1)     //size as per your requirement
                {
                    code_two.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        code_two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(code_two.getText().toString().length()==1)     //size as per your requirement
                {
                    code_three.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        code_three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(code_three.getText().toString().length()==1)     //size as per your requirement
                {
                    code_four.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        code_four.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(code_four.getText().toString().length()==1)     //size as per your requirement
                {
//                    dothelogin();
//                    next_button.setText("VERIFY");

                    nextbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String scode_one = code_one.getText().toString();
                            String scode_two = code_two.getText().toString();
                            String scode_three = code_three.getText().toString();
                            String scode_four = code_four.getText().toString();
                            if(!scode_one.equals("") && !scode_two.equals("") && !scode_three.equals("")
                                    && !scode_four.equals("")){
                                String full_code = scode_one + scode_two + scode_three + scode_four;
                                if(isNetworkAvailable()){
                                    loading.setVisibility(View.VISIBLE);
//                                    verification complete state here
                                    if(full_code.equals(school_code)){
                                        verify_school_accesssrs.put("isverified", true);
                                        Intent gotoMain = new Intent(Verify_School.this, MainActivity.class);
                                        startActivity(gotoMain);
                                        getUserInformation();
                                    }else{
                                        loading.setVisibility(View.GONE);
                                        status_message.setText("School verification failed");
                                        status_message.setTextColor(getResources().getColor(R.color.main_blue));
                                        status_message.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    loading.setVisibility(View.GONE);
                                    status_message.setText("No internet connection");
                                    status_message.setTextColor(getResources().getColor(R.color.colorAccent));
                                    status_message.setVisibility(View.VISIBLE);                                }
                            }else{
                                loading.setVisibility(View.GONE);
                                status_message.setText("Code Required");
                                status_message.setTextColor(getResources().getColor(R.color.colorAccent));
                                status_message.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void getUserInformation() {
        DatabaseReference get_parent_info = FirebaseDatabase.getInstance().getReference("parents").child(school_code).child(parent_code);
        get_parent_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){ //getting the parent details
                        if(child.getKey().equals("firstname")){
                            parent_fname = child.getValue().toString();
                            verify_school_accesssrs.put("parent_fname", parent_fname);
                        }
                        if(child.getKey().equals("lastname")){
                            parent_lname = child.getValue().toString();
                            verify_school_accesssrs.put("parent_lname", parent_lname);
                        }
                        if(child.getKey().equals("email")){
                            parent_email = child.getValue().toString();
                            verify_school_accesssrs.put("parent_email", parent_email);
                        }
                        if(child.getKey().equals("location")){
                            parent_location = child.getValue().toString();
                            verify_school_accesssrs.put("parent_location", parent_location);
                        }

                    }
                    addToNotifications(school_code, parent_code);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            finish();
        }
    }

    private void addToNotifications(String school_code, String parent_code) {
        try {
            Random random = new Random();
            int a = random.nextInt(987654);
            String notificationID = "notification" + a+"";
            addto__Dataabse_ref = FirebaseDatabase.getInstance().getReference("notifications").child(school_code).child(parent_code).child(notificationID);
            addto__Dataabse_ref.child("image").setValue("WM");
            addto__Dataabse_ref.child("message").setValue("Welcome to Safet. The most secure bus tracker system for your school. Your kids safety is our number one concern");
            addto__Dataabse_ref.child("title").setValue("Welcome to Safet");
            addto__Dataabse_ref.child("time").setValue(new Date().toString());
        }catch (NullPointerException e){

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}