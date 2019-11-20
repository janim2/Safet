package com.tekdivisal.safet;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdivisal.safet.Adapters.ChildrenAdapter;
import com.tekdivisal.safet.Model.Children;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {
    private TextView no_children_textView;
    private TextView no_internet;
    private ArrayList childrenArray = new ArrayList<Children>();
    private RecyclerView children_RecyclerView;
    private RecyclerView.Adapter children_Adapter;
    private String school_id_string, phone_number_string, string_child_code, sfirst_name, slastname,
    sclass, sgender;
    private Accessories home_accessor;


    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View home =  inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");
        setRetainInstance(true);
        //initialization of accessories
        home_accessor = new Accessories(getActivity());

        no_children_textView = home.findViewById(R.id.no_children);
        no_internet = home.findViewById(R.id.no_internet);
        children_RecyclerView = home.findViewById(R.id.children_recyclerView);

        //strings
        school_id_string = home_accessor.getString("school_code");
        phone_number_string = home_accessor.getString("user_phone_number");


        if(isNetworkAvailable()){
            get_Children_IDs();
        }else{
            no_children_textView.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }
        children_RecyclerView.setHasFixedSize(true);
        children_RecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        children_Adapter = new ChildrenAdapter(getFromDatabase(),getActivity());
        children_RecyclerView.setAdapter(children_Adapter);

        no_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    get_Children_IDs();
                }else{
                    no_children_textView.setVisibility(View.GONE);
                    no_internet.setVisibility(View.VISIBLE);
                }
            }
        });
        return home;
    }

    private void get_Children_IDs() {
        try{
            DatabaseReference get_child_id = FirebaseDatabase.getInstance().getReference("children").child(school_id_string).child(phone_number_string);

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
        DatabaseReference getChild_info = FirebaseDatabase.getInstance().getReference("children").child(school_id_string).child(phone_number_string).child(key);
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
                        string_child_code = key;
                    }
                    Children obj = new Children(sfirst_name,slastname,sclass,sgender,string_child_code);
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

    public ArrayList<Children> getFromDatabase(){
        return  childrenArray;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
