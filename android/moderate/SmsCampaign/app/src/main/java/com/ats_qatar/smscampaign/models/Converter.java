package com.ats_qatar.smscampaign.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Converter implements Serializable {

    public static final int TIME = 1;
    public static final int DATE = 2;
    public static final int DATE_TIME = 3;


    private static SimpleDateFormat getFormat(int mode) {
        SimpleDateFormat simpleDateFormat;

        switch (mode) {
            case Converter.TIME:
                return new SimpleDateFormat("HH:mm:ss");
            case Converter.DATE:
                return new SimpleDateFormat("yy-MM-dd");
            case Converter.DATE_TIME:
                return new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            default:
                return new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        }
    }

    public static String toString(Calendar calendar, int mode) {
        try {
            SimpleDateFormat simpleDateFormat = Converter.getFormat(mode);
            return simpleDateFormat.format(calendar.getTime());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static String toString(Date date, int mode) {
        try {
            SimpleDateFormat simpleDateFormat = Converter.getFormat(mode);
            return simpleDateFormat.format(date);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static Date toDate(String date, int mode) {
        try {
            SimpleDateFormat simpleDateFormat = Converter.getFormat(mode);
            return simpleDateFormat.parse(date);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static Calendar toCalendar(String date, int mode) {
        try {
            SimpleDateFormat simpleDateFormat = Converter.getFormat(mode);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(simpleDateFormat.parse(date));
            return calendar1;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }


    public static String  concat(String delimiter, String[] strings) {
        StringBuilder stringBuilder = new StringBuilder();

        for(int index = 0; index < strings.length; index++){
            stringBuilder.append(strings[index]);
            stringBuilder.append(delimiter);
        }

        return stringBuilder.substring(0,stringBuilder.length() - 2);
    }
}
