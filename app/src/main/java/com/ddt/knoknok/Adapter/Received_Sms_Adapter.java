package com.ddt.knoknok.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ddt.knoknok.AddContactActivity;
import com.ddt.knoknok.ContactHolder.Received_Sms_Holder;
import com.ddt.knoknok.Fragment.MessageActivity;
import com.ddt.knoknok.Fragment.ReceivedSmsFragment;
import com.ddt.knoknok.Model.Sms_Model;
import com.ddt.knoknok.R;

import java.util.List;


public class Received_Sms_Adapter extends RecyclerView.Adapter<Received_Sms_Holder>{

    ReceivedSmsFragment received_sms;
    List<Sms_Model> modelList;
    MessageActivity messageActivity;

    public Received_Sms_Adapter(ReceivedSmsFragment received_sms,List<Sms_Model> modelList) {
        this.received_sms = received_sms;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public Received_Sms_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_sms_list, parent, false);

        Received_Sms_Holder received_sms_holder = new Received_Sms_Holder(itemView);
        //handle itemclcick
        received_sms_holder.setOnClickListener(new Received_Sms_Holder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String smsBody = modelList.get(position).getSmsbody();
                String smsFrom = modelList.get(position).getSmsfrom();
                String smsDate = modelList.get(position).getDate();

                received_sms.showData(position, smsDate, smsFrom, smsBody);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                received_sms.CreateDeleteDialogue(position);
            }
        });
            return received_sms_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Received_Sms_Holder holder, int position) {
        //bind views
        holder.textsms.setText(modelList.get(position).getSmsbody());
        holder.textfrom.setText(modelList.get(position).getSmsfrom());
        holder.textdate.setText(String.valueOf(modelList.get(position).getDate()));

        GradientDrawable backgroundGradient = (GradientDrawable)holder.image_mesage.getBackground().mutate();
        backgroundGradient.setColor(Color.parseColor("#BCBCBC"));
    }

    @Override
    public int getItemCount() {
        if (modelList == null)
            return 0;
        else
            return  modelList.size();

    }
}
