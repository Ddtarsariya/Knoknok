package com.ddt.knoknok.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ddt.knoknok.AddContactActivity;
import com.ddt.knoknok.ContactHolder.ContactHolder;
import com.ddt.knoknok.Listener.ItemClickListener;
import com.ddt.knoknok.MainScreenActivity;
import com.ddt.knoknok.Model.Contact;
import com.ddt.knoknok.R;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactHolder> implements View.OnClickListener,Filterable {

    MainScreenActivity mainScreenActivity;
    List<Contact> modelList;
    List<Contact> modellistfull;

    public ArrayList<Contact> checkedcontact = new ArrayList<>();

    public ContactAdapter(MainScreenActivity mainScreenActivity, List<Contact> modelList) {
        this.mainScreenActivity = mainScreenActivity;
        this.modelList = modelList;
        modellistfull = new ArrayList<>(modelList);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_contact_list, parent, false);

        ContactHolder contactHolder = new ContactHolder(itemView);
        //handle itemclcick
        contactHolder.setOnClickListener(new ContactHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String name = modelList.get(position).getName();
                String phone = modelList.get(position).getPhone();
               // Toast.makeText(mainScreenActivity, name + "\n" + phone, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainScreenActivity);
                String[] options = {"Update", "Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            String id = modelList.get(position).getId();
                            String name = modelList.get(position).getName();
                            String phone = modelList.get(position).getPhone();

                            Intent intent = new Intent(mainScreenActivity, AddContactActivity.class);
                            intent.putExtra("pid", id);
                            intent.putExtra("pname", name);
                            intent.putExtra("pphone", phone);
                            mainScreenActivity.startActivity(intent);

                        }
                        if (which == 1) {
                            mainScreenActivity.deleteData(position);
                        }
                    }
                }).create().show();
            }
        });
        return contactHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        //bind views
        holder.textname.setText(modelList.get(position).getName());
        holder.textphone.setText(modelList.get(position).getPhone());
        holder.contact_letter.setText(String.valueOf(modelList.get(position).getName()));
        GradientDrawable backgroundGradient = (GradientDrawable) holder.contactletter_image.getBackground().mutate();
        String character_contact = String.valueOf(modelList.get(position).getName().charAt(0));

        switch (character_contact) {
            case "A":
            case "H":
            case "O":
                backgroundGradient.setColor(Color.parseColor("#7850DC"));//purple
                break;
            case "B":
            case "I":
            case "P":
            case "V":
                backgroundGradient.setColor(Color.parseColor("#79d70f"));//green
                break;
            case "C":
            case "J":
            case "Q":
            case "W":
                backgroundGradient.setColor(Color.parseColor("#207dff"));//Blue
                break;
            case "D":
            case "K":
            case "R":
            case "X":
                backgroundGradient.setColor(Color.parseColor("#ffd31d"));//Yellow
                break;
            case "E":
            case "L":
            case "S":
            case "Y":
                backgroundGradient.setColor(Color.parseColor("#f87829"));//orange
                break;
            case "F":
            case "M":
            case "T":
            case "Z":
                backgroundGradient.setColor(Color.parseColor("#cccccc"));//Gray
                break;
            case "G":
            case "N":
            case "U":
                backgroundGradient.setColor(Color.parseColor("#f54291"));//pink
                break;
            default:
                backgroundGradient.setColor(Color.parseColor("#cecece"));
        }
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                CheckBox checkBox = (CheckBox) v;
                //check check or not
                if (checkBox.isChecked()) {
                    checkedcontact.add(modelList.get(pos));
                } else if (!checkBox.isChecked()) {
                    checkedcontact.remove(modelList.get(pos));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public Filter getFilter() {
        return modelFilter;
    }
    private Filter modelFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Contact> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0 ){
                filteredList.addAll(modellistfull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Contact item : modellistfull){
                    if (item.getName().toLowerCase().contains(filterPattern) || item.getPhone().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            modelList.clear();
            modelList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
