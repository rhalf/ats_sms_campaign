package com.ats_qatar.smscampaign.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Setting implements Serializable {

    public static final int SINGLE = 1;
    public static final int LOOPING = 2;
    public static final int IMPORT_FILE = 3;
    public static final int WEB_SERVICE = 4;

    public int mode;
    public String areaCode;
    public String message;
    //SINGLE
    public String number;
    //LOOPING
    public int prefix;
    public int padding;
    public int min;
    public int max;
    //IMPORT_FILE
    public String path;
    //WEB_SERVICE
    public String url;

    public static void set(Activity activity, Setting setting) {
        set(activity.getApplicationContext(), setting);
    }

    public static void set(Context context, Setting setting) {
        SharedPreferences sp;
        SharedPreferences.Editor spEditor;
        String preferenceName = "setting";
        sp = context.getSharedPreferences(preferenceName, 0);
        spEditor = sp.edit();

        spEditor.putInt("mode",setting.mode);
        spEditor.putString("areaCode",setting.areaCode);
        spEditor.putString("message",setting.message);
        //SINGLE
        spEditor.putString("number",setting.number);
        //LOOPING
        spEditor.putInt("prefix",setting.prefix);
        spEditor.putInt("padding",setting.padding);
        spEditor.putInt("min",setting.min);
        spEditor.putInt("max",setting.max);
        //IMPORT_FILE
        spEditor.putString("path",setting.path);
        //WEB_SERVICE
        spEditor.putString("url",setting.url);
        spEditor.commit();
    }

    public static Setting get(Activity activity) {
        return get(activity.getApplicationContext());
    }

    public static Setting get(Context context) {
        SharedPreferences sp;
        String preferenceName = "setting";
        sp = context.getSharedPreferences(preferenceName, 0);
        Setting setting = new Setting();

        setting.mode = sp.getInt("mode", 1);
        setting.areaCode = sp.getString("areaCode", "+974");
        setting.message = sp.getString("message", "Advanced Technologies and Solutions" + "\n" + "goo.gl/gZW4n7");
        //SINGLE
        setting.number = sp.getString("number","30352125");
        //LOOPING
        setting.prefix = sp.getInt("prefix", 55);
        setting.padding = sp.getInt("padding", 6);
        setting.min = sp.getInt("min", 0);
        setting.max = sp.getInt("max", 1);
        //IMPORT_FILE
        setting.path = sp.getString("path","/");
        //WEB_SERVICE
        setting.url = sp.getString("url","/");

        return setting;
    }


}