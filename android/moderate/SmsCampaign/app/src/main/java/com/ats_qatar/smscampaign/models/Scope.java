package com.ats_qatar.smscampaign.models;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.ats_qatar.smscampaign.services.SmsCampaignService;
import com.ats_qatar.smscampaign.services.SmsContainer;
import com.ats_qatar.smscampaign.services.SmsDispatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Scope extends Application implements Serializable {
    public static SmsCampaignService smsCampaignService;
    public static SmsContainer smsContainer;
    public static Setting setting;
    public static SmsDispatcher smsDispatcher;


    public static  boolean isServiceRunning(Activity activity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static long getNewId() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

//    public static void export(Activity activity) {
//        try {
//            int requestCode = PackageManager.PERMISSION_GRANTED;
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
//
//            File sdCard = Environment.getExternalStorageDirectory();
//            File folder = new File(sdCard.getAbsolutePath(), "SmsCampaign");
//            // if external memory exists and folder with name Notes
//            if (!folder.exists()) {
//                //this will create folder.
//                folder.mkdirs();
//            }
//            // file path to save
//            File filepath = new File(folder.getAbsolutePath(), Converter.toString(new Date(), Converter.DATE) + ".csv");
//            filepath.createNewFile();
//
//            FileOutputStream fileOutputStream = new FileOutputStream(filepath.getAbsolutePath());
//
//            StringBuilder stringBuilder = new StringBuilder();
//
//            stringBuilder.append("DateTimeStarted, " + Converter.toString(Scope.smsContainer.dateTimeStarted, Converter.DATE_TIME) + "\n");
//            stringBuilder.append("DateTimeEnded, " + Converter.toString(Scope.smsContainer.dateTimeCompleted, Converter.DATE_TIME) + "\n");
//            stringBuilder.append("\n");
//            stringBuilder.append(
//                    "Id, " +
//                            "DateTimeCreated, " +
//                            "Number, " +
//                            "Message, " +
//                            "Center, " +
//                            "DateTimeSent, " +
//                            "Sent, " +
//                            "DateTimeDelivered" +
//                            "Delivered, " +
//                            "Error\n");
//
//            for (Sms sms : Scope.smsContainer.items) {
//                stringBuilder.append(sms.id + ",");
//                stringBuilder.append(sms.dateTimeCreated + ",");
//                stringBuilder.append(sms.number + ",");
//                stringBuilder.append(sms.message + ",");
//                stringBuilder.append(sms.center + ",");
//                stringBuilder.append(sms.dateTimeSent + ",");
//                stringBuilder.append(sms.sent + ",");
//                stringBuilder.append(sms.dateTimeDelivered + ",");
//                stringBuilder.append(sms.delivered + ",");
//                stringBuilder.append(sms.error + "\n");
//            }
//
//            fileOutputStream.write(stringBuilder.toString().getBytes());
//            fileOutputStream.flush();
//            fileOutputStream.close();
//
//            Toast.makeText(activity, "Save to File Successfully", Toast.LENGTH_LONG).show();
//        } catch (Exception exception) {
//            Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }

}
