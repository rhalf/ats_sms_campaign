package com.ats_qatar.smscampaign;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * Created by Rhalf on 10/1/2016.
 */

public class SmsDeliveredReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        for (Sms sms:GlobalScope.smsContainer.items) {
            if (sms.id == (int) intent.getExtras().getInt("ID")){
                sms.delivered = "OK";
                sms.dateTimeDelivered = GlobalScope.dateTime();
            }
        }
    }
}
