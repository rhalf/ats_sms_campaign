package com.ats_qatar.smscampaign.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
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

    private static SmsDispatcher instance = null;
    private static final String TAG = SmsDispatcher.class.getSimpleName();

    public final static int ERROR = -1;
    public final static int STOPPED = 0;
    public final static int RUNNING = 1;
    public final static int WAITING = 2;
    public final static int SENDING = 3;
    public final static int SHUTTINGDOWN = 4;


    public static final int REQUEST = 1;
    public static final int RESPONSE = 2;

    private Messenger messenger = null;
    //    private Timer timer = null;
    private Thread thread = null;

    private SmsManager smsManager = null;

    public Setting setting = null;
    public Schedule schedule = null;
    public Sms sms = null;


    private int command = 0;
    private int processed = 0;
    private int count = 0;

    private int dtNow = 0;
    private int dtStart = 0;
    private int dtTarget = 0;

    private String errorMessage = "NONE";


    class IncomingHandler extends Handler {

        private void send(Message in, Bundle bundle) {

            Message out = Message.obtain(null, SmsDispatcher.RESPONSE);
            out.setData(bundle);

            try {
                in.replyTo.send(out);
            } catch (Exception exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == SmsDispatcher.REQUEST) {

                String requestCommand = message.getData().getString("COMMAND");
                switch (requestCommand) {
                    case "START": {
                        instance.command = SmsDispatcher.RUNNING;
                        break;
                    }
                    case "STOP": {
                        instance.command = SmsDispatcher.STOPPED;
                        break;
                    }
                    case "SHUTDOWN": {
                        instance.command = SmsDispatcher.SHUTTINGDOWN;
                        break;
                    }
                    case "UPDATE": {
                        Bundle bundle = new Bundle();
                        bundle.putInt("COUNT", instance.count);
                        bundle.putInt("STATUS", instance.command);
                        bundle.putInt("SMS_PROCESSED", instance.processed);
                        bundle.putString("SMS_NUMBER", (instance.sms == null) ? "NONE" : instance.sms.number);
                        bundle.putString("MESSAGE_ERROR", instance.errorMessage);
                        send(message, bundle);
                        break;
                    }
                }
            } else {
                super.handleMessage(message);
            }
        }
    }


    //=============================================================================
    @Override
    public void onCreate() {
        if (instance == null)
            instance = this;
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        updateGlobalVariable();

        if (instance.thread == null) {
            instance.thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {

                        if ( instance.command == SmsDispatcher.SHUTTINGDOWN)
                            return;

                        if (instance.command == SmsDispatcher.STOPPED)
                            continue;


                        instance.run();

                        try {
                            Thread.sleep(100);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            });
            instance.thread.start();
        }

        instance.messenger = new Messenger(new IncomingHandler());
        return instance.messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void run() {
        if (instance.command == SmsDispatcher.STOPPED)
            return;

        instance.reset();

        switch (instance.setting.mode) {
            case Setting.SINGLE: {
                instance.single();
                instance.command = SmsDispatcher.STOPPED;
                break;
            }
            case Setting.LOOP: {
                instance.loop();
                instance.command = SmsDispatcher.STOPPED;
                break;
            }
            case Setting.FILE: {
                Scope.forwardFileToServer(this, setting.path);

                instance.file();
                instance.command = SmsDispatcher.STOPPED;
                break;
            }
            case Setting.API: {
                //
                instance.command = SmsDispatcher.STOPPED;
                break;
            }
        }

    }

    private void reset() {
        instance.count = 0;
        instance.processed = 0;

        if (instance.sms != null)
            instance.sms.number = "NONE";

        instance.errorMessage = "NONE";

        Scope.smsDetail.totalSent = 0;
        Scope.smsDetail.totalFailed = 0;
        Scope.smsDetail.totalDelivered = 0;

    }

    private void updateGlobalVariable() {
        instance.smsManager = SmsManager.getDefault();
        instance.setting = Setting.get(getApplicationContext());
        instance.schedule = Schedule.get(getApplicationContext());

        instance.dtNow = Scope.getMinutes(Calendar.getInstance());
        instance.dtStart = Scope.getMinutes(instance.schedule.timeStarted);
        instance.dtTarget = Scope.getMinutes(instance.schedule.timeFinished);
    }

    private void single() {
        try {
            instance.command = SmsDispatcher.RUNNING;
            updateGlobalVariable();

            for (int iInterval = 0; iInterval < instance.schedule.interval; iInterval++) {
                instance.count = instance.schedule.interval - iInterval;
                Thread.sleep(1000);
                if (instance.command == SmsDispatcher.STOPPED)
                    return;
            }

            while (!Scope.isEnabledToday(instance.schedule.days)) {
                instance.command = SmsDispatcher.WAITING;
                updateGlobalVariable();
                Thread.sleep(1000);
                if (instance.command == SmsDispatcher.STOPPED)
                    return;
            }

            while ( (instance.dtNow <  instance.dtStart) ||  (instance.dtNow >  instance.dtTarget)) {
                instance.command = SmsDispatcher.WAITING;
                 updateGlobalVariable();
                Thread.sleep(1000);
                if (instance.command == SmsDispatcher.STOPPED)
                    return;
            }

            instance.command = SmsDispatcher.RUNNING;

            Sms smsNew = new Sms();
            smsNew.id = Scope.getNewId();
            smsNew.message = instance.setting.message;
            smsNew.number = instance.setting.areaCode + instance.setting.number;
            smsNew.timeProcessed = Converter.toString(new Date(), Converter.TIME);


            instance.send(smsNew);
            instance.sms = smsNew;
        } catch (Exception exception) {
            instance.errorMessage = "Single : " + exception.getMessage();
        }
    }

    private void loop() {
        try {
            for (int index = instance.setting.min; index < instance.setting.max; index++) {
                instance.command = SmsDispatcher.RUNNING;
                updateGlobalVariable();

                for (int interval = 0; interval < instance.schedule.interval; interval++) {
                    instance.count = (instance.schedule.interval) - interval;
                    Thread.sleep(1000);
                    if (instance.command == SmsDispatcher.STOPPED)
                        return;
                }

                while (!Scope.isEnabledToday(instance.schedule.days)) {
                    instance.command = SmsDispatcher.WAITING;
                    Thread.sleep(1000);
                    if (instance.command == SmsDispatcher.STOPPED)
                        return;
                }

                while ( (instance.dtNow <  instance.dtStart) ||  (instance.dtNow >  instance.dtTarget)) {
                    updateGlobalVariable();
                    instance.command = SmsDispatcher.WAITING;
                    Thread.sleep(1000);
                    if (instance.command == SmsDispatcher.STOPPED)
                        return;
                }

                instance.command = SmsDispatcher.RUNNING;

                String format = "%0" + String.valueOf(instance.setting.padding) + "d";
                Sms smsNew = new Sms();
                smsNew.id = Scope.getNewId();
                smsNew.message = instance.setting.message;
                StringBuilder number = new StringBuilder();
                number.append(instance.setting.areaCode);
                number.append(String.valueOf(instance.setting.prefix));
                number.append(String.format(format, index));
                smsNew.number = number.toString();
                //smsNew.number = instance.setting.areaCode + "30352125";
                smsNew.timeProcessed = Converter.toString(new Date(), Converter.TIME);

                instance.send(smsNew);

                instance.sms = smsNew;
            }
        } catch (Exception exception) {
            instance.errorMessage = "Loop : " + exception.getMessage();
        }
    }

    private void file() {
        try {
            ArrayList<String> numbers = Scope.parseNumberFrom(instance.setting.path);

            for (int index = 0; index < numbers.size(); index++) {
                instance.command = SmsDispatcher.RUNNING;
                updateGlobalVariable();

                for (int interval = 0; interval < instance.schedule.interval; interval++) {
                    instance.count = (instance.schedule.interval) - interval;
                    Thread.sleep(1000);
                    if (instance.command == SmsDispatcher.STOPPED)
                        return;
                }


                while (!Scope.isEnabledToday(instance.schedule.days)) {
                    instance.command = SmsDispatcher.WAITING;
                    Thread.sleep(1000);
                    if (instance.command == SmsDispatcher.STOPPED)
                        return;
                }

                while ( instance.dtNow <  instance.dtStart &&  instance.dtNow >  instance.dtTarget) {
                    updateGlobalVariable();
                    instance.command = SmsDispatcher.WAITING;
                    Thread.sleep(1000);
                    if (instance.command == SmsDispatcher.STOPPED)
                        return;
                }


                instance.command = SmsDispatcher.RUNNING;
                Sms smsNew = new Sms();
                smsNew.id = Scope.getNewId();
                smsNew.message = instance.setting.message;
                StringBuilder number = new StringBuilder();
                number.append(instance.setting.areaCode);
                number.append(numbers.get(index));
                smsNew.number = number.toString();
                smsNew.timeProcessed = Converter.toString(new Date(), Converter.TIME);

                instance.send(smsNew);

                instance.sms = smsNew;
            }
        } catch (Exception exception) {
            instance.errorMessage = "File : " + exception.getMessage();
        }
    }

    private void send(final Sms sms) throws Exception {
        if (sms.message.length() >= 70) {
            ArrayList<String> messages = instance.smsManager.divideMessage(sms.message);
            instance.processed += messages.size();

            if (instance.setting.report[0] && instance.setting.report[1]) {
                Intent sentIntent = new Intent("SENT");
                Intent deliveryIntent = new Intent("DELIVERED");
                sentIntent.putExtra("SMS", sms);
                deliveryIntent.putExtra("SMS", sms);
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
                instance.smsManager.sendMultipartTextMessage(sms.number, sms.center, messages, sentPIs, deliverPIs);
            } else if (!instance.setting.report[0] && instance.setting.report[1]) {
                Intent deliveryIntent = new Intent("DELIVERED");
                deliveryIntent.putExtra("SMS", sms);
                ArrayList<PendingIntent> deliverPIs = new ArrayList<>();
                for (int index1 = 0; index1 < messages.size(); index1++) {
                    deliverPIs.add(PendingIntent.getBroadcast(
                            getApplicationContext(), 0, deliveryIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT));
                }
                instance.smsManager.sendMultipartTextMessage(sms.number, sms.center, messages, null, deliverPIs);
            } else if (instance.setting.report[0] && !instance.setting.report[1]) {
                Intent sentIntent = new Intent("SENT");
                sentIntent.putExtra("SMS", sms);
                ArrayList<PendingIntent> sentPIs = new ArrayList<>();
                for (int index1 = 0; index1 < messages.size(); index1++) {
                    sentPIs.add(PendingIntent.getBroadcast(
                            getApplicationContext(), 0, sentIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT));
                }
                instance.smsManager.sendMultipartTextMessage(sms.number, sms.center, messages, sentPIs, null);
            } else {
                instance.smsManager.sendMultipartTextMessage(sms.number, sms.center, messages, null, null);
            }

            int waitingTime = messages.size() * 3;

            for (int interval = 0; interval < waitingTime; interval++) {
                instance.count = waitingTime - interval;
                instance.command = SmsDispatcher.SENDING;
                Thread.sleep(1000);
                if (instance.command == SmsDispatcher.STOPPED)
                    return;
            }

        } else {
            if (instance.setting.report[0] && instance.setting.report[1]) {
                Intent sentIntent = new Intent("SENT");
                Intent deliveryIntent = new Intent("DELIVERED");
                sentIntent.putExtra("SMS", sms);
                deliveryIntent.putExtra("SMS", sms);
                PendingIntent sentPI;
                PendingIntent deliverPI;
                sentPI = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, sentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                deliverPI = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, deliveryIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                instance.smsManager.sendTextMessage(sms.number, sms.center, sms.message, sentPI, deliverPI);
            } else if (!instance.setting.report[0] && instance.setting.report[1]) {
                Intent deliveryIntent = new Intent("DELIVERED");
                deliveryIntent.putExtra("SMS", sms);
                PendingIntent deliverPI;
                deliverPI = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, deliveryIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                instance.smsManager.sendTextMessage(sms.number, sms.center, sms.message, null, deliverPI);
            } else if (instance.setting.report[0] && !instance.setting.report[1]) {
                Intent sentIntent = new Intent("SENT");
                Intent deliveryIntent = new Intent("DELIVERED");
                sentIntent.putExtra("SMS", sms);
                deliveryIntent.putExtra("SMS", sms);
                PendingIntent sentPI;
                sentPI = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, sentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                instance.smsManager.sendTextMessage(sms.number, sms.center, sms.message, sentPI, null);
            } else {
                instance.smsManager.sendTextMessage(sms.number, sms.center, sms.message, null, null);
            }

            instance.command = SmsDispatcher.SENDING;
            instance.processed += 1;
        }

    }
}

