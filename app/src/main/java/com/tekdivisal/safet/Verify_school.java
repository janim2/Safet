package com.tekdivisal.safet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

public class Verify_school extends Activity {

    private ImageView goback;
    private EditText code_one,code_two,code_three,code_four;
    private Button nextbutton;
    private ProgressBar loading;
    private TextView status_message;
    private String school_code;
    private Accessories verify_school_accesssrs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__last_step);

        goback = findViewById(R.id.back);

        //initialization of accessor
        verify_school_accesssrs = new Accessories(Verify_school.this);

        //code editTexts
        code_one = findViewById(R.id.code_number_one);
        code_two = findViewById(R.id.code_number_two);
        code_three = findViewById(R.id.code_number_three);
        code_four = findViewById(R.id.code_number_four);
        nextbutton = findViewById(R.id.next_button);
        loading = findViewById(R.id.loading);
        status_message = findViewById(R.id.status_message);

        school_code = verify_school_accesssrs.getString("school_code");


        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                            Intent gotoMain = new Intent(Verify_school.this, MainActivity.class);
                            startActivity(gotoMain);
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
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
