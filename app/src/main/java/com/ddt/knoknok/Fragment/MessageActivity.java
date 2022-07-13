package com.ddt.knoknok.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ddt.knoknok.Fragment.ui.main.SectionsPagerAdapter;
import com.ddt.knoknok.MainScreenActivity;
import com.ddt.knoknok.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getEmail());
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        ImageView back = findViewById(R.id.back);
        String pmsgFrom,pmsgBody,pDate;

        back.setOnClickListener(view -> {
            onBackPressed();
        });

        Bundle bundle = getIntent().getExtras();
        
        if (bundle != null){
            pmsgFrom = bundle.getString("pmsgFrom");
            pmsgBody = bundle.getString("pmsgBody");
            pDate = bundle.getString("pDate");
            viewPager.setCurrentItem(1);
            saveSmsData(pmsgFrom,pmsgBody,pDate);
        }
        
    }
    private void saveSmsData(String pmsgForm, String pmsgBody, String pDate) {
        String id = UUID.randomUUID().toString();
        Map<String,Object> doc = new HashMap<>();
        doc.put("id",id);
        doc.put("from",pmsgForm);
        doc.put("msg",pmsgBody);
        doc.put("date",pDate);
        documentReference.collection("Received_Sms").document(id)
                    .set(doc);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainScreenActivity.class));
        finish();
    }
}