package com.ddt.knoknok.ContactHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ddt.knoknok.R;

public class Received_Sms_Holder extends RecyclerView.ViewHolder{

    public TextView textfrom,textsms,textdate;
    public ImageView image_mesage;
    View view;

    public Received_Sms_Holder(@NonNull View itemView) {
        super(itemView);

        view  = itemView;
        //itemclick
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v,getAdapterPosition());
            }
        });
        //item long click
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v,getAdapterPosition());
                return true;
            }
        });
        //initialize view with model single_contact_list.xml
        textfrom = itemView.findViewById(R.id.recycler_from);
        textsms = itemView.findViewById(R.id.recycler_smstext);
        textdate = itemView.findViewById(R.id.recycler_date);
        image_mesage = itemView.findViewById(R.id.image_message);
    }

    private Received_Sms_Holder.ClickListener mClickListener;

    //inteface for click listener
    public interface ClickListener {
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }
    public void setOnClickListener(Received_Sms_Holder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}
