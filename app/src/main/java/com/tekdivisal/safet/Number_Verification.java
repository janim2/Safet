package com.tekdivisal.safet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Number_Verification extends Activity {

    private ImageView goBack_Image;
    private Button next_Button;
    private String sphone_number, sschool_code,sregistered_number;
    private EditText phone_number_editText;
    private ProgressBar loading;
    private TextView status_message_textview;
    private DatabaseReference check_existance_reference;
    private Accessories number_verification_accessor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number__verification);

        goBack_Image = findViewById(R.id.back);
        next_Button = findViewById(R.id.next_button);
        phone_number_editText = findViewById(R.id.user_phone_number);
        loading = findViewById(R.id.loading);
        status_message_textview = findViewById(R.id.status_message);

        number_verification_accessor = new Accessories(Number_Verification.this);

        //formating phone number
        phone_number_editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //stings
        sschool_code = number_verification_accessor.getString("school_code");

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
                sphone_number = phone_number_editText.getText().toString().trim();
                if(!sphone_number.equals("")){
                     Check_for_user_registration(sphone_number);
                }else{
                    status_message_textview.setText("Number required");
                    status_message_textview.setTextColor(getResources().getColor(R.color.main_blue));
                    status_message_textview.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void Check_for_user_registration(final String phone_number, String key){
        loading.setVisibility(View.VISIBLE);
        //checking if user exists in database

        check_existance_reference = FirebaseDatabase.getInstance().getReference("isRegistered").child(phone_number).child(key);
        check_existance_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("school_code")){
                            sregistered_number = child.getValue().toString();
                            Toast.makeText(Number_Verification.this, sregistered_number,Toast.LENGTH_LONG).show();
                            if(sregistered_number.equals("0"+phone_number)){
                                Toast.makeText(Number_Verification.this, sregistered_number,Toast.LENGTH_LONG).show();
                            }else{
                                loading.setVisibility(View.GONE);
                                status_message_textview.setText("Number isn't registered with school");
                                status_message_textview.setTextColor(getResources().getColor(R.color.main_blue));
                                status_message_textview.setVisibility(View.VISIBLE);
                            }
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
