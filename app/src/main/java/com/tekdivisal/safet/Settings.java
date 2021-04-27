package com.tekdivisal.safet;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdivisal.safet.Helpers.HelperClass;


/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {
    private CheckBox pickup_notify, reached_notify, left_notify, dropped_notify;
    private TextView create_passwordtextview;
    private LinearLayout create_passwordlayout, reminder_layout, delete_notifications_layout;
    private Accessories settings_accessor;
    private TextView change_reminder,select_all, reminder_text;

    private String parent_code, school_id_string, shouldN_for_pickup, shouldN_for_drop,
            shouldN_for_left, shouldN_for_reached, notify_distace;
    private Dialog view_process_dialogue;

    public Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View settings =  inflater.inflate(R.layout.fragment_settings, container, false);
        getActivity().setTitle("Settings");
        settings_accessor = new Accessories(getActivity());

        //initializing the dialogue
        view_process_dialogue = new Dialog(getActivity());

        school_id_string = settings_accessor.getString("school_code");
        parent_code = settings_accessor.getString("user_phone_number");


        pickup_notify = settings.findViewById(R.id.pickup_notify_text);
        reached_notify = settings.findViewById(R.id.reached_notify_text);
        dropped_notify = settings.findViewById(R.id.drop_notify_text);
        left_notify = settings.findViewById(R.id.left_notify_text);

        create_passwordlayout = settings.findViewById(R.id.create_or_change_layout);
        reminder_layout = settings.findViewById(R.id.reminder_layout);
        create_passwordtextview = settings.findViewById(R.id.create_or_chagne_textView);
        delete_notifications_layout = settings.findViewById(R.id.delete_notifications_layotu);

        change_reminder = settings.findViewById(R.id.change_reminder_);
        select_all = settings.findViewById(R.id.select_all);
        reminder_text = settings.findViewById(R.id.reminder_set);

        //        making the text color mix mix
//        for pickup notification text
        Spannable pickupT = new SpannableString(getResources().getString(R.string.pickup_notification));
        pickupT.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_blue)), 0, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pickup_notify.setText(pickupT);

//        for drop notification
        Spannable dropT = new SpannableString(getResources().getString(R.string.drop_notification));
        dropT.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_blue)), 0, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        dropped_notify.setText(dropT);

//        for reached notification
        Spannable reachedT = new SpannableString(getResources().getString(R.string.reached_school_notification));
        reachedT.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_blue)), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        reached_notify.setText(reachedT);

//        for left school notification
        Spannable leftT = new SpannableString(getResources().getString(R.string.left_school_notification));
        leftT.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_blue)), 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        left_notify.setText(leftT);

//       for password
        Spannable changepassword = new SpannableString(getResources().getString(R.string.create_password));
        changepassword.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_blue)), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//        if(settings_accessor.getBoolean("isverified")){
//            if(settings_accessor.getBoolean("isPasswordCreated")){
//                create_passwordtextview.setText("Change password");
//            }else{
//                create_passwordtextview.setText("Create password");
//            }
//        }else{
//            reminder_layout.setVisibility(View.GONE);
//            create_passwordtextview.setText("Create password");
//        }

        create_passwordlayout.setOnClickListener(v -> {
            Intent goto_password = new Intent(getActivity(), Change_password.class);
            startActivity(goto_password);
        });

        delete_notifications_layout.setOnClickListener(v -> {
            AlertDialog.Builder delete_notifications = new AlertDialog.Builder(getActivity());
            delete_notifications.setTitle("Delete Notifications")
                    .setMessage("Are you sure you want to delete all your notifications?")
                    .setPositiveButton("DELETE", (dialog, which) -> {
                        if(new HelperClass(getActivity()).isNetworkAvailable()){
                            try {
                                DatabaseReference delete_ = FirebaseDatabase.getInstance().getReference("notifications")
                                        .child(school_id_string).child(parent_code);
                                delete_.removeValue().addOnCompleteListener(task -> Toast.makeText(getActivity(), "Notifications deleted", Toast.LENGTH_LONG).show());
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();
                        }
                    });

            delete_notifications.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

            delete_notifications.show();
        });

        if(!settings_accessor.getBoolean("hassetAlertSettings")){
            pickup_notify.setChecked(true);
            dropped_notify.setChecked(true);
            reached_notify.setChecked(true);
            left_notify.setChecked(true);
        }else{
            if(new HelperClass(getActivity()).isNetworkAvailable()){
                FetchUserSettings(school_id_string, parent_code);
            }else{
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();
                pickup_notify.setChecked(false);
                dropped_notify.setChecked(false);
                reached_notify.setChecked(false);
                left_notify.setChecked(false);
            }
        }

        if(!settings_accessor.getBoolean("hassetnotify_Distance")){
            reminder_text.setText("Currently 1.0m before spot");
        }else{
            if(new HelperClass(getActivity()).isNetworkAvailable()){
                try {
                    DatabaseReference _pick_reminder = FirebaseDatabase.getInstance().getReference("settings")
                            .child(school_id_string).child(parent_code);
                    _pick_reminder.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for(DataSnapshot child : dataSnapshot.getChildren()){
                                    if(child.getKey().equals("notify_distance")){
                                        notify_distace = child.getValue().toString();
                                    }

                                    else{
//                                      Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                                    }
                                }

                                if(notify_distace != null){
                                    reminder_text.setText("Currently " + notify_distace + "m before spot");
                                }else{
                                    reminder_text.setText("Currently 1.0m before spot");
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
            }else{
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();

            }
        }
//
//        delete_notifications_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(settings_accessor.getBoolean("isPasswordCreated")){
//                    Intent pasword_intent = new Intent(getActivity(), Change_password.class);
//                    pasword_intent.putExtra("pAction_indicator", "change_password");
//                    startActivity(pasword_intent);
//                }else{
//                    Intent pasword_intent = new Intent(getActivity(), Change_password.class);
//                    pasword_intent.putExtra("pAction_indicator", "create_password");
//                    startActivity(pasword_intent);
//                }
//            }
//        });

        change_reminder.setOnClickListener(v -> {
            Changereminder_dialogue(getActivity());
        });

        select_all.setOnClickListener(v -> {
            pickup_notify.setChecked(true);
            dropped_notify.setChecked(true);
            reached_notify.setChecked(true);
            left_notify.setChecked(true);
            SaveSettings(school_id_string, parent_code,"all","");
        });

        pickup_notify.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                SaveSettings(school_id_string,parent_code,"pickup_notify", "Yes");
            }else{
                SaveSettings(school_id_string,parent_code,"pickup_notify", "No");
            }
        });

        dropped_notify.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                SaveSettings(school_id_string,parent_code,"dropped_notify", "Yes");
            }else{
                SaveSettings(school_id_string,parent_code,"dropped_notify", "No");
            }
        });

        reached_notify.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                SaveSettings(school_id_string,parent_code,"reached_notify", "Yes");
            }else{
                SaveSettings(school_id_string,parent_code,"reached_notify", "No");
            }
        });

        left_notify.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                try{
                    SaveSettings(school_id_string,parent_code,"left_notify", "Yes");
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }else{
                try{
                    SaveSettings(school_id_string,parent_code,"left_notify", "No");
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        return settings;

    }

    private void FetchUserSettings(String school_id_string, String parent_code) {
        try {
            DatabaseReference fetchSettings = FirebaseDatabase.getInstance().getReference("settings")
                    .child(school_id_string).child(parent_code);
            fetchSettings.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("pickup_notify")){
                                shouldN_for_pickup = child.getValue().toString();
                            }

                            if(child.getKey().equals("dropped_notify")){
                                shouldN_for_drop = child.getValue().toString();
                            }

                            if(child.getKey().equals("reached_notify")){
                                shouldN_for_reached = child.getValue().toString();
                            }

                            if(child.getKey().equals("left_notify")){
                                shouldN_for_left = child.getValue().toString();
                            }

                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();

                            }
                        }

                        try{
                            if(shouldN_for_pickup.equals("Yes")){
                                pickup_notify.setChecked(true);
                            }else{
                                pickup_notify.setChecked(false);
                            }

                            if(shouldN_for_drop.equals("Yes")){
                                dropped_notify.setChecked(true);
                            }else{
                                dropped_notify.setChecked(false);
                            }

                            if(shouldN_for_left.equals("Yes")){
                                left_notify.setChecked(true);
                            }else{
                                left_notify.setChecked(false);
                            }

                            if(shouldN_for_reached.equals("Yes")){
                                reached_notify.setChecked(true);
                            }else{
                                reached_notify.setChecked(false);
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void SaveSettings(String school_id_string, String parent_code, String itemtoChange, String changeValue) {
            if(itemtoChange.equals("pickup_notify")){
                DatabaseReference addsettings = FirebaseDatabase.getInstance().getReference("settings")
                        .child(school_id_string).child(parent_code);
                addsettings.child("pickup_notify").setValue(changeValue)
                        .addOnCompleteListener(task -> Toast.makeText(getActivity(), "Settings changed", Toast.LENGTH_LONG).show());
            }

            else if(itemtoChange.equals("dropped_notify")){
                DatabaseReference addsettings = FirebaseDatabase.getInstance().getReference("settings")
                        .child(school_id_string).child(parent_code);
                addsettings.child("dropped_notify").setValue(changeValue)
                        .addOnCompleteListener(task -> {
                            Toast.makeText(getActivity(), "Settings changed", Toast.LENGTH_LONG).show();
                            settings_accessor.put("hassetAlertSettings", true);
                        });
            }

            else if(itemtoChange.equals("reached_notify")){
                DatabaseReference addsettings = FirebaseDatabase.getInstance().getReference("settings")
                        .child(school_id_string).child(parent_code);
                addsettings.child("reached_notify").setValue(changeValue)
                        .addOnCompleteListener(task -> {
                            Toast.makeText(getActivity(), "Settings changed", Toast.LENGTH_LONG).show();
                            settings_accessor.put("hassetAlertSettings", true);
                        });
            }

            else if(itemtoChange.equals("left_notify")){
                DatabaseReference addsettings = FirebaseDatabase.getInstance().getReference("settings")
                        .child(school_id_string).child(parent_code);
                addsettings.child("left_notify").setValue(changeValue)
                        .addOnCompleteListener(task -> {
                            Toast.makeText(getActivity(), "Settings changed", Toast.LENGTH_LONG).show();
                            settings_accessor.put("hassetAlertSettings", true);
                        });
            }

            else if(itemtoChange.equals("all")){
                DatabaseReference addsettings = FirebaseDatabase.getInstance().getReference("settings")
                        .child(school_id_string).child(parent_code);
                        addsettings.child("pickup_notify").setValue("Yes");
                        addsettings.child("dropped_notify").setValue("Yes");
                        addsettings.child("reached_notify").setValue("Yes");
                        addsettings.child("left_notify").setValue("Yes")
                                .addOnCompleteListener(task -> {
                                    Toast.makeText(getActivity(), "Settings changed", Toast.LENGTH_LONG).show();
                                    settings_accessor.put("hassetAlertSettings", true);
                                });
            }
    }

    private void Changereminder_dialogue(FragmentActivity activity) {
        final TextView cancelpopup,success_message;
        final Spinner when_spinner;
        final Button set_button;
        final ProgressBar loading;
        String distance[] = {"1", "2", "3", "4", "5",
                "10", "20", "30"};
        final String[] selected_distance = new String[1];
        ArrayAdapter<String> gender_type;

        view_process_dialogue.setContentView(R.layout.custom_confirmation_popup);
        cancelpopup = (TextView)view_process_dialogue.findViewById(R.id.cancel);
        success_message = (TextView)view_process_dialogue.findViewById(R.id.success_message);
        when_spinner = (Spinner) view_process_dialogue.findViewById(R.id.when_spinner);
        set_button = (Button)view_process_dialogue.findViewById(R.id.done_button);
        loading = (ProgressBar) view_process_dialogue.findViewById(R.id.loading);

        cancelpopup.setOnClickListener(v -> view_process_dialogue.dismiss());

        //setting the adapter for the child one spinner
        when_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_distance[0] = distance[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_distance[0] = "1";
            }
        });
        gender_type = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,distance);
        when_spinner.setAdapter(gender_type);

        set_button.setOnClickListener(v -> {
            if(new HelperClass(activity).isNetworkAvailable()){
                success_message.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                try {
                    DatabaseReference change_pick = FirebaseDatabase.getInstance().getReference("settings")
                            .child(school_id_string).child(parent_code);
                    change_pick.child("notify_distance").setValue(selected_distance[0]).addOnCompleteListener(task -> {
                        success_message.setText("Setting changed");
                        success_message.setVisibility(View.VISIBLE);
                        view_process_dialogue.dismiss();
                        Toast.makeText(activity, "Settings changed", Toast.LENGTH_LONG).show();
                        settings_accessor.put("hassetnotify_Distance", true);
                    });
                }catch (NullPointerException e){

                }
            }else{
                success_message.setText("No internet connection");
                success_message.setVisibility(View.VISIBLE);
            }
        });

        view_process_dialogue.show();

    }

}
