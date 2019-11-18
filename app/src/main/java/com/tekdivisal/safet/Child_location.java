package com.tekdivisal.safet;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class Child_location extends AppCompatActivity {

    private LinearLayout status_bottom_sheet, bus_bottom_sheet;
    private BottomSheetBehavior status_sheetBehavior, bus_sheetBehaviour;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_location);

        getSupportActionBar().setTitle("Student Name");

        bottomNavigationView = findViewById(R.id.find_child_navigation);
        //status bottom sheet
        status_bottom_sheet = findViewById(R.id.status_bottom_sheet);
        status_sheetBehavior = BottomSheetBehavior.from(status_bottom_sheet);

        //bus bottom sheet
        bus_bottom_sheet = findViewById(R.id.bus_bottom_sheet);
        bus_sheetBehaviour = BottomSheetBehavior.from(bus_bottom_sheet);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.l_location:
                        fragmentManager.beginTransaction().replace(R.id.child_find_layout,new Bus_Location()).commit();
                        break;

                    case R.id.bus:
                        if(bus_sheetBehaviour.getState() != BottomSheetBehavior.STATE_EXPANDED){
                            bus_sheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                            status_sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }else{
                            bus_sheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        break;

                    case R.id.status:
                        if(status_sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                            status_sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            bus_sheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }else{
                            status_sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        break;
                }
                return false;
            }
        });

    }
}
