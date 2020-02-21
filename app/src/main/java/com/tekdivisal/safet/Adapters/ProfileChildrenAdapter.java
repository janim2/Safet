package com.tekdivisal.safet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tekdivisal.safet.Accessories;
import com.tekdivisal.safet.Child_Details;
import com.tekdivisal.safet.Model.Children;
import com.tekdivisal.safet.R;

import java.util.ArrayList;

public class ProfileChildrenAdapter extends RecyclerView.Adapter<ProfileChildrenAdapter.ViewHolder>{
    ArrayList<Children> itemList;
    Context context;
    private final ArrayList<Integer> selected = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ProfileChildrenAdapter(ArrayList<Children> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public ProfileChildrenAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_child_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final TextView child_name = holder.view.findViewById(R.id.child_name);
        TextView child_class = holder.view.findViewById(R.id.child_class);
        TextView child_gender = holder.view.findViewById(R.id.child_gender);
        CardView child_card_view = holder.view.findViewById(R.id.child_card_view);

        child_name.setText(itemList.get(position).getChild_fname() + " " + itemList.get(position).getChild_lname());
        child_class.setText("Class " + itemList.get(position).getChild_class());
        child_gender.setText(itemList.get(position).getGender());

        child_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent open_child_details = new Intent(v.getContext(), Child_Details.class);
                Accessories child_accessor = new Accessories(v.getContext());
                child_accessor.put("parentcode_adapter", itemList.get(position).getParent_code());
                child_accessor.put("childfname_adapter", itemList.get(position).getChild_fname());
                child_accessor.put("childlname_adapter", itemList.get(position).getChild_lname());
                child_accessor.put("childclass_adapter", itemList.get(position).getChild_class());
                child_accessor.put("childgender_adapter", itemList.get(position).getGender());
                child_accessor.put("childcode_adapter", itemList.get(position).getChild_code());
                v.getContext().startActivity(open_child_details);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
