package com.ats_qatar.smscampaign;

import android.app.Activity;
import android.content.SharedPreferences;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Resource implements Serializable{

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    String preferenceName = "resource";

     public Resource(Activity activity) {
        sp = activity.getSharedPreferences(preferenceName,0);
        spEditor = sp.edit();
    }


}
