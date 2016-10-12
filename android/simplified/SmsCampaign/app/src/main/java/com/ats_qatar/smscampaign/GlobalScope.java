package com.ats_qatar.smscampaign;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class GlobalScope extends Application implements Serializable {
    public static SmsCampaignService smsCampaignService;
    public static SmsContainer smsContainer;
    public static Setting setting;


    public static String dateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }


    public static void export(Activity activity) {
        try {
            int requestCode = PackageManager.PERMISSION_GRANTED;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);

            File sdCard = Environment.getExternalStorageDirectory();
            File folder = new File(sdCard.getAbsolutePath(), "SmsCampaign");
            // if external memory exists and folder with name Notes
            if (!folder.exists()) {
                //this will create folder.
                folder.mkdirs();
            }
            // file path to save
            File filepath = new File(folder.getAbsolutePath(), GlobalScope.dateTime() + ".csv");
            filepath.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(filepath.getAbsolutePath());

            StringBuilder stringBuilder = new StringBuilder();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            stringBuilder.append("DateTimeStarted, " +  simpleDateFormat.format(GlobalScope.smsContainer.dateTimeStarted) + "\n");
            stringBuilder.append("DateTimeEnded, " + simpleDateFormat.format(GlobalScope.smsContainer.dateTimeCompleted) + "\n");
            stringBuilder.append("\n");
            stringBuilder.append(
                    "Id, " +
                            "DateTimeCreated, " +
                            "Number, " +
                            "Message, " +
                            "Center, " +
                            "DateTimeSent, " +
                            "Sent, " +
                            "DateTimeDelivered" +
                            "Delivered, " +
                            "Error\n");

            for (Sms sms : GlobalScope.smsContainer.items) {
                stringBuilder.append(sms.id + ",");
                stringBuilder.append(sms.dateTimeCreated + ",");
                stringBuilder.append(sms.number + ",");
                stringBuilder.append(sms.message + ",");
                stringBuilder.append(sms.center + ",");
                stringBuilder.append(sms.dateTimeSent + ",");
                stringBuilder.append(sms.sent + ",");
                stringBuilder.append(sms.dateTimeDelivered + ",");
                stringBuilder.append(sms.delivered + ",");
                stringBuilder.append(sms.error + "\n");
            }

            fileOutputStream.write(stringBuilder.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();

            Toast.makeText(activity, "Save to File Successfully", Toast.LENGTH_LONG).show();
        } catch (Exception exception) {
            Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
