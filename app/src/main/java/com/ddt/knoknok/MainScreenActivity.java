package com.ddt.knoknok;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ddt.knoknok.Adapter.ContactAdapter;
import com.ddt.knoknok.Fragment.MessageActivity;
import com.ddt.knoknok.Model.Contact;
import com.ddt.knoknok.Permission.PermissionHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainScreenActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DocumentReference documentReference = db.collection("Users").document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()));
    private CollectionReference collectionReference = db.collection("Users");
    SwipeRefreshLayout swipeRefreshLayout;
    List<Contact> modelList = new ArrayList<>();
    ContactAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    StringBuffer sb = null;
    PermissionHandler permissions = new PermissionHandler();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

         mAuth = FirebaseAuth.getInstance();
         findViewById(R.id.floatingAddNumber).setOnClickListener(this);
         findViewById(R.id.floating_send_sms).setOnClickListener(this);
         findViewById(R.id.floatingshowMessages).setOnClickListener(this);
         swipeRefreshLayout = findViewById(R.id.swipe_Refresh);
         recyclerView = findViewById(R.id.myContact_Recyclerview);
         recyclerView.setHasFixedSize(true);
         layoutManager = new LinearLayoutManager(this);
         recyclerView.setLayoutManager(layoutManager);
         textView = findViewById(R.id.Message_contacts);
        showData();
         permissions.permissionCheck(MainScreenActivity.this,this);
         displaypermissionandpower();
         swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                 modelList.clear();
                 swipeRefreshLayout.setRefreshing(false);
                 showData();
                 }
         });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    private void displaypermissionandpower() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
        if (!Settings.canDrawOverlays(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainScreenActivity.this);
                    builder.setTitle("Grant this permission");
                    builder.setMessage("Display over other apps,for the dialogue purpose");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        }
        else if(!isIgnoringBatteryOptimizations){
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        }
    }

    private void showData() {
        swipeRefreshLayout.setRefreshing(true);
        documentReference.collection("Contacts").orderBy("name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("No Contacts");
                            swipeRefreshLayout.setRefreshing(false);
                            return;
                        }
                        textView.setVisibility(View.GONE);
                        Log.d(TAG, "onComplete: ");
                            for (DocumentSnapshot doc : task.getResult()){
                                Contact contact = new Contact(
                                        //doc.getString("id"),
                                        doc.getString("name"),
                                        doc.getString("phone"));
                                modelList.add(contact);
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            adapter= new ContactAdapter(MainScreenActivity.this,modelList);
                            recyclerView.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public  void deleteData(int index){
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.GONE);
        if (!permissions.isNetworkAvailable(MainScreenActivity.this)){
            documentReference.collection("Contacts").document(modelList.get(index).getPhone())
                    .delete();
            swipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);
            modelList.clear();
            showData();
            return;
        }
        documentReference.collection("Contacts").document(modelList.get(index).getPhone())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        modelList.clear();
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.VISIBLE);
                        Toast.makeText(MainScreenActivity.this, "Deleted..", Toast.LENGTH_SHORT).show();
                        showData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                recyclerView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void delteAllContact(String[] phone) {
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.GONE);
        //Log.d(TAG, "delteAllContact: "+phone);
        WriteBatch batch = db.batch();
        try {
            for (String documentId : phone){
                batch.delete(documentReference.collection("Contacts").document(documentId));
                if (!permissions.isNetworkAvailable(MainScreenActivity.this)){
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.VISIBLE);
                    modelList.clear();
                    showData();
                }
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        modelList.clear();
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.VISIBLE);
                        showData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutmenu:
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                GoogleSignInClient googleSignInClient =GoogleSignIn.getClient(this,gso) ;
                googleSignInClient.signOut();

                    FirebaseAuth.getInstance().signOut();
                    finishAffinity();
                    finish();
                    startActivity(new Intent(this, LoginActivity.class));
                break;
            case  R.id.deleteall_Contact:
                //sb = new StringBuffer();
                swipeRefreshLayout.setRefreshing(true);
                recyclerView.setVisibility(View.GONE);
                for (Contact c:adapter.checkedcontact){
                   // Log.d(TAG,"He"+ c.getPhone());
                    documentReference.collection("Contacts").document(c.getPhone()).delete();
                }
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.VISIBLE);
                modelList.clear();
                showData();
                if (adapter.checkedcontact.size() == 0){
                    documentReference.collection("Contacts").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            //    Log.d(TAG, document.getId());
                                            delteAllContact(new String[]{document.getId()});
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
                break;
            case  R.id.contact_search:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("Type here to search");
                searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return true;
                    }
                });
                searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        showData();
                        return false;
                    }
                });
                break;
            case R.id.Profile:
                profile();
                break;
        }
        return  true;
    }

    private void profile() {
        Dialog dialog;
        String email = mAuth.getCurrentUser().getEmail();
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_layout);
        dialog.setCancelable(false);
        TextView emailProfile = dialog.findViewById(R.id.email_Profile);
        ImageView close_profile = dialog.findViewById(R.id.profile_close);
        emailProfile.setText(email);
        close_profile.setOnClickListener(v -> {
            dialog.dismiss();
        });
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.floatingAddNumber:
                    startActivity(new Intent(this,AddContactActivity.class));
                    break;
                case R.id.floating_send_sms:
                    startActivity(new Intent(this,Send_Sms_Text.class));
                    break;
                case R.id.floatingshowMessages:
                    startActivity(new Intent(this, MessageActivity.class));
                    break;
            }
        }

    public void ClickedButton(View view) {
        sb = new StringBuffer();
        for (Contact c:adapter.checkedcontact){
            sb.append(c.getName());
            sb.append("\n");
        }
        if (adapter.checkedcontact.size() > 0){
            Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
        }
    }
}
