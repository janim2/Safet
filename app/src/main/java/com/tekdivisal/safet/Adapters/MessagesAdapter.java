package com.tekdivisal.safet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v7.widget.RecyclerView;
import com.tekdivisal.safet.Model.Messages;
import com.tekdivisal.safet.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>{
    ArrayList<Messages> itemList;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public MessagesAdapter(ArrayList<Messages> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(MessagesAdapter.ViewHolder holder, final int position) {
        final TextView title = holder.view.findViewById(R.id.messages_title);
        ImageView image = holder.view.findViewById(R.id.messages_image);
//        TextView time = holder.view.findViewById(R.id.);
        TextView message = holder.view.findViewById(R.id.messages_message);
        TextView location = holder.view.findViewById(R.id.location_message);
        TextView date = holder.view.findViewById(R.id.date_message);
        TextView time = holder.view.findViewById(R.id.time_message);

//        String the_image = itemList.get(position).getImage();
//                if (the_image.equals("")) {//stands for welcome Notification
                    image.setImageDrawable(holder.view.getResources().getDrawable(R.drawable.message));
//                }
//                else if (the_image.equals("")) {
//                    image.setImageDrawable(holder.view.getResources().getDrawable(R.drawable.finish_line));
//                }
        title.setText(itemList.get(position).getMessage_title());
        message.setText(itemList.get(position).getMessage_message());

        try{
            if(!itemList.get(position).getLocation().equals("")){
                location.setVisibility(View.VISIBLE);
                location.setText(itemList.get(position).getLocation());
            }
            if(!itemList.get(position).getDate().equals("")){
                date.setVisibility(View.VISIBLE);
//            Date date_ = null;
//            try {
//                date_ = new SimpleDateFormat("yyyy-MMM-dd").parse(itemList.get(position).getDate());
//                date.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date_));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
                date.setText(itemList.get(position).getDate());
            }
            if(!itemList.get(position).getTime().equals("")){
                time.setVisibility(View.VISIBLE);
                time.setText(itemList.get(position).getTime());
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
