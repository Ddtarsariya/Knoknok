package com.ddt.knoknok.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ddt.knoknok.Adapter.Sended_Sms_Adapter;
import com.ddt.knoknok.Model.Sended_Sms_Model;
import com.ddt.knoknok.Permission.PermissionHandler;
import com.ddt.knoknok.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class SendedSmsFragment extends Fragment {

    private static final String TAG = "Frag";
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView textView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getEmail());
    private CollectionReference collectionReference = db.collection("Users");
    List<Sended_Sms_Model> modelList = new ArrayList<>();
    Sended_Sms_Adapter adapter;
    PermissionHandler permissions = new PermissionHandler();



    public SendedSmsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SendedSmsFragment newInstance() {
        SendedSmsFragment fragment = new SendedSmsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sended_sms, container, false);
        recyclerView = view.findViewById(R.id.SendedSMS_Recyclerview);
        progressBar = view.findViewById(R.id.recycler_progress);
        textView = view.findViewById(R.id.emptyornot);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showData();

    }

    private void showData() {
        progressBar.setVisibility(View.VISIBLE);
        documentReference.collection("Sended_Sms").orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            textView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        textView.setVisibility(View.GONE);
                        for (DocumentSnapshot doc : task.getResult()){
                            Sended_Sms_Model sended_sms_model = new Sended_Sms_Model(
                                    doc.getString("id"),
                                    doc.getString("msg"),
                                    doc.getString("date")
                            );
                            modelList.add(sended_sms_model);
                        }
                        progressBar.setVisibility(View.GONE);
                        adapter= new Sended_Sms_Adapter(SendedSmsFragment.this,modelList);
                        recyclerView.setAdapter(adapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void showData(String smsDate, String smsBody) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_received_sms);
        dialog.setCancelable(true);
        TextView textDate = dialog.findViewById(R.id.smsDate);
        TextView textbody = dialog.findViewById(R.id.smsBody);
        MaterialCardView cardView = dialog.findViewById(R.id.cardFrom);

        cardView.setVisibility(View.GONE);

        textDate.setText("Date : "+smsDate);
        textbody.setText(smsBody);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public void deleteData(int index) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        if (permissions.isNetworkAvailable(getContext())){
            documentReference.collection("Sended_Sms").document(modelList.get(index).getId())
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            modelList.clear();
                            recyclerView.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Deleted..", Toast.LENGTH_SHORT).show();
                            showData();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            documentReference.collection("Sended_Sms").document(modelList.get(index).getId())
                    .delete();
            modelList.clear();
            Toast.makeText(getContext(), "Deleted..", Toast.LENGTH_SHORT).show();
            showData();
        }
    }

    public void CreateDeleteDialogue(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            String[] options = {"Delete", "Delete All"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    deleteData(position);
                }
                if (which == 1) {
                    deleteAlldata();
                }
            }
        }).create().show();
    }

    private void deleteAlldata() {
        documentReference.collection("Sended_Sms").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                delteeAllSendedSms(new String[]{document.getId()});
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void delteeAllSendedSms(String[] id) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        WriteBatch batch = db.batch();
        if (!permissions.isNetworkAvailable(getContext())) {
            try {
                for (String documentId : id) {
                    batch.delete(documentReference.collection("Sended_Sms").document(documentId));
                }
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                modelList.clear();
                showData();
                checkEmptyOrNot();
                return;

            } catch(Exception e){
                e.printStackTrace();
            }
        }else {
            try {
                for (String documentId : id) {
                    batch.delete(documentReference.collection("Sended_Sms").document(documentId));
                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            modelList.clear();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            showData();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                }
                checkEmptyOrNot();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkEmptyOrNot() {
        documentReference.collection("Sended_Sms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getContext(), "Deleted..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}