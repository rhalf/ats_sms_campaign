package com.ats_qatar.smscampaign.services;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.ats_qatar.smscampaign.models.Converter;
import com.ats_qatar.smscampaign.models.Schedule;
import com.ats_qatar.smscampaign.models.Scope;
import com.ats_qatar.smscampaign.models.Setting;
import com.ats_qatar.smscampaign.models.Sms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rhalf on 10/6/2016.
 */

public class SmsDispatcher extends Service {

    private static SmsDispatcher instance;
    private final Messenger messenger = new Messenger(new IncomingHandler());

    private static final String TAG = SmsDispatcher.class.getSimpleName();

    public static final int REQUEST = 1;
    public static final int RESPONSE = 2;

    SmsManager smsManager;

    public Setting setting;
    public Schedule schedule;
    Thread threadWorker;

    public final static int ERROR = -1;
    public final static int STOPPED = 0;
    public final static int RUNNING = 1;
    public final static int IDLING = 2;

    int status = SmsDispatcher.STOPPED;
    int sent = 0;
    int interval = 0;
    String message = "";

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case SmsDispatcher.REQUEST:
                    String request = message.getData().getString("COMMAND");

                    switch (request) {
                        case "START": {
                            Message response = Message.obtain(null, SmsDispatcher.RESPONSE);
                            Bundle bundle = new Bundle();
                            bundle.putString("STATUS", "STARTED");
                            response.setData(bundle);
                            try {
                                message.replyTo.send(response);
                                //Toast.makeText(getApplicationContext(),request,Toast.LENGTH_SHORT).show();
                            } catch (Exception exception) {
                                Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }


                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }


    //=============================================================================
    @Override
    public void onCreate() {
        instance = this;
        String message = "onCreate";
        Log.d(TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

        setting = Setting.get(getApplicationContext());

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String message = "onBind";
        Log.d(TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        return messenger.getBinder();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String message = "onStartCommand";
        Log.d(TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        String message = "onUnbind";
        Log.d(TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        status = SmsDispatcher.STOPPED;
        String message = "onDestroy";
        Log.d(TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }


    private void run() {
        status = SmsDispatcher.RUNNING;


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                switch (setting.mode) {
                    case Setting.SINGLE: {
                        single();
                        break;
                    }
                    case Setting.LOOPING: {
                        looping();
                        break;
                    }
                    case Setting.IMPORT_FILE: {
                        //
                        break;
                    }
                    case Setting.WEB_SERVICE: {
                        //
                        break;
                    }
                }
            }
        };
        threadWorker = new Thread(runnable);
        threadWorker.start();
    }


    private void single() {
        try {
            Sms sms = new Sms();
            sms.id = Scope.getNewId();
            sms.message = setting.message;
            sms.number = setting.areaCode + setting.number;
            send(sms);
            report(sms);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void looping() {
        for (int index = setting.min; index < setting.max; index++) {
            try {
                for (int count = 0; count < schedule.interval; count++) {

                    report();

                    interval = schedule.interval - count;
                    Thread.sleep(1000);

                    if (status == SmsDispatcher.STOPPED)
                        return;
                }

                Calendar calendar = Calendar.getInstance();
                int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
                int nowMinute = calendar.get(Calendar.MINUTE);
                int startHour = schedule.timeStarted.get(Calendar.HOUR_OF_DAY);
                int startMinute = schedule.timeStarted.get(Calendar.MINUTE);
                int finishHour = schedule.timeFinished.get(Calendar.HOUR_OF_DAY);
                int finishMinute = schedule.timeFinished.get(Calendar.MINUTE);

                int iNow = (nowHour * 60) + nowMinute;
                int iStart = (startHour * 60) + startMinute;
                int iFinish = (finishHour * 60) + finishMinute;


                if (iNow > iStart) {
                    if (iNow < iFinish) {
                        String format = "%0" + String.valueOf(setting.padding) + "d";

                        Sms sms = new Sms();
                        sms.id = Scope.getNewId();
                        sms.message = setting.message;
                        sms.number = setting.areaCode + "30352125";//String.valueOf(setting.prefix) + String.format(format, index);
                        sent = index - setting.min;
                        send(sms);
                        report(sms);
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void send(Sms sms) {
        Intent sentIntent = new Intent("SENT");
        Intent deliveryIntent = new Intent("DELIVERED");

        sentIntent.putExtra("SMS", sms);
        deliveryIntent.putExtra("SMS", sms);

        if (sms.message.length() >= 70) {
            ArrayList<String> messages = smsManager.divideMessage(sms.message);
            ArrayList<PendingIntent> sentPIs = new ArrayList<>();
            ArrayList<PendingIntent> deliverPIs = new ArrayList<>();

            for (int index1 = 0; index1 < messages.size(); index1++) {
                sentPIs.add(PendingIntent.getBroadcast(
                        getApplicationContext(), 0, sentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
                deliverPIs.add(PendingIntent.getBroadcast(
                        getApplicationContext(), 0, deliveryIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
            }

            smsManager.sendMultipartTextMessage(sms.number, sms.center, messages, sentPIs, deliverPIs);

        } else {
            PendingIntent sentPI;
            PendingIntent deliverPI;


            sentPI = PendingIntent.getBroadcast(
                    getApplicationContext(), 0, sentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            deliverPI = PendingIntent.getBroadcast(
                    getApplicationContext(), 0, deliveryIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            smsManager.sendTextMessage(sms.number, sms.center, sms.message, sentPI, deliverPI);
        }
    }

    private void report(Sms sms) {
        Intent intentService = new Intent("SMS_DISPATCHER");

        intentService.putExtra("number", sms.number);

        getApplicationContext().sendBroadcast(intentService);
    }

    private void report() {
        Intent intentService = new Intent("SMS_DISPATCHER");
        intentService.putExtra("count", interval);
        intentService.putExtra("status", status);
        intentService.putExtra("sent", sent);
        intentService.putExtra("message", message);

        getApplicationContext().sendBroadcast(intentService);
    }

}
