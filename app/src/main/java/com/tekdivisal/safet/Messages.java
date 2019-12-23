package com.tekdivisal.safet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Messages extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        getSupportActionBar().setTitle("Messages");
    }
}
