package com.ddt.knoknok.ContactHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ddt.knoknok.Listener.ItemClickListener;
import com.ddt.knoknok.R;

public class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView textname,textphone,contact_letter;
    public ImageView contactletter_image;
    public CheckBox item_checkbox;
    View view;
    ItemClickListener itemClickListener;

    public ContactHolder(@NonNull View itemView) {
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
        textname = itemView.findViewById(R.id.recycler_name);
        textphone = itemView.findViewById(R.id.recycler_phone);
        contact_letter = itemView.findViewById(R.id.contact_firstcharacter);
        contactletter_image = itemView.findViewById(R.id.image_contact);
        item_checkbox = itemView.findViewById(R.id.checkbox_item);

        item_checkbox.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener ic){
        this.itemClickListener = ic;
    }
    private ContactHolder.ClickListener mClickListener;

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }

    //inteface for click listener
    public interface ClickListener {
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }
    public void setOnClickListener(ContactHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}
