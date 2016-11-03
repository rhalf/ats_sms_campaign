package com.ats_qatar.smscampaign;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rhalf on 10/3/2016.
 */


public class



SmsCampaignService {

    SmsManager smsManager = SmsManager.getDefault();

    private Setting setting;
    private Activity activity;
    private SmsContainer smsContainer;
    private OnEventListener onEventListener;

    private Thread thread;

    private boolean status;

    public boolean getStatus() {
        return this.status;
    }

    ;


    public void setOnEventListener(OnEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public void start() {
        status = true;
        run();
    }


    public SmsCampaignService(Setting setting, SmsContainer smsContainer, Activity activity) {
        this.activity = activity;
        this.setting = new Setting(activity);
        this.smsContainer = smsContainer;
    }

    public void stop() {
        status = false;
    }


    public void run() {
        onEventListener.onMessage("Sending SMS Started");
        onEventListener.onStart(status);
        smsContainer.items.clear();
        smsContainer.dateTimeStarted = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, setting.getWorkingTime());
        final Date dateTarget = calendar.getTime();
        status = true;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int index = 0; index < setting.getMaximum(); index++) {

                    Sms sms = new Sms();
                    try {
                        for (int count = 0; count < setting.getInterval(); count++) {
                            Thread.sleep(1000);
                            onEventListener.onInterval(setting.getInterval() - count);
                            if (!status) {
                                onEventListener.onMessage("Sending SMS Interrupted by Click");
                                smsContainer.dateTimeCompleted = new Date();
                                onEventListener.onFinish(status);
                                return;
                            }

                            if (dateTarget.compareTo(new Date()) == -1) {
                                status = false;
                                onEventListener.onMessage("Sending SMS Interrupted by Set DateTime");
                                smsContainer.dateTimeCompleted = new Date();
                                onEventListener.onFinish(status);
                                return;
                            }
                        }


                        sms.id = index;
                        sms.dateTimeCreated = GlobalScope.dateTime();
                        sms.message = setting.getMessage();
                        if (setting.getLoop()) {
                            sms.number = setting.getAreaCode() +
                                    setting.getPrefix() +
                                    String.format("%06d", setting.getMinimum() + index);
                        } else {
                            sms.number = setting.getAreaCode() +
                                    setting.getPrefix();
                        }


                        Intent sentIntent = new Intent("SENT");
                        Intent deliveryIntent = new Intent("DELIVERED");


                        sentIntent.putExtra("ID", sms.id);
                        deliveryIntent.putExtra("ID", sms.id);


                        if (sms.message.length() >= 70) {

                            ArrayList<String> messages = smsManager.divideMessage(sms.message);

                            ArrayList<PendingIntent> sentPIs = new ArrayList<>();
                            ArrayList<PendingIntent> deliverPIs = new ArrayList<>();

                            for (int index1 = 0; index1 < messages.size(); index1++) {
                                sentPIs.add(PendingIntent.getBroadcast(
                                        activity.getApplicationContext(), 0, sentIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT));
                                deliverPIs.add(PendingIntent.getBroadcast(
                                        activity.getApplicationContext(), 0, deliveryIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT));
                            }

//                            smsManager.sendMultipartTextMessage(sms.number, sms.center, messages, sentPIs,  deliverPIs);
                            smsManager.sendMultipartTextMessage(sms.number, sms.center, messages, null,  null );

                        } else {
                            PendingIntent sentPI;
                            PendingIntent deliverPI;

                                    /*Create Pending Intents*/
                            sentPI = PendingIntent.getBroadcast(
                                    activity.getApplicationContext(), 0, sentIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            deliverPI = PendingIntent.getBroadcast(
                                    activity.getApplicationContext(), 0, deliveryIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                                    /*Register for SMS send action*/
//                        smsManager.sendTextMessage(sms.number, sms.center, sms.message, sentPI, deliverPI);
                            smsManager.sendTextMessage(sms.number, null, sms.message, null, null);

                       }

                    } catch (Exception exception) {
                        sms.error = exception.getMessage();
                    }

                    smsContainer.items.add(sms);
                    onEventListener.onSmsSent(sms, smsContainer.items.size());
                }
                onEventListener.onMessage("Sending SMS Finished");
                smsContainer.dateTimeCompleted = new Date();
                status = false;
                onEventListener.onFinish(status);
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }
}
