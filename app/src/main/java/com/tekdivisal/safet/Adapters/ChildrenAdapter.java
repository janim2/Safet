package com.tekdivisal.safet.Adapters;

import android.content.Context;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
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
import com.tekdivisal.safet.Accessories;
import com.tekdivisal.safet.Assign_bus_To_Child;
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
    public void onBindViewHolder(ViewHolder holder,final int position) {
        final TextView name = holder.view.findViewById(R.id.student_name);
        TextView child_class = holder.view.findViewById(R.id.child_class);
        TextView status = holder.view.findViewById(R.id.status);
        CardView childCardView = holder.view.findViewById(R.id.child_cardView);

        name.setText(itemList.get(position).getChild_fname() + " " + itemList.get(position).getChild_lname());
        child_class.setText("class: " + itemList.get(position).getChild_class());

        if(!itemList.get(position).getIsAssigned_bus().equals("No")){
            SetStatusText(status, itemList.get(position).getAssigned_bus());
        }else{
            status.setText("Unassigned");
        }

        childCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent open_driver_details = new Intent(v.getContext(), Driver_Details.class);
//                v.getContext().startActivity(open_driver_details);
                String isAssigned = itemList.get(position).getIsAssigned_bus();
                if(!isAssigned.equals("")){
                    Accessories adapter_class = new Accessories(v.getContext());
//                    if(isAssigned.equals("No")){
//                        Intent assign_child = new Intent(v.getContext(), Assign_bus_To_Child.class);
//                        adapter_class.put("from_home_child_fname",itemList.get(position).getChild_fname());
//                        adapter_class.put("from_home_child_lname",itemList.get(position).getChild_lname());
//                        adapter_class.put("child_code",itemList.get(position).getChild_code());
//                        adapter_class.put("isAssigned_bus","No");
//                        v.getContext().startActivity(assign_child);
//                    }else{
                        Intent child_location = new Intent(v.getContext(), Child_location.class);
                        adapter_class.put("from_home_child_fname",itemList.get(position).getChild_fname());
                        adapter_class.put("from_home_child_lname",itemList.get(position).getChild_lname());
                        adapter_class.put("child_code",itemList.get(position).getChild_code());
                        adapter_class.put("isAssigned_bus",isAssigned);
                        v.getContext().startActivity(child_location);
//                    }
                }

            }
        });
    }
    String trip_status;
    private void SetStatusText(final TextView status, String assigned_bus) {
        Accessories adapter_accessor = new Accessories(context);
        String school_id_string = adapter_accessor.getString("school_code");
        String parent_code_string = adapter_accessor.getString("user_phone_number");

        DatabaseReference bus_status = FirebaseDatabase.getInstance().getReference("trip_status")
                .child(school_id_string).child(assigned_bus);
            bus_status.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("status")){
                                trip_status = child.getValue().toString();
                            }
                        }
                        status.setText(trip_status);
                    }else{
//                    Toast.makeText(getActivity(),"Cannot get ID",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
