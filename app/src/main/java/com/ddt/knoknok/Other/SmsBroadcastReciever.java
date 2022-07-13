package com.ddt.knoknok.Other;

import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.ddt.knoknok.Fragment.MessageActivity;
import com.ddt.knoknok.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SmsBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];

                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }

                if (messages[0].getMessageBody().startsWith("/*/Knoknok/*/") && messages[0].getMessageBody().endsWith(".")){
                    Dialog dialog = new Dialog(context);

                    String msgFrom = messages[0].getOriginatingAddress();

                    String msgafterReplace = messages[0].getMessageBody().replace("/*/Knoknok/*/","")
                            .replace("--Alert--","").replaceAll(".$","").trim();

                    String currentDate = new SimpleDateFormat("d.M.yy [ h:mm a ]", Locale.getDefault()).format(new Date());

                    Intent SaveIntent = new Intent(context,MessageActivity.class);
                    SaveIntent.putExtra("pmsgFrom",msgFrom);
                    SaveIntent.putExtra("pmsgBody", msgafterReplace);
                    SaveIntent.putExtra("pDate",currentDate);


                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_dialogue);
                    dialog.setCancelable(false);
                    Button savebtn = dialog.findViewById(R.id.Alert_save);
                    Button cancelbtn = dialog.findViewById(R.id.Alert_cancel);
                    ImageView imageView = dialog.findViewById(R.id.imagealert);
                    TextView textfrom = dialog.findViewById(R.id.textFrom);
                    TextView textbody = dialog.findViewById(R.id.textBody);

                    textfrom.setText(msgFrom);
                    textbody.setText(msgafterReplace);

                    savebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent = new Intent(context, MessageActivity.class);
                            intent.putExtra("pmsgFrom",msgFrom);
                            intent.putExtra("pmsgBody", msgafterReplace);
                            intent.putExtra("pDate",currentDate);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });
                    cancelbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }

                    });

                    if (Build.VERSION.SDK_INT >= 26)
                        Objects.requireNonNull(dialog.getWindow())
                                .setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                    else
                        Objects.requireNonNull(dialog.getWindow())
                                .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    dialog.show();
                }
            }

        }
    }

}
