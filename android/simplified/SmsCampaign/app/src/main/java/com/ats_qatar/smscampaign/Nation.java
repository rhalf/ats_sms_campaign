package com.ats_qatar.smscampaign;

import android.app.Activity;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Nation implements Serializable{

    public String name;
    public String areaCode;
    public String code;

    public Nation(String name, String areaCode, String code) {
        this.name = name;
        this.areaCode = areaCode;
        this.code = code;
    }

    public static ArrayList<Nation> getNations() {
        ArrayList<Nation> nations = new ArrayList<Nation>();

        nations.add(new Nation("Qatar","+974", "QA"));
        nations.add(new Nation("Philippines","+63", "PH"));
        nations.add(new Nation("India","+91", "IN"));
        nations.add(new Nation("Pakistan","+92", "PK"));

        return nations;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
