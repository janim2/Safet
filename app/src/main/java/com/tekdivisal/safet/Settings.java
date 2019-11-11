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
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {
    private CheckBox pickup_notify, reached_notify, left_notify, dropped_notify;


    public Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View settings =  inflater.inflate(R.layout.fragment_settings, container, false);

        pickup_notify = settings.findViewById(R.id.pickup_notify_text);
        reached_notify = settings.findViewById(R.id.reached_notify_text);
        dropped_notify = settings.findViewById(R.id.drop_notify_text);
        left_notify = settings.findViewById(R.id.left_notify_text);

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
        return settings;

    }

}
