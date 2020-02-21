package com.tekdivisal.safet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Random;

public class Child_Details extends AppCompatActivity {

    private TextView child_fname_textview, child_lname_textview, child_gender_textview, child_class_textview;
    private EditText child_fname_edittext, child_lname_edittext, child_class_edittext;
    private Spinner child_gender_spinner;

    private String parent_code_string,child_code_string, child_fname_string, child_lname_string, child_gender_string,
            child_class_string, school_code;
    private ImageView edit_image, delete_child;
    private Button edit_button;

    private Accessories childdetails_accessor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child__details);
        //accessories
        childdetails_accessor = new Accessories(Child_Details.this);
        school_code = childdetails_accessor.getString("school_code");

        //retrieve the values from add child activity
        parent_code_string = childdetails_accessor.getString("parentcode_adapter");
        child_fname_string = childdetails_accessor.getString("childfname_adapter");
        child_lname_string = childdetails_accessor.getString("childlname_adapter");
        child_class_string = childdetails_accessor.getString("childclass_adapter");
        child_gender_string = childdetails_accessor.getString("childgender_adapter");
        child_code_string = childdetails_accessor.getString("childcode_adapter");

        getSupportActionBar().setTitle(child_fname_string + " | Child");

        //textviews
        child_fname_textview = findViewById(R.id.child_fname_textview);
        child_lname_textview = findViewById(R.id.child_lname_textview);
        child_gender_textview = findViewById(R.id.child_gender_textview);
        child_class_textview = findViewById(R.id.the_child_class_textview);

        //edittexts
        child_fname_edittext = findViewById(R.id.the_child_fname_edittext);
        child_lname_edittext = findViewById(R.id.the_childlname_editText);
        child_class_edittext = findViewById(R.id.the_class_editText);

        //spinner
        child_gender_spinner = findViewById(R.id.gender_spinner);

        //ImageView
        edit_image = findViewById(R.id.edit_image);

        //button
        edit_button = findViewById(R.id.edit_button);

        //set text into tetviews
        child_fname_textview.setText(child_fname_string);
        child_lname_textview.setText(child_lname_string);
        child_gender_textview.setText(child_gender_string);
        child_class_textview.setText(child_class_string);


        edit_image.setOnClickListener(v -> {

            AlertDialog.Builder makechanges = new AlertDialog.Builder(this, R.style.Myalert);
            makechanges.setTitle("Edit")
                    .setIcon(android.R.drawable.ic_menu_edit)
                    .setMessage("Contact school administration to correct any mistakes with your child\'s details.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            makechanges.show();
        });

//
//    private void addToNotifications() {
//        try {
//            Random random = new Random();
//            int a = random.nextInt(987654);
//            String notificationID = "notification" + a+"";
//            DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference("school_notifications").child(school_code).child(notificationID);
//            mdatabase.child("image").setValue("UC");
//            mdatabase.child("message").setValue("You have successfully updated details of " + child_fname_string);
//            mdatabase.child("title").setValue("Child update successful");
//            mdatabase.child("time").setValue(new Date().toString());
//        }catch (NullPointerException e){
//
//        }
//    }


}
}
