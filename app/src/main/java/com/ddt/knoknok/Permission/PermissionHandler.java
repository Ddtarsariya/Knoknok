package com.ddt.knoknok.Permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHandler implements ActivityCompat.OnRequestPermissionsResultCallback{

    private int REQUEST_CODE = 1;
    public Activity activity;


    public boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if (manager != null){
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        }catch (NullPointerException e){
            return false;
        }
    }

    public void permissionCheck(Context context,Activity activity) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.SEND_SMS) +
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) +
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) +
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) +
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_SMS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.RECEIVE_SMS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.READ_CONTACTS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.SEND_SMS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.READ_PHONE_STATE)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Grant those permissions");
                builder.setCancelable(false);
                builder.setMessage("Send SMS,\nReceive SMS,\nRead Contacts");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{
                                        Manifest.permission.SEND_SMS,
                                        Manifest.permission.READ_SMS,
                                        Manifest.permission.RECEIVE_SMS,
                                        Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.READ_PHONE_STATE,
                                }, REQUEST_CODE
                        );
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.READ_SMS,
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_PHONE_STATE,
                        }, REQUEST_CODE
                );
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if ((grantResults.length > 0 ) &&
                    (grantResults[0]+
                            grantResults[1] +
                            grantResults[2] +
                            grantResults[3] +
                            grantResults[4]
                    == PackageManager.PERMISSION_GRANTED)){
                Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
}
