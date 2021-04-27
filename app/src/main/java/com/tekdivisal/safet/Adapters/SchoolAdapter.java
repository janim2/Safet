package com.tekdivisal.safet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v7.widget.RecyclerView;
import com.tekdivisal.safet.Accessories;
import com.tekdivisal.safet.MainActivity;
import com.tekdivisal.safet.Model.Notify;
import com.tekdivisal.safet.Model.School;
import com.tekdivisal.safet.R;

import java.util.ArrayList;

public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.ViewHolder>{
    ArrayList<School> itemList;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public SchoolAdapter(ArrayList<School> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public SchoolAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_name_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(SchoolAdapter.ViewHolder holder, final int position) {
        final TextView school_name = holder.view.findViewById(R.id.school_name);
        school_name.setText(itemList.get(position).getSchool_name().toUpperCase());

        school_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Accessories school_adapter = new Accessories(v.getContext());
                school_adapter.put("hasChoosenSchool",true);
                school_adapter.put("school_code",itemList.get(position).getSchool_code());
                school_adapter.put("school_name",itemList.get(position).getSchool_name());
                school_adapter.put("school_telephone",itemList.get(position).getSchool_phone());
                school_adapter.put("school_email",itemList.get(position).getSchool_email());
                school_adapter.put("school_location",itemList.get(position).getSchool_location());
                Intent main_intent = new Intent(v.getContext(), MainActivity.class);
                main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                v.getContext().startActivity(main_intent);
            }
        });
        }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
