package com.ddt.knoknok;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ddt.knoknok.Fragment.MessageActivity;
import com.ddt.knoknok.Fragment.SendedSmsFragment;
import com.ddt.knoknok.Permission.PermissionHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.Manifest.*;
import static android.Manifest.permission.SEND_SMS;

public class Send_Sms_Text extends AppCompatActivity {

    private TextInputLayout smsText;
    TextView whylimit;
    private Button cancel,send;
    private static final String TAG = "Send_Sms_Text";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getEmail());
    PermissionHandler permissions = new PermissionHandler();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send__sms__text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Send Text");
        whylimit = findViewById(R.id.textwhy);
        smsText = findViewById(R.id.sending_text);
        cancel = findViewById(R.id.btn_Cancel);
        send = findViewById(R.id.btn_Send);

        cancel.setOnClickListener(view->{
                startActivity(new Intent(this,MainScreenActivity.class));
                finish();
        });
        send.setOnClickListener(v -> {
           sendclickcheck();
        });
        if (ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.SEND_SMS
                    }, 1
            );
            send.setEnabled(false);
        }
    }

    private void sendclickcheck() {
        String textsms = smsText.getEditText().getText().toString().trim();
            if (textsms.isEmpty()) {
                smsText.setError("Text can't be empty!");
            } else if (textsms.length() > 120) {
                smsText.setError("Maximum length reached!");
            }else {
                sendSMStoAllnumber();
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS ) != PackageManager.PERMISSION_GRANTED){
            send.setEnabled(false);
        }else {
            send.setEnabled(true);
        }
    }

    private void sendSMStoAllnumber() {
        SmsManager smsManager = SmsManager.getDefault();
        String textsms = smsText.getEditText().getText().toString().trim();
        String currentDate = new SimpleDateFormat("d.M.yy [ h:mm a ]", Locale.getDefault()).format(new Date());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("If you don't have an valid SMS pack then Money deducted from your mobile.");
        builder.setCancelable(false);
        builder.setPositiveButton("Send",(dialogInterface, i) -> {
            documentReference.collection("Contacts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()){
                                    Toast toast = Toast.makeText(Send_Sms_Text.this,"No Contacts Available!",Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER,0,0);
                                    toast.show();
                                    return;
                                }else {
                                    Toast.makeText(Send_Sms_Text.this, "message sended successfully ", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Send_Sms_Text.this,MessageActivity.class));
                                    finish();
                                    saveSmsData(currentDate,textsms);
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        smsManager.sendTextMessage(document.getString("phone"), null,
                                                "/*/Knoknok/*/\n--Alert--\n" + textsms + ".", null, null);
                                    }
                                }
                            } else {
                                Toast.makeText(Send_Sms_Text.this, "Error geting mobile number", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Send_Sms_Text.this, "Failed to read mobile no.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Cancel",(dialogInterface, i) -> {
            dialogInterface.cancel();
        });
        builder.create().show();

    }

    public void saveSmsData(String pDate,String pmsgBody) {
        String id = UUID.randomUUID().toString();
        Map<String,Object> doc = new HashMap<>();
        doc.put("id",id);
        doc.put("msg",pmsgBody);
        doc.put("date",pDate);
        documentReference.collection("Sended_Sms").document(id)
                    .set(doc);
    }

    public void showmessageinfo(View view) {
        String text = whylimit.getText().toString();
        if (text == "Why max lenght is 120?"){
            whylimit.setText("Dialogue will not appear to any mobile phone when you send above 120 character.");
        }else {
            whylimit.setText("Why max lenght is 120?");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && 
            grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Well i can't do anything", Toast.LENGTH_SHORT).show();
        }
        return;
    }
}