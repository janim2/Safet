package com.tekdivisal.safet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdivisal.safet.Adapters.NotifyAdapter;
import com.tekdivisal.safet.Model.Notify;

import java.util.ArrayList;

public class Messages extends AppCompatActivity {
    private ArrayList messagesArray = new ArrayList<Notify>();
    private RecyclerView messages_RecyclerView;
    private RecyclerView.Adapter messages_Adapter;
    private String school_id, parent_code, notification_title, messages_message, messages_time,
            messageImage;
    private FirebaseAuth mauth;
    private TextView no_messages;

    private Accessories messages_accessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        getSupportActionBar().setTitle("Messages");

        messages_accessor = new Accessories(Messages.this);

        mauth = FirebaseAuth.getInstance();

        school_id = messages_accessor.getString("school_code");
        parent_code = messages_accessor.getString("user_phone_number");

        messages_RecyclerView = findViewById(R.id.messages_recyclerView);
        no_messages = findViewById(R.id.no_messages);

        //reviews adapter settings starts here
        if(isNetworkAvailable()){
            getUsermessages_ID();
        }else{
            no_messages.setText("No internet connection");
            no_messages.setVisibility(View.VISIBLE);
        }
        messages_RecyclerView.setHasFixedSize(true);
        messages_Adapter = new NotifyAdapter(getmessagesFromDatabase(),Messages.this);
        messages_RecyclerView.setAdapter(messages_Adapter);
    }

    private void getUsermessages_ID() {
        try{
            DatabaseReference get_Driver_messages = FirebaseDatabase.getInstance().getReference("messages").child(school_id).child(parent_code);

            get_Driver_messages.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Fetch_User_messages(child.getKey());
                        }
                    }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Messages.this,"Cancelled",Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_User_messages(String key) {
        DatabaseReference getmessages = FirebaseDatabase.getInstance().getReference("messages").child(school_id).child(parent_code).child(key);
        getmessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("title")){
                            notification_title = child.getValue().toString();
                        }

                        if(child.getKey().equals("message")){
                            messages_message = child.getValue().toString();
                        }

                        if(child.getKey().equals("time")){
                            messages_time = child.getValue().toString();
                        }

                        if(child.getKey().equals("image")){
                            messageImage = child.getValue().toString();
                        }

                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                        }
                    }

                    Notify obj = new Notify(notification_title,messages_message,messages_time,messageImage);
                    messagesArray.add(obj);
                    messages_RecyclerView.setAdapter(messages_Adapter);
                    messages_Adapter.notifyDataSetChanged();
                    no_messages.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Messages.this,"Cancelled",Toast.LENGTH_LONG).show();

            }
        });
    }

    public ArrayList<Notify> getmessagesFromDatabase(){
        return  messagesArray;
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
