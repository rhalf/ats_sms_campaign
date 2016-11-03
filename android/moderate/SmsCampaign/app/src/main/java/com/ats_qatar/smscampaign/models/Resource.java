package com.ats_qatar.smscampaign.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Resource implements Serializable{


    public String userId;
    public Calendar dtExpire;


    public static void set(Activity activity, Resource resource) {
        set(activity.getApplicationContext(), resource);
    }

    public static void set(Context context, Resource resource) {
        SharedPreferences sp;
        SharedPreferences.Editor spEditor;
        String preferenceName = "resource";
        sp = context.getSharedPreferences(preferenceName, 0);
        spEditor = sp.edit();

        spEditor.putString("userId", resource.userId);
        spEditor.putString("dtExpire", Converter.toString(resource.dtExpire,Converter.DATE));

        spEditor.commit();
    }


    public static Resource get(Activity activity) {
        return get(activity.getApplicationContext());
    }


    public static Resource get(Context context) {
        SharedPreferences sp;
        String preferenceName = "resource";
        sp = context.getSharedPreferences(preferenceName, 0);

        Resource resource = new Resource();
        resource.userId = sp.getString("userId", "5hCQazvy");
        resource.dtExpire = Converter.toCalendar(sp.getString("dtExpire", "16-11-25"), Converter.DATE);



        return resource;
    }
}


