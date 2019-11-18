package com.tekdivisal.safet;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Edit_Location extends Fragment {
    private BottomNavigationView location_navigation;
    private FragmentManager fragmentManager;

    public Edit_Location() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View edit_location =  inflater.inflate(R.layout.fragment_edit__location, container, false);
        getActivity().setTitle("Location");

        location_navigation = edit_location.findViewById(R.id.location_navigation);
        fragmentManager = getActivity().getSupportFragmentManager();
        location_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.location:
                        fragmentManager.beginTransaction().replace(R.id.location_fragment,new Bus_Location()).commit();
                        break;

                    case R.id.reminder:
                        fragmentManager.beginTransaction().replace(R.id.location_fragment,new Reminder_Fragment()).commit();
                        break;
                }
                return false;
            }
        });
        return edit_location;
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentManager.beginTransaction().replace(R.id.location_fragment,new Bus_Location()).commit();
    }
}
