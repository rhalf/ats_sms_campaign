package com.ats_qatar.smscampaign.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ats_qatar.smscampaign.models.Converter;
import com.ats_qatar.smscampaign.models.Scope;
import com.ats_qatar.smscampaign.models.Setting;
import com.ats_qatar.smscampaign.models.Sms;

import java.util.Date;

/**
 * Created by Rhalf on 10/1/2016.
 */

public class SmsDeliveredReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Setting setting = Setting.get(context.getApplicationContext());

        if (setting.report[1]) {
            Sms sms = (Sms) intent.getExtras().getSerializable("SMS");
            sms.delivered = "OK";
            sms.timeDelivered = Converter.toString(new Date(), Converter.TIME);
            Scope.writeDelivered(sms);
        }


        if (Scope.smsDetail != null) {
            Scope.smsDetail.totalDelivered++;
        }
    }
}
