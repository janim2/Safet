package com.tekdivisal.safet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Login extends Activity {
    private TextView phone_numberEdit_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone_numberEdit_Text = findViewById(R.id.phone_number_editText);

//        making phone number editText clickable
        phone_numberEdit_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Number_Verification.class));
            }
        });
    }
}
