package com.tekdivisal.safet;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {
    private CheckBox pickup_notify, reached_notify, left_notify, dropped_notify;
    private TextView create_passwordtextview;
    private LinearLayout create_passwordlayout, reminder_layout, delete_notifications_layout;
    private Accessories settings_accessor;

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

        pickup_notify = settings.findViewById(R.id.pickup_notify_text);
        reached_notify = settings.findViewById(R.id.reached_notify_text);
        dropped_notify = settings.findViewById(R.id.drop_notify_text);
        left_notify = settings.findViewById(R.id.left_notify_text);

        create_passwordlayout = settings.findViewById(R.id.create_or_change_layout);
        reminder_layout = settings.findViewById(R.id.reminder_layout);
        create_passwordtextview = settings.findViewById(R.id.create_or_chagne_textView);
        delete_notifications_layout = settings.findViewById(R.id.delete_notifications_layotu);

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

        if(settings_accessor.getBoolean("isverified")){
            if(settings_accessor.getBoolean("isPasswordCreated")){
                create_passwordtextview.setText("Change password");
            }else{
                create_passwordtextview.setText("Create password");
            }
        }else{
            reminder_layout.setVisibility(View.GONE);
            create_passwordtextview.setText("Create password");
        }

        create_passwordlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "change password",Toast.LENGTH_LONG).show();
            }
        });

        delete_notifications_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "delete notification",Toast.LENGTH_LONG).show();
            }
        });
        return settings;

    }

}
