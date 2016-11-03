package com.ats_qatar.smscampaign.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Schedule implements Serializable {

    public boolean[] days = new boolean[7];
    public Calendar timeStarted;
    public Calendar timeFinished;
    public int interval;


    public static void set(Activity activity, Schedule schedule) {
        set(activity.getApplicationContext(), schedule);
    }

    public static void set(Context context, Schedule schedule) {
        SharedPreferences sp;
        SharedPreferences.Editor spEditor;
        String preferenceName = "schedule";
        sp = context.getSharedPreferences(preferenceName, 0);
        spEditor = sp.edit();

        spEditor.putBoolean("day0", schedule.days[0]);
        spEditor.putBoolean("day1", schedule.days[1]);
        spEditor.putBoolean("day2", schedule.days[2]);
        spEditor.putBoolean("day3", schedule.days[3]);
        spEditor.putBoolean("day4", schedule.days[4]);
        spEditor.putBoolean("day5", schedule.days[5]);
        spEditor.putBoolean("day6", schedule.days[6]);
        spEditor.putString("timeStarted", Converter.toString(schedule.timeStarted, Converter.TIME));
        spEditor.putString("timeFinished", Converter.toString(schedule.timeFinished, Converter.TIME));
        spEditor.putInt("interval", schedule.interval);
        spEditor.commit();
    }

    public static Schedule get(Activity activity) {
        return get(activity.getApplicationContext());
    }


    public static Schedule get(Context context) {
        SharedPreferences sp;
        String preferenceName = "schedule";
        sp = context.getSharedPreferences(preferenceName, 0);

        Schedule schedule = new Schedule();
        schedule.days[0] = sp.getBoolean("day0", false);
        schedule.days[1] = sp.getBoolean("day1", false);
        schedule.days[2] = sp.getBoolean("day2", false);
        schedule.days[3] = sp.getBoolean("day3", false);
        schedule.days[4] = sp.getBoolean("day4", false);
        schedule.days[5] = sp.getBoolean("day5", false);
        schedule.days[6] = sp.getBoolean("day6", false);

        schedule.timeStarted = Converter.toCalendar(sp.getString("timeStarted", "00:00:00"), Converter.TIME);
        schedule.timeFinished = Converter.toCalendar(sp.getString("timeFinished", "00:00:00"), Converter.TIME);


        schedule.interval = sp.getInt("interval", 3);

        return schedule;
    }


}