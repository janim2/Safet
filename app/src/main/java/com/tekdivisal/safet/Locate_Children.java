package com.tekdivisal.safet;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdivisal.safet.Adapters.ChildrenAdapter;
import com.tekdivisal.safet.Model.Children;

import java.util.ArrayList;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class Locate_Children extends Fragment {
    private TextView no_children_textView;
    private TextView no_internet;
    private ArrayList childrenArray = new ArrayList<Children>();
    private RecyclerView children_RecyclerView;
    private RecyclerView.Adapter children_Adapter;
    private String school_id_string, parent_code_string, string_child_code, sfirst_name, slastname,
    sclass, sgender, driver_key, is_assigned, the_assigned_bus;
    private Accessories home_accessor;
    private String bus_arrived_title, bus_arrived_message, bus_arrived_time, bus_arrivedImage,
            bus_arrived_status;

    public Locate_Children() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View home =  inflater.inflate(R.layout.fragment_locate_children, container, false);
        getActivity().setTitle("Locate_Children");
        setRetainInstance(true);
        //initialization of accessories
        home_accessor = new Accessories(getActivity());

        no_children_textView = home.findViewById(R.id.no_children);
        no_internet = home.findViewById(R.id.no_internet);
        children_RecyclerView = home.findViewById(R.id.children_recyclerView);

        //strings
        school_id_string = home_accessor.getString("school_code");
        parent_code_string = home_accessor.getString("user_phone_number");


        if(isNetworkAvailable()){
            get_Children_IDs();
            new Look_for_all().execute();
        }else{
            no_children_textView.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }
        children_RecyclerView.setHasFixedSize(true);
        children_RecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        children_Adapter = new ChildrenAdapter(getFromDatabase(),getActivity());
        children_RecyclerView.setAdapter(children_Adapter);

        no_internet.setOnClickListener(v -> {
            if(isNetworkAvailable()){
                get_Children_IDs();
                new Look_for_all().execute();
            }else{
                no_children_textView.setVisibility(View.GONE);
                no_internet.setVisibility(View.VISIBLE);
            }
        });
        return home;
    }

    private void get_Children_IDs() {
        try{
            DatabaseReference get_child_id = FirebaseDatabase.getInstance().getReference("children").child(school_id_string).child(parent_code_string);

            get_child_id.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Fetch_Child_Details(child.getKey());
                        }
                    }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Toast.makeText(getActivity().this,"Cancelled",Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){

        }
    }

    private void Fetch_Child_Details(final String key) {
        DatabaseReference getChild_info = FirebaseDatabase.getInstance().getReference("children").child(school_id_string).child(parent_code_string).child(key);
        getChild_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("firstname")){
                            sfirst_name = child.getValue().toString();
                        }
                        if(child.getKey().equals("lastname")){
                            slastname = child.getValue().toString();
                        }
                        if(child.getKey().equals("class")){
                            sclass = child.getValue().toString();
                        }
                        if(child.getKey().equals("gender")){
                            sgender = child.getValue().toString();
                        }
                        if(child.getKey().equals("isAssigned_bus")){
                            is_assigned = child.getValue().toString();
                        }
                        if(child.getKey().equals("assigned_bus")){
                            the_assigned_bus = child.getValue().toString();
                        }

                        string_child_code = key;
                    }
                    Children obj = new Children(sfirst_name,slastname,sclass,sgender,string_child_code,is_assigned,the_assigned_bus);
                    childrenArray.add(obj);
                    children_RecyclerView.setAdapter(children_Adapter);
                    children_Adapter.notifyDataSetChanged();
                    no_internet.setVisibility(View.GONE);
                    no_children_textView.setVisibility(View.GONE);

                    //might get user details here
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                        get_bus_Arrived_IDs();
                        get_bus_status_IDs();
                    }else{
//                        Toast.makeText(Admin_MainActivity.this,"checking", Toast.LENGTH_LONG).show();
                    }
                    thehandler.postDelayed(this,delay);
                }
            },delay);
            return null;
        }
    }

    private void get_bus_Arrived_IDs() {
//        Toast.makeText(getActivity(), "working",Toast.LENGTH_LONG).show();
        try {
            DatabaseReference get_Bus_arrived = FirebaseDatabase.getInstance().getReference("bus_notification")
                    .child(parent_code_string).child(school_id_string);
            get_Bus_arrived.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Has_bus_arrived(child.getKey());
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

    private void Has_bus_arrived(final String key) {
        DatabaseReference has_bus_arrived = FirebaseDatabase.getInstance().getReference("bus_notification")
                .child(parent_code_string).child(school_id_string).child(key);
        has_bus_arrived.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("title")){
                            bus_arrived_title = child.getValue().toString();
                        }

                        if(child.getKey().equals("message")){
                            bus_arrived_message = child.getValue().toString();
                        }

                        if(child.getKey().equals("time")){
                            bus_arrived_time = child.getValue().toString();
                        }

                        if(child.getKey().equals("image")){
                            bus_arrivedImage = child.getValue().toString();
                        }

                        else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                        }
                    }
                    driver_key = key;
                    Show_arrived_notification(R.drawable.finish_line,bus_arrived_title, bus_arrived_message,
                            bus_arrivedImage,bus_arrived_time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

            }
        });
    }

    private void Show_arrived_notification(int iconss, String bus_arrived_title,
                                           String bus_arrived_message, String bus_arrivedImage,
                                           String bus_arrived_time) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(getActivity(), Notifications.class);
//        intent.putExtra("alertID","yes");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "1100")
                .setSmallIcon(iconss)
                .setContentTitle(bus_arrived_title)
                .setContentText(bus_arrived_message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bus_arrived_message))
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
            NotificationChannel channel = new NotificationChannel("1100", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity());

            // notificationId is a unique int for each notification that you must define
            notificationManagerCompat.notify(1100, builder.build());
//            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            if(bus_arrivedImage.equals("BAN")){
                Move_Arrived_From_pending(bus_arrived_title,bus_arrived_message,bus_arrivedImage,bus_arrived_time);
            }else{
//                Toast.makeText(getActivity(),"Nothing to move",Toast.LENGTH_LONG).show();
            }
        }
//        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity());
        // notificationId is a unique int for each notification that you must define
        notificationManagerCompat.notify(1100, builder.build());
        if(bus_arrivedImage != null){
            Move_Arrived_From_pending(bus_arrived_title,bus_arrived_message,bus_arrivedImage,bus_arrived_time);
        }else{
            Toast.makeText(getActivity(),"Nothing to move",Toast.LENGTH_LONG).show();
        }
    }

    private void Move_Arrived_From_pending(String bus_arrived_title, String bus_arrived_message, String bus_arrivedImage, String bus_arrived_time) {
        try {
            Random ndd = new Random();
            int rr = ndd.nextInt(99999);
            String notifications_id = "notification"+rr+"";
            DatabaseReference move_from_bus_notification_to_main = FirebaseDatabase.getInstance().getReference("notifications")
                    .child(school_id_string).child(parent_code_string).child(notifications_id);

            move_from_bus_notification_to_main.child("BAN").setValue(bus_arrivedImage);
            move_from_bus_notification_to_main.child("message").setValue(bus_arrived_message);
            move_from_bus_notification_to_main.child("title").setValue(bus_arrived_title);
            move_from_bus_notification_to_main.child("time").setValue(bus_arrived_time).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ReMoveSecurity_from_main_toPending(parent_code_string);
                }
            });
        }catch (NullPointerException e){

        }
    }

    private void ReMoveSecurity_from_main_toPending(String parent_code_string) {
        try {
            DatabaseReference removeRef = FirebaseDatabase.getInstance().getReference("bus_notification").child(parent_code_string);
            removeRef.removeValue();
        }catch (NullPointerException e){

        }
    }

    private void get_bus_status_IDs() {
//        Toast.makeText(getActivity(), "house", Toast.LENGTH_LONG).show();
        try {
            DatabaseReference get_Bus_arrived = FirebaseDatabase.getInstance().getReference("trip_status")
                    .child(school_id_string);
            get_Bus_arrived.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            get_Bus_Status(child.getKey());
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

    private void get_Bus_Status(String key) {
//        Toast.makeText(getActivity(), "working", Toast.LENGTH_LONG).show();
        DatabaseReference get_Bus_status = FirebaseDatabase.getInstance().getReference("trip_status")
                .child(school_id_string).child(key);
        get_Bus_status.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("status")){
                            try {
                                bus_arrived_status = child.getValue().toString();
//                                Toast.makeText(getActivity(), bus_arrived_status,Toast.LENGTH_LONG).show();
//                                home_accessor.put("bus_status", bus_arrived_status);
                            }catch (NullPointerException e){

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
    }

    public ArrayList<Children> getFromDatabase(){
        return  childrenArray;
    }


    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }catch (NullPointerException e){

        }
        return false;
    }

}
