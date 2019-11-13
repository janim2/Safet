package com.tekdivisal.safet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Number_Verification extends Activity {

    private ImageView goBack_Image;
    private Button next_Button;
    private String sphone_number, sschool_code,sregistered_number,spassword, sconfirmpassword;
    private EditText phone_number_editText, password_edditText, confitm_password_editText;
    private ProgressBar loading, password_loading;
    private TextView status_message_textview, password_success_message;
    private DatabaseReference check_existance_reference;
    private Accessories number_verification_accessor;
    private LinearLayout password_layout;
    private FirebaseAuth mauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number__verification);

        mauth = FirebaseAuth.getInstance();

        goBack_Image = findViewById(R.id.back);
        next_Button = findViewById(R.id.next_button);
        phone_number_editText = findViewById(R.id.user_phone_number);
        loading = findViewById(R.id.loading);
        status_message_textview = findViewById(R.id.status_message);
        password_layout = findViewById(R.id.password_layout);
        password_edditText = findViewById(R.id.password_editText);
        confitm_password_editText = findViewById(R.id.confirm_password_editText);
        password_loading = findViewById(R.id.password_loading);
        password_success_message = findViewById(R.id.password_success_message);

        number_verification_accessor = new Accessories(Number_Verification.this);

        //formating phone number
        phone_number_editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //stings
//        sschool_code = number_verification_accessor.getString("school_code");

//        makng goBack clickable
        goBack_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        making next button clickable
        next_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number_from_editText = phone_number_editText.getText().toString().trim();
                String formated_number = number_from_editText.replace("(","");
                String formated_number2 = formated_number.replace(")","");
                String formated_number3 = formated_number2.replace("-","");
                sphone_number  = "0" + formated_number3.replace(" ","");
                Toast.makeText(Number_Verification.this, sphone_number,Toast.LENGTH_LONG).show();
                if(!sphone_number.equals("")){
                    if(isNetworkAvailable()){
                        Check_for_user_existance(sphone_number);
                    }else{
                        loading.setVisibility(View.GONE);
                        status_message_textview.setText("No internet connection");
                        status_message_textview.setTextColor(getResources().getColor(R.color.main_blue));
                        status_message_textview.setVisibility(View.VISIBLE);
                    }
                }else{
                    status_message_textview.setText("Number required");
                    status_message_textview.setTextColor(getResources().getColor(R.color.main_blue));
                    status_message_textview.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void Check_for_user_existance(final String phone_number) {
        try{
            loading.setVisibility(View.VISIBLE);
            status_message_textview.setVisibility(View.GONE);
            check_existance_reference = FirebaseDatabase.getInstance().getReference("isRegistered");


            check_existance_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(phone_number)){
//                        for(DataSnapshot child : dataSnapshot.getChildren()){
//                            if(child.getKey() != null) {
//                                Toast.makeText(Number_Verification.this, "key" + child.getKey(), Toast.LENGTH_LONG).show();
//                                Get_for_user_school(phone_number, child.getKey());
//                            }
//                        }
                        final DatabaseReference get_Registration_ids = FirebaseDatabase.getInstance().getReference("isRegistered").child(phone_number);
                        get_Registration_ids.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot child : dataSnapshot.getChildren()){
                                        Get_parent_registration_id(phone_number, child.getKey());
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }else{
                    Toast.makeText(Number_Verification.this,"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Number_Verification.this,"Cancelled",Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Get_parent_registration_id(final String phone_number, String key) {
        DatabaseReference get_parent_id = FirebaseDatabase.getInstance().getReference("isRegistered").child(phone_number).child(key);
        get_parent_id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){ //getting the school code
                        if(child.getKey().equals("school_code")){
                            sschool_code = child.getValue().toString();
                            number_verification_accessor.put("school_code",sschool_code);
                            loading.setVisibility(View.GONE);

                            //verification message
                            status_message_textview.setText("Verification successful");
                            status_message_textview.setTextColor(getResources().getColor(R.color.main_blue));
                            status_message_textview.setVisibility(View.VISIBLE);
                            password_layout.setVisibility(View.VISIBLE);

                            //setting new button onclick
                            next_Button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    spassword = password_edditText.getText().toString().trim();
                                    sconfirmpassword = confitm_password_editText.getText().toString().trim();

                                    if(spassword.equals(sconfirmpassword)){
                                        SIgn_user_in_with_details(phone_number, spassword, sconfirmpassword);
                                    }
                                    else{
                                        loading.setVisibility(View.GONE);
                                        status_message_textview.setText("Password mismatch");
                                        status_message_textview.setTextColor(getResources().getColor(R.color.main_blue));
                                        status_message_textview.setVisibility(View.VISIBLE);
                                    }

                                }
                            });

                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SIgn_user_in_with_details(String phone_number, String spassword, String sconfirmpassword) {
        mauth.signInWithEmailAndPassword(sschool_email,spassword).addOnCompleteListener(Admin_Login.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    loading.setVisibility(View.GONE);
                    success_message.setVisibility(View.VISIBLE);
                    success_message.setTextColor(getResources().getColor(R.color.red));
                    success_message.setText("Login failed");
                }else{
                    loading.setVisibility(View.GONE);
                    success_message.setVisibility(View.VISIBLE);
                    success_message.setTextColor(getResources().getColor(R.color.green));
                    success_message.setText("Login successful");
                    Intent gotoVerification = new Intent(Admin_Login.this,Verify_School.class);
//                                        gotoVerification.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(gotoVerification);
                }
            }
        });
    }

    public void Get_for_user_school(final String phone_number, String key){
        //checking if user exists in database

        check_existance_reference = FirebaseDatabase.getInstance().getReference("isRegistered").child(phone_number).child(key);
        check_existance_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("school_code")){
                            sschool_code = child.getValue().toString();
                            number_verification_accessor.put("school_code",sschool_code);
                            loading.setVisibility(View.GONE);
                        }
                    }

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
