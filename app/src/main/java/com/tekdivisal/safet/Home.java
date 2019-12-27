package com.tekdivisal.safet;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tekdivisal.safet.Adapters.Facilities_Adapter;
import com.tekdivisal.safet.Adapters.image_slider_adapter;
import com.tekdivisal.safet.Model.Facilities;
import com.tekdivisal.safet.PiccassoImageProcessor.PicassoImageLoadingService;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import ss.com.bannerslider.Slider;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private Slider slider;
    private TextView school_nameTextView;
    private Accessories homeaccessor;

    //facilities items
    private ArrayList facilitiesArray = new ArrayList<Facilities>();
    private RecyclerView facilities_RecyclerView;
    private RecyclerView.Adapter facilities_Adapter;
    private String school_id,facility_id,facility_name, facility_image,
            image_one_image, image_two_image, image_three_image, language_string, range_string,
            school_logo_string,
            mission_string, vision_string, admission_status_string, parent_code_string,message_arrived_title,
            message_arrived_message, message_arrived_date, message_arrived_time;
    private TextView no_facilities, facilies_no_internet, language_range_textview, mission_text,
            vision_text, admission_status;

    private ImageView school_logo;
    private LinearLayout mission_layout, vision_layout;

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View unverified =  inflater.inflate(R.layout.fragment_home, container, false);
        homeaccessor = new Accessories(getActivity());

        school_id = homeaccessor.getString("school_code");
        parent_code_string = homeaccessor.getString("user_phone_number");

        Slider.init(new PicassoImageLoadingService(getActivity()));
        slider = unverified.findViewById(R.id.banner_slider1);
        slider.setAdapter(new image_slider_adapter(image_one_image, image_two_image, image_three_image));

         school_nameTextView = unverified.findViewById(R.id.school_name);

         school_nameTextView.setText(homeaccessor.getString("school_name"));

        //        facilities initializations
        facilities_RecyclerView = unverified.findViewById(R.id.facilities_recyclerView);
        facilies_no_internet = unverified.findViewById(R.id.facilities_no_internet);
        no_facilities = unverified.findViewById(R.id.no_facilities);

        language_range_textview = unverified.findViewById(R.id.language_range);
        mission_text = unverified.findViewById(R.id.mission_text);
        vision_text = unverified.findViewById(R.id.vision_text);
        admission_status = unverified.findViewById(R.id.admission_status);
        school_logo = unverified.findViewById(R.id.school_logo);

        //layouts
        mission_layout = unverified.findViewById(R.id.mission_layout);
        vision_layout = unverified.findViewById(R.id.vision_layout);

        facilities_RecyclerView.setHasFixedSize(true);
        facilities_Adapter = new Facilities_Adapter(getFacilitiesFromDatabase(),getActivity());
        facilities_RecyclerView.setAdapter(facilities_Adapter);

        new Look_for_all().execute();

        facilies_no_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    Fetch_Image_One();
//                    Fetch_Facilities_IDS();
                }else{
                    facilies_no_internet.setVisibility(View.VISIBLE);
                    no_facilities.setVisibility(View.GONE);
//                    mission_text.setText("No internet connection");
//                    vision_text.setText("No internet connection");
                }
            }
        });
        return  unverified;
    }


    private class Look_for_all extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            final Handler thehandler;

            thehandler = new Handler(Looper.getMainLooper());
            final int delay = 15000;

            thehandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isNetworkAvailable()){
                        get_Messages_IDs();
                    }else{
//                        Toast.makeText(Admin_MainActivity.this,"checking", Toast.LENGTH_LONG).show();
                    }
                    thehandler.postDelayed(this,delay);
                }
            },delay);
            return null;
        }
    }

    private void get_Messages_IDs() {
        try {
            DatabaseReference get_messages_arrived = FirebaseDatabase.getInstance().getReference("temp_messages")
                    .child(school_id).child(parent_code_string);
            get_messages_arrived.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Fetch_message_details(child.getKey());
                        }
                    }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();
                }
            });
        }catch(NullPointerException e){

        }
    }

    private void Fetch_message_details(final String key) {
        DatabaseReference has_bus_arrived = FirebaseDatabase.getInstance().getReference("temp_messages")
                .child(school_id).child(parent_code_string).child(key);
        has_bus_arrived.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("subject")){
                            message_arrived_title = child.getValue().toString();
                        }

                        if(child.getKey().equals("message")){
                            message_arrived_message = child.getValue().toString();
                        }

                        if(child.getKey().equals("date")){
                            message_arrived_date = child.getValue().toString();
                            if(!message_arrived_date.equals("Select date")){

                            }else{
                                message_arrived_date = "";
                            }
                        }

                        if(child.getKey().equals("time")){
                            message_arrived_time = child.getValue().toString();
                            if(!message_arrived_time.equals("Select time")){

                            }else{
                                message_arrived_time = "";
                            }
                        }

                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                        }
                    }
                    Show_arrived_notification(R.drawable.message,key, message_arrived_title, message_arrived_message,
                            message_arrived_date,message_arrived_time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

            }
        });
    }

    private void Show_arrived_notification(int message, String key, String message_arrived_title, String message_arrived_message, String message_arrived_date, String message_arrived_time) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(getActivity(), Messages_Activity.class);
//        intent.putExtra("alertID","yes");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "1200")
                .setSmallIcon(message)
                .setContentTitle(message_arrived_title)
                .setContentText(message_arrived_message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message_arrived_message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
//                .setFullScreenIntent(fullScreenPendingIntent,true);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1200", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity());

            // notificationId is a unique int for each notification that you must define
            notificationManagerCompat.notify(1200, builder.build());
//            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//            builder.setDefaults(Notification.DEFAULT_VIBRATE);
                Move_Arrived_From_pending(key,message_arrived_title,message_arrived_message,message_arrived_date,message_arrived_time);

        }else {
//        builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//        builder.setDefaults(Notification.DEFAULT_VIBRATE);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity());
            // notificationId is a unique int for each notification that you must define
            notificationManagerCompat.notify(1200, builder.build());
            Move_Arrived_From_pending(key,message_arrived_title, message_arrived_message, message_arrived_date, message_arrived_time);
        }
    }

    private void Move_Arrived_From_pending(final String message_key, String message_arrived_title, String message_arrived_message, String message_arrived_date, String message_arrived_time) {
        try {
            DatabaseReference move_from_tmpMessage_to_main = FirebaseDatabase.getInstance().getReference("messages")
                    .child(school_id).child(parent_code_string).child(message_key);

            move_from_tmpMessage_to_main.child("subject").setValue(message_arrived_title);
            move_from_tmpMessage_to_main.child("message").setValue(message_arrived_message);
            move_from_tmpMessage_to_main.child("date").setValue(message_arrived_date);
            move_from_tmpMessage_to_main.child("time").setValue(message_arrived_time)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    DatabaseReference removeRef = FirebaseDatabase.getInstance().getReference("temp_messages").child(school_id).child(parent_code_string).child(message_key);
                    removeRef.removeValue();
                    Toast.makeText(getActivity(), "Removed", Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_Facilities_IDS() {
        facilies_no_internet.setVisibility(View.GONE);
        try{
            DatabaseReference get_faciility_urlID = FirebaseDatabase.getInstance().getReference("images").child("facilities").child(school_id);//.child(mauth.getCurrentUser().getUid());
            get_faciility_urlID.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Fetch_Facility_Url(child.getKey());
                        }
                    }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_Facility_Url(final String key) {
        DatabaseReference getFacilityUrls = FirebaseDatabase.getInstance().getReference("images").child("facilities").child(school_id).child(key);
        getFacilityUrls.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("image")){
                            facility_image = child.getValue().toString();
                        }
                        if(child.getKey().equals("facility_name")){
                            facility_name = child.getValue().toString();
                        }
                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                        }
                    }

                    Facilities obj = new Facilities(key,facility_name, facility_image);
                    facilitiesArray.add(obj);
                    facilities_RecyclerView.setAdapter(facilities_Adapter);
                    facilities_Adapter.notifyDataSetChanged();
                    no_facilities.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

            }
        });
    }

    private void Fetch_Image_One() {
        try {
            DatabaseReference image_one = FirebaseDatabase.getInstance().getReference("images").child("school_images").child(school_id).child("image_one");
            image_one.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("image")){
                                image_one_image = child.getValue().toString();
                                if(!image_one_image.equals("")){
                                }else{
                                    image_one_image = "https://www.cdnis.edu.hk/sites/default/files/styles/1200x795/public/CDNIS%20solar%20panel_1.jpg?itok=qCVODOAq";
                                }
                            }
                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                            }
                        }
                        Fetch_Image_Two();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_Image_Two() {
        try {
            DatabaseReference image_two = FirebaseDatabase.getInstance().getReference("images").child("school_images").child(school_id).child("image_two");
            image_two.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("image")){
                                image_two_image = child.getValue().toString();
                                if(!image_two_image.equals("")){
                                }else{
                                    image_two_image = "https://via.placeholder.com/550/FFFFFF/808080%20?Text=Digital.comC/O%20https://placeholder.com/";
                                }
                            }

                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                            }
                        }
                        Fetch_Image_Three();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_Image_Three() {
        try {
            DatabaseReference image_three = FirebaseDatabase.getInstance().getReference("images").child("school_images").child(school_id).child("image_three");
            image_three.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("image")){
                                image_three_image = child.getValue().toString();
                                if(!image_three_image.equals("")){
                                }else{
                                    image_three_image = "https://via.placeholder.com/550/FFFFFF/808080%20?Text=Digital.comC/O%20https://placeholder.com/";
                                }
                            }

                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                            }
                        }
                        slider.setAdapter(new image_slider_adapter(image_one_image, image_two_image, image_three_image));
                        Fetch_Facilities_IDS();
                        Fetch_school_logo();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_school_logo() {
        try {
            DatabaseReference image_three = FirebaseDatabase.getInstance().getReference("images").child("school_images").child(school_id).child("logo");
            image_three.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("image")){
                                school_logo_string = child.getValue().toString();
                                if(!school_logo_string.equals("")){
                                    Picasso.with(getActivity()).load(Uri.parse(school_logo_string)).into(school_logo);
                                }else{
                                    image_three_image = "https://via.placeholder.com/550/FFFFFF/808080%20?Text=Digital.comC/O%20https://placeholder.com/";
                                }
                            }

                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_lang_and_status() {
        try {
            DatabaseReference get_lang = FirebaseDatabase.getInstance().getReference("schools")
                    .child(school_id);
            get_lang.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("language")){
                                language_string = child.getValue().toString();
                            }

                            if(child.getKey().equals("range")){
                                range_string = child.getValue().toString();
                                if(range_string.equals("")){
                                    language_range_textview.setText("None");
                                }else{
                                    language_range_textview.setText(language_string + " | " + range_string);
                                }
                            }

                            if(child.getKey().equals("mission")){
                                mission_string = child.getValue().toString();
                                if(mission_string.equals("")){
                                    mission_text.setText("None");
                                }else{
                                    mission_layout.setVisibility(View.VISIBLE);
                                    mission_text.setText(mission_string);
                                }
                            }

                            if(child.getKey().equals("vision")){
                                vision_string = child.getValue().toString();
                                if(vision_string.equals("")){
                                    vision_text.setText("None");
                                }else{
                                    vision_layout.setVisibility(View.VISIBLE);
                                    vision_text.setText(vision_string);
                                }
                            }

                            if(child.getKey().equals("admission_status")){
                                admission_status_string = child.getValue().toString();
                                if(admission_status_string.equals("")){
                                    admission_status.setText("Close");
                                }else{
                                    admission_status.setText(admission_status_string);
                                }
                            }

                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){

        }
    }

    public ArrayList<Facilities> getFacilitiesFromDatabase(){
        return  facilitiesArray;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isNetworkAvailable()){
            Fetch_Image_One();
            Fetch_lang_and_status();
        }else{
            facilies_no_internet.setVisibility(View.VISIBLE);
            no_facilities.setVisibility(View.GONE);
        }

    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }catch (NullPointerException e){

        }
      return true;
    }
}
