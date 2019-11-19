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
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Number_Verification extends Activity {

    private ImageView goBack_Image;
    private Button next_Button;
    private String sphone_number, sschool_code,
            transit_code, number_without_zero;
    private EditText phone_number_editText, code_one, code_two, code_three, code_four, code_five, code_six;
    private ProgressBar loading;
    private TextView status_message_textview;
    private DatabaseReference check_existance_reference;
    private Accessories number_verification_accessor;
    private LinearLayout password_layout;
    private FirebaseAuth mauth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

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
        code_one = findViewById(R.id.code_number_one);
        code_two = findViewById(R.id.code_number_two);
        code_three = findViewById(R.id.code_number_three);
        code_four = findViewById(R.id.code_number_four);
        code_five = findViewById(R.id.code_number_five);
        code_six = findViewById(R.id.code_number_six);

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
                number_without_zero = formated_number3.replace(" ","");
                sphone_number  = "0" + number_without_zero;
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
                    status_message_textview.setTextColor(getResources().getColor(R.color.colorAccent));
                    status_message_textview.setVisibility(View.VISIBLE);
                }
            }
        });

        // auto movement in code area
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
                    code_five.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        code_five.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(code_five.getText().toString().length()==1)     //size as per your requirement
                {
                    code_six.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        code_six.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(code_six.getText().toString().length()==1)     //size as per your requirement
                {
//                    dothelogin();
                    next_Button.setText("CONFIRM CODE");
                    next_Button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String thecode = code_one.getText().toString()+code_two.getText().toString()+
                                    code_three.getText().toString()+code_four.getText().toString()+code_five.getText().toString()+
                                    code_six.getText().toString();
                            if(isNetworkAvailable()){
                                verifyPhoneNumberWithCode(mVerificationId,thecode);
                            }else{
                                loading.setVisibility(View.GONE);
                                status_message_textview.setText("No internet connection");
                                status_message_textview.setTextColor(getResources().getColor(R.color.colorAccent));
                                status_message_textview.setVisibility(View.VISIBLE);
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

    private void verifyPhoneNumberWithCode(String verificationId,String theuserentered_code){
        loading.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, theuserentered_code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loading.setVisibility(View.GONE);
                            status_message_textview.setText("Verification complete");
                            status_message_textview.setTextColor(getResources().getColor(R.color.main_blue));
                            status_message_textview.setVisibility(View.VISIBLE);
                            Intent gotoVerificaton = new Intent(Number_Verification.this,Verify_School.class);
                            gotoVerificaton.putExtra("intent_school_code", sschool_code);
                            startActivity(gotoVerificaton);

                        } else {
                            // Sign in failed, display a message and update the UI
                            loading.setVisibility(View.GONE);
                            status_message_textview.setText("Login failed");
                            status_message_textview.setTextColor(getResources().getColor(R.color.colorAccent));
                            status_message_textview.setVisibility(View.VISIBLE);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                            }
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
                                        loading.setVisibility(View.GONE);
                                        status_message_textview.setText("Number validation complete");
                                        status_message_textview.setTextColor(getResources().getColor(R.color.colorAccent));
                                        status_message_textview.setVisibility(View.VISIBLE);

                                        Authentiate_the_parent_number(number_without_zero);
                                        transit_code = child.getKey();
//                                        Get_parent_registration_id(phone_number, child.getKey());
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }else{
                        loading.setVisibility(View.GONE);
                        status_message_textview.setText("Number is not registered with school");
                        status_message_textview.setTextColor(getResources().getColor(R.color.colorAccent));
                        status_message_textview.setVisibility(View.VISIBLE);
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

    private void Authentiate_the_parent_number(String phoneNumber) {

        loading.setVisibility(View.GONE);
        status_message_textview.setText("Sending code ...");
        status_message_textview.setTextColor(getResources().getColor(R.color.colorAccent));
        status_message_textview.setVisibility(View.VISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+233" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
        loading.setVisibility(View.VISIBLE);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verificaiton without
            //     user action.
//                signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            loading.setVisibility(View.GONE);


            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request

            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded

            }

            // Show a message and update the UI

        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            loading.setVisibility(View.GONE);
            status_message_textview.setText("Code has been sent");
            status_message_textview.setTextColor(getResources().getColor(R.color.colorAccent));
            status_message_textview.setVisibility(View.VISIBLE);
            Get_parent_registration_id(sphone_number, transit_code);

            password_layout.setVisibility(View.VISIBLE);
            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;

            next_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!code_one.equals("") && !code_two.equals("") && !code_three.equals("") && !code_four.equals("") && !code_five.equals("") && !code_six.equals("")){

                    }else{
                        loading.setVisibility(View.GONE);
                        status_message_textview.setText("Code required");
                        status_message_textview.setTextColor(getResources().getColor(R.color.colorAccent));
                        status_message_textview.setVisibility(View.VISIBLE);                    }
                }
            });
        }
    };

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
                            number_verification_accessor.put("user_phone_number", phone_number);
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
