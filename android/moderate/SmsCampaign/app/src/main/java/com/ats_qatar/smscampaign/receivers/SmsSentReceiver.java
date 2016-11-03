package com.ats_qatar.smscampaign.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.ats_qatar.smscampaign.models.Converter;
import com.ats_qatar.smscampaign.models.Scope;
import com.ats_qatar.smscampaign.models.Setting;
import com.ats_qatar.smscampaign.models.Sms;

import java.util.Date;

/**
 * Created by Rhalf on 10/1/2016.
 */

public class SmsSentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String result = "";

        int success = 0;
        int failed = 0;

        switch (getResultCode()) {
            case Activity.RESULT_OK:
                result = "Transmission successful";
                success = 1;
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                result = "Transmission failed";
                failed = 1;
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                result = "Radio off";
                failed = 1;
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                result = "No PDU defined";
                failed = 1;
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                result = "No service";
                failed = 1;
                break;
        }

        Setting setting = Setting.get(context.getApplicationContext());
        if (setting.report[0]) {
            Sms sms = (Sms) intent.getExtras().getSerializable("SMS");
            sms.sent = result;
            sms.timeSent = Converter.toString(new Date(), Converter.TIME);
            Scope.writeSent(sms);
        }

        if (Scope.smsDetail != null) {
            Scope.smsDetail.totalSent += success;
            Scope.smsDetail.totalSent += failed;
        }
    }
}
