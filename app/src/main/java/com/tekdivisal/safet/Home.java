package com.tekdivisal.safet;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tekdivisal.safet.Adapters.image_slider_adapter;
import com.tekdivisal.safet.PiccassoImageProcessor.PicassoImageLoadingService;

import ss.com.bannerslider.Slider;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private Slider slider;
    private TextView school_nameTextView;
    private Accessories homeaccessor;

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View unverified =  inflater.inflate(R.layout.fragment_home, container, false);
        homeaccessor = new Accessories(getActivity());

        Slider.init(new PicassoImageLoadingService(getActivity()));
        slider = unverified.findViewById(R.id.banner_slider1);

        slider.setAdapter(new image_slider_adapter());
         school_nameTextView = unverified.findViewById(R.id.school_name);

         school_nameTextView.setText(homeaccessor.getString("school_name"));

        return  unverified;
    }

}
