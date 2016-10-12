package com.ats_qatar.smscampaign;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * Created by Rhalf on 10/1/2016.
 */

public class SmsSentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String result = "";
        switch (getResultCode()) {

            case Activity.RESULT_OK:
                result = "Transmission successful";
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                result = "Transmission failed";
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                result = "Radio off";
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                result = "No PDU defined";
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                result = "No service";
                break;
        }

        for (Sms sms: GlobalScope.smsContainer.items) {
            if (sms.id == (int)intent.getExtras().getInt("ID")){
                sms.sent = result;
                sms.dateTimeSent = GlobalScope.dateTime();
            }
        }
    }
}
