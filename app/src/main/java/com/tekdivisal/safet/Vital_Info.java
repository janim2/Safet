package com.tekdivisal.safet;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class Vital_Info extends Fragment {
    private LinearLayout status_bottom_sheet, bus_bottom_sheet;
    private Accessories vital_info_accessor;
    private String vital_info_type;



    public Vital_Info() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vital =  inflater.inflate(R.layout.fragment_vital__info, container, false);

        vital_info_accessor = new Accessories(getActivity());
        vital_info_type = vital_info_accessor.getString("vital_type");
        status_bottom_sheet = vital.findViewById(R.id.status_bottom_sheet);
        //bus bottom sheet
        bus_bottom_sheet = vital.findViewById(R.id.bus_bottom_sheet);

        if(!vital_info_type.equals("")){
            if(vital_info_type.equals("bus_vitals")){
                status_bottom_sheet.setVisibility(View.GONE);
                bus_bottom_sheet.setVisibility(View.VISIBLE);
//                getBus_Details();
            }else{
                bus_bottom_sheet.setVisibility(View.GONE);
                status_bottom_sheet.setVisibility(View.VISIBLE);
//                getStatus();
            }
        }

        return vital;
    }

}
