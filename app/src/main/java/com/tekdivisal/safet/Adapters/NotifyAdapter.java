package com.tekdivisal.safet.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tekdivisal.safet.Model.Notify;
import com.tekdivisal.safet.R;

import java.util.ArrayList;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.ViewHolder>{
    ArrayList<Notify> itemList;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public NotifyAdapter(ArrayList<Notify> itemList, Context context){
        this.itemList  = itemList;
        this.context  = context;
    }

    @Override
    public NotifyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_attachment,parent,false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }


    @Override
    public void onBindViewHolder(NotifyAdapter.ViewHolder holder, final int position) {
        final TextView title = holder.view.findViewById(R.id.notify_title);
        ImageView image = holder.view.findViewById(R.id.notify_image);
//        TextView time = holder.view.findViewById(R.id.);
        TextView message = holder.view.findViewById(R.id.notify_message);

        String the_image = itemList.get(position).getImageType();
            if(the_image.equals("WM")){//stands for welcome Notification
                image.setImageDrawable(holder.view.getResources().getDrawable(R.drawable.logo));
            }
//            else if(the_image.equals("US")){
//                image.setImageDrawable(holder.view.getResources().getDrawable(R.drawable.save));
//            }
//            else if(the_image.equals("AD")){
//                image.setImageDrawable(holder.view.getResources().getDrawable(R.drawable.driver));
//            }
//            else if(the_image.equals("SN")){
//                image.setImageDrawable(holder.view.getResources().getDrawable(R.drawable.security));
//            }
//            else if(the_image.equals("DN")){
//                image.setImageDrawable(holder.view.getResources().getDrawable(R.drawable.distress));
//            }
//            else{
//                image.setImageDrawable(holder.view.getResources().getDrawable(R.drawable.logo));
//            }

        title.setText(itemList.get(position).getTitle());
            message.setText(itemList.get(position).getMessage());
        }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


}
