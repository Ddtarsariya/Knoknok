package com.ddt.knoknok.Adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ddt.knoknok.ContactHolder.Sended_Sms_Holder;
import com.ddt.knoknok.Fragment.MessageActivity;
import com.ddt.knoknok.Fragment.SendedSmsFragment;
import com.ddt.knoknok.Model.Sended_Sms_Model;
import com.ddt.knoknok.R;

import java.util.List;

public class Sended_Sms_Adapter extends RecyclerView.Adapter<Sended_Sms_Holder> {

    SendedSmsFragment sendedSmsFragment;
    List<Sended_Sms_Model> modelList;
    MessageActivity messageActivity;

    public Sended_Sms_Adapter(SendedSmsFragment sendedSmsFragment, List<Sended_Sms_Model> modelList) {
        this.sendedSmsFragment = sendedSmsFragment;
        this.modelList = modelList;
    }


    @NonNull
    @Override
    public Sended_Sms_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_sendedsms, parent, false);
        Sended_Sms_Holder sended_sms_holder = new Sended_Sms_Holder(itemView);

        sended_sms_holder.setOnClickListener(new Sended_Sms_Holder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String smsBody = modelList.get(position).getSmsbody();
                String smsDate = modelList.get(position).getDate();

                sendedSmsFragment.showData(smsDate, smsBody);
                //Toast.makeText(messageActivity, "Id:-"+smsid+"\n"+"Date :-"+smsDate + "\n" + "Msg :-" + smsBody + "\n" +"From:-"+ smsFrom, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                sendedSmsFragment.CreateDeleteDialogue(position);
            }
        });
        return sended_sms_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Sended_Sms_Holder holder, int position) {
        holder.textsms.setText(modelList.get(position).getSmsbody());
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
