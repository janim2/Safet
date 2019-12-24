package com.tekdivisal.safet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tekdivisal.safet.Accessories;
import com.tekdivisal.safet.Child_location;
import com.tekdivisal.safet.Model.Children;
import com.tekdivisal.safet.R;

import java.util.ArrayList;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.ViewHolder>{
    ArrayList<Children> itemList;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ChildrenAdapter(ArrayList<Children> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.children_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final TextView name = holder.view.findViewById(R.id.student_name);
        TextView child_class = holder.view.findViewById(R.id.child_class);
        TextView status = holder.view.findViewById(R.id.status);
        CardView childCardView = holder.view.findViewById(R.id.child_cardView);

//        Typeface lovelo =Typeface.createFromAsset(context.getAssets(),  "fonts/lovelo.ttf");
//        name.setTypeface(lovelo);
//        phone_number.setTypeface(lovelo);

        name.setText(itemList.get(position).getChild_fname() + " " + itemList.get(position).getChild_lname());
        child_class.setText("class: " + itemList.get(position).getChild_class());
//        Accessories n = new Accessories(context);
//        String sss_tatus = n.getString("bus_status");
//        if(sss_tatus != null){
//            status.setText(sss_tatus);
//        }else{
//            status.setText("Status");
//        }

        childCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent open_driver_details = new Intent(v.getContext(), Driver_Details.class);
//                v.getContext().startActivity(open_driver_details);
                Intent child_location = new Intent(v.getContext(), Child_location.class);
                child_location.putExtra("from_home_child_fname",itemList.get(position).getChild_fname());
                child_location.putExtra("from_home_child_lname",itemList.get(position).getChild_lname());
                v.getContext().startActivity(child_location);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
