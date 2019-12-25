package com.tekdivisal.safet.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tekdivisal.safet.Model.Facilities;
import com.tekdivisal.safet.R;

import java.util.ArrayList;

public class Facilities_Adapter extends RecyclerView.Adapter<Facilities_Adapter.ViewHolder>{
    ArrayList<Facilities> itemList;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public Facilities_Adapter(ArrayList<Facilities> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public Facilities_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.facilities_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(Facilities_Adapter.ViewHolder holder, final int position) {
        ImageView image = holder.view.findViewById(R.id.facility_image);
        TextView name = holder.view.findViewById(R.id.facility_name);

        Uri imageurl = Uri.parse(itemList.get(position).getImageUrl());
        Picasso.with(context).load(imageurl).into(image);
        name.setText(itemList.get(position).getFacility_name());
        }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}

