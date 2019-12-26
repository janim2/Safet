package com.tekdivisal.safet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Change_password extends AppCompatActivity {
    private String parent_code,user_password_,current_password_string, new_password_string,confirm_password_string;
    private LinearLayout change_password_layout,create_password_layout;
    private EditText current_password,create_new_password,change_new_password,
            create_confirm_password,change_confirm_password;
    private TextView do_password_text, create_success_message, change_success_message, already_have_password;
    private Accessories changePassword_accessor;
    private Button create_password_button, change_password_button;
    private ProgressBar create_progressBar, change_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle("Password");

        changePassword_accessor = new Accessories(Change_password.this);

        parent_code = changePassword_accessor.getString("user_phone_number");

        do_password_text = findViewById(R.id.do_indicator);
        change_password_layout = findViewById(R.id.change_password_layout);
        create_password_layout = findViewById(R.id.create_password_layout);

        //create password staff
        create_new_password = findViewById(R.id.create_password_editText);
        create_confirm_password = findViewById(R.id.create_confirm_password_editText);
        create_success_message = findViewById(R.id.create_success_message);
        create_progressBar = findViewById(R.id.create_loading);
        create_password_button = findViewById(R.id.create_button);

        already_have_password = findViewById(R.id.already_have_password);


        //change password staff
        current_password = findViewById(R.id.current_password);
        change_new_password = findViewById(R.id.change_newPassword_editText);
        change_confirm_password = findViewById(R.id.change_confirm_editText);
        change_success_message = findViewById(R.id.change_success_message);
        change_progressBar = findViewById(R.id.change_loading);
        change_password_button = findViewById(R.id.change_button);

        if(changePassword_accessor.getBoolean("isPasswordCreated")){
            create_password_layout.setVisibility(View.GONE);
            change_password_layout.setVisibility(View.VISIBLE);
            do_password_text.setText("Change");
        }else{
//            create_password_layout.setVisibility(View.VISIBLE);
            do_password_text.setText("Create");
        }

        already_have_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword_accessor.put("isPasswordCreated", true);
                finish();
                Toast.makeText(Change_password.this, "Enter password to access child details",
                        Toast.LENGTH_LONG).show();
            }
        });

        create_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_password_string = create_new_password.getText().toString().trim();
                confirm_password_string = create_confirm_password.getText().toString().trim();
                if(!new_password_string.equals("") && !confirm_password_string.equals("") ){
                    if(isNetworkAvailable()){
                        if(new_password_string.equals(confirm_password_string)){
                            CreatePassword(new_password_string);
                        }else{
                            create_progressBar.setVisibility(View.GONE);
                            create_success_message.setText("Password mismatch");
                            create_success_message.setVisibility(View.VISIBLE);
                        }
                    }else{
                        create_progressBar.setVisibility(View.GONE);
                        create_success_message.setText("No internet connection");
                        create_success_message.setVisibility(View.VISIBLE);
                    }
                }else{
                    create_progressBar.setVisibility(View.GONE);
                    create_success_message.setText("All fields required");
                    create_success_message.setVisibility(View.VISIBLE);
                }
            }
        });

        change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_password_string = current_password.getText().toString().trim();
                new_password_string = change_new_password.getText().toString().trim();
                confirm_password_string = change_confirm_password.getText().toString().trim();
                if(!current_password_string.equals("") && !new_password_string.equals("")
                        && !confirm_password_string.equals("")){
                    if(user_password_ != null){
                        if(current_password_string.equals(user_password_)){
                            if(new_password_string.equals(confirm_password_string)){
                                if(isNetworkAvailable()){
                                    Change_the_password(new_password_string);
                                }else{
                                    create_progressBar.setVisibility(View.GONE);
                                    create_success_message.setText("No internet connection");
                                    create_success_message.setVisibility(View.VISIBLE);
                                }
                            }else{
                                create_progressBar.setVisibility(View.GONE);
                                create_success_message.setText("Password mismatch");
                                create_success_message.setVisibility(View.VISIBLE);
                            }
                        }else{
                                create_progressBar.setVisibility(View.GONE);
                                create_success_message.setText("Current password invalid");
                                create_success_message.setVisibility(View.VISIBLE);
                        }
                    }
                }else{
                    create_progressBar.setVisibility(View.GONE);
                    create_success_message.setText("All fields required");
                    create_success_message.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void CreatePassword(String new_password_string) {
        create_progressBar.setVisibility(View.VISIBLE);
        create_success_message.setVisibility(View.GONE);
        try {
            DatabaseReference create_password = FirebaseDatabase.getInstance().getReference("passwords").child(parent_code);
            create_password.child("password").setValue(new_password_string);
            create_progressBar.setVisibility(View.GONE);
            create_success_message.setText("Successfully created password");
            create_success_message.setVisibility(View.VISIBLE);
            changePassword_accessor.put("isPasswordCreated", true);
        }catch (NullPointerException e){

        }
    }

    private void Change_the_password(String new_password_string) {
        create_progressBar.setVisibility(View.VISIBLE);
        create_success_message.setVisibility(View.GONE);
        try {
            DatabaseReference create_password = FirebaseDatabase.getInstance().getReference("passwords").child(parent_code);
            create_password.child("password").setValue(new_password_string);
            create_progressBar.setVisibility(View.GONE);
            create_success_message.setText("Successfully changed password");
            create_success_message.setVisibility(View.VISIBLE);
        }catch (NullPointerException e){

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(changePassword_accessor.getBoolean("isPasswordCreated")){
            if(isNetworkAvailable()){
                Get_current_password();
            }else{
                Toast.makeText(Change_password.this, "No internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void Get_current_password() {
        try {
            DatabaseReference get_password = FirebaseDatabase.getInstance().getReference("passwords").child(parent_code);
            get_password.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("password")){
                                user_password_ = child.getValue().toString();
                            }
                            else{
//                              Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Change_password.this,"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
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
