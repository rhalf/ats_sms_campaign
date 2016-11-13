package com.ats_qatar.smscampaign.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Resource implements Serializable {


    public String imei;
    public Calendar dtExpire;

    public int smsCreditLimit;
    public int smsCreditConsumed;

    public String activationKey;

    public static void set(Activity activity, Resource resource) {
        set(activity.getApplicationContext(), resource);
    }

    public static void set(Context context, Resource resource) {
        SharedPreferences sp;
        SharedPreferences.Editor spEditor;
        String preferenceName = "resource";
        sp = context.getSharedPreferences(preferenceName, 0);
        spEditor = sp.edit();

        spEditor.putString("imei", resource.imei);
        spEditor.putString("dtExpire", Converter.toString(resource.dtExpire, Converter.DATE));
        spEditor.putInt("smsCreditLimit", resource.smsCreditLimit);
        spEditor.putInt("smsCreditConsumed", resource.smsCreditConsumed);

        spEditor.putString("activationKey", resource.activationKey);

        spEditor.commit();
    }


    public static Resource get(Activity activity) {
        return get(activity.getApplicationContext());
    }


    public static Resource get(Context context) {
        SharedPreferences sp;
        String preferenceName = "resource";
        sp = context.getSharedPreferences(preferenceName, 0);

        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        String id  = telephonyManager.getDeviceId();
        Resource resource = new Resource();
        resource.imei = id;
        resource.dtExpire = Converter.toCalendar(sp.getString("dtExpire", "16-11-25"), Converter.DATE);
        resource.smsCreditLimit = sp.getInt("smsCreditLimit", 50);
        resource.smsCreditConsumed = sp.getInt("smsCreditConsumed", 0);
        resource.activationKey = sp.getString("activationKey", null);

        return resource;
    }

    public boolean isNotExpired() {

        Calendar dateNow = Calendar.getInstance();
        Calendar dateExpire = this.dtExpire;

        if (dateNow.compareTo(dateExpire) >= 1){
            return true;
        }

        return false;
    }

    public boolean isCreditLimitReached() {
        if (this.smsCreditConsumed >= this.smsCreditLimit){
            return true;
        }
        return false;
    }
}


