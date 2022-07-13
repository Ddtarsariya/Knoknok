package com.ddt.knoknok;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ddt.knoknok.Permission.PermissionHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AddContactActivity extends AppCompatActivity {

    private static final int CONTACT_PICKER_RESULT = 0;
    public TextInputEditText input_name, input_phone;
    TextInputLayout textInputLayout;
    ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DocumentReference documentReference = db.collection("Users").document(mAuth.getCurrentUser().getEmail());
    private CollectionReference collectionReference = db.collection("Users");
    private static final String TAG = "AddContactActivity";
    String pname,pphone,pid;
    private static final int READ_CONTACTS_CODE = 100;
    PermissionHandler permissions = new PermissionHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        setTitle("Add Contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        input_name = findViewById(R.id.input_name);
        input_phone = findViewById(R.id.input_phone);
        progressBar = findViewById(R.id.progress);
        textInputLayout = findViewById(R.id.textInputLayout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            //Update
            setTitle("Update Contact");
            //get Data
            pid = bundle.getString("pid");
            pname = bundle.getString("pname");
            pphone = bundle.getString("pphone");
            input_name.setText(pname);
            input_phone.setText(pphone);
        }
        textInputLayout.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chechselfpermission(Manifest.permission.READ_CONTACTS,READ_CONTACTS_CODE);
                if (ContextCompat.checkSelfPermission(AddContactActivity.this,Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED){
                   // Toast.makeText(AddContactActivity.this, "Well i can't read contact", Toast.LENGTH_SHORT).show();
                }else {
                    Intent contactpickerintent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(contactpickerintent,CONTACT_PICKER_RESULT);
                }
            }
        });
    }

    private void contactErrorcheck() {

        String regX = "[6-9]{1}[0-9]{9}";
        String name = input_name.getEditableText().toString().trim();
        String phone = input_phone.getEditableText().toString().trim();
       // String id = pid;

        if (name.isEmpty()) {
            input_name.setError("Name is Required!");
            input_name.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            input_phone.setError("Required!");
            input_phone.requestFocus();
            return;
        }
       if (phone.length() != 10)  {
           input_phone.setError("Enter Valid phone number!");
           input_phone.requestFocus();
           return;
        }if (!phone.matches(regX)){
            input_phone.setError("Enter Valid phone!");
            input_phone.requestFocus();
            return;
        }
       if (phone.equals("9876543210") || phone.contains("00000") || phone.contains("11111") || phone.contains("22222") ||
               phone.contains("33333") || phone.contains("44444") || phone.contains("55555") || phone.contains("66666") ||
               phone.contains("77777") ||phone.contains("88888") ||phone.contains("99999") )  {
           input_phone.setError("Enter Valid phone!");
           input_phone.requestFocus();
           return;
       }
       try {
           BigDecimal ph = BigDecimal.valueOf(Long.parseLong(phone));
       }catch (NumberFormatException e){
           input_phone.setError("Enter valid phone number!");
           input_phone.requestFocus();
           return;
       }
            Bundle bundle = getIntent().getExtras();
            if (bundle != null){
                //Updating
                updateData(name,phone,name.toLowerCase());
            }else {
                //adding new
                //Updating
                UploadData(name, phone, name.toLowerCase());
            }

    }

    private void UploadData(String name, String phone, String toLowerCase) {
        progressBar.setVisibility(View.VISIBLE);
        //String id = UUID.randomUUID().toString();
        Map<String,Object> doc = new HashMap<>();
       // doc.put("id",id);
        doc.put("name",name);
        doc.put("phone",phone);
        doc.put("search",toLowerCase);
        if (!permissions.isNetworkAvailable(this)){
            documentReference.collection("Contacts").document(phone)
                    .set(doc);
            Toast.makeText(AddContactActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            startActivity(new Intent(AddContactActivity.this,MainScreenActivity.class));
            finish();
            return;
        }
        documentReference.collection("Contacts").document(phone)
                .set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AddContactActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(AddContactActivity.this,MainScreenActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddContactActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void  updateData(String name, String phone, String toLowerCase) {
        progressBar.setVisibility(View.VISIBLE);
        if (!permissions.isNetworkAvailable(this)){
            documentReference.collection("Contacts").document(phone)
                    .update("name",name,"search",name.toLowerCase(),"phone",phone);
            Toast.makeText(AddContactActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            finish();
            startActivity(new Intent(AddContactActivity.this,MainScreenActivity.class));
            return;
        }
        documentReference.collection("Contacts").document(phone)
                .update("name",name,"search",name.toLowerCase(),"phone",phone)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddContactActivity.this, "Updated....", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddContactActivity.this,MainScreenActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddContactActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chechselfpermission(String permission,int requestCode) {
        if (ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);
        }else {
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               // Log.d(TAG, "ReadContact Permission Granted");
            }else {
                Toast.makeText(this, "Well i can't read Contacts ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_contact, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       // Log.d(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case R.id.save:
                contactErrorcheck();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CONTACT_PICKER_RESULT){
            if(resultCode == Activity.RESULT_OK){

                Uri uri = data.getData();
                ContentResolver contentResolver = getContentResolver();
                Cursor contentCursor = contentResolver.query(uri, null, null,null, null);

                if(contentCursor.moveToFirst()){
                    String id = contentCursor.getString(contentCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    String hasPhone =
                            contentCursor.getString(contentCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1"))
                    {
                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
                        phones.moveToFirst();
                        String contactNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                      //  Log.d(TAG, "The phone number is "+ contactNumber+ "id"+name);
                        contactNumber.trim();

                        if (contactNumber.startsWith("+91") || contactNumber.contains(" ")){
                            String contact = contactNumber.replace("+91","").replaceAll("\\s","");
                            input_phone.setText(contact.trim());
                            input_name.setText(name);
                        }
                    }else {
                        Toast.makeText(this, "This is a empty contact", Toast.LENGTH_SHORT).show();
                    }
                }

                super.onActivityResult(requestCode, resultCode  , data);
            }
        }

    }

}
