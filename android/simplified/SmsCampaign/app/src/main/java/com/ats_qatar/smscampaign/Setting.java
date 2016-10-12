package com.ats_qatar.smscampaign;

import android.app.Activity;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Setting implements Serializable{

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    String preferenceName = "setting";

    public Setting(Activity activity) {
        sp = activity.getSharedPreferences(preferenceName,0);
        spEditor = sp.edit();
    }

    public boolean getLoop (){
        return sp.getBoolean("loop",false);
    }
    public void setLoop (boolean value){
        spEditor.putBoolean("loop", value);
        spEditor.commit();
    }

    public String getAreaCode(){
        return sp.getString("areaCode","+974");
    }
    public void setAreaCode(String value){
        spEditor.putString("areaCode", value);
        spEditor.commit();
    }

    public String getPrefix(){
        return sp.getString("prefix", "30352125");
    }
    public void setPrefix(String value){
        spEditor.putString("prefix", value);
        spEditor.commit();
    }

    public int getMinimum(){
        return sp.getInt("min", 0);
    }
    public void setMinimum(int value){
        spEditor.putInt("min", value);
        spEditor.commit();
    }

    public int getMaximum(){
        return sp.getInt("max", 0);
    }
    public void setMaximum(int value){
        spEditor.putInt("max", value);
        spEditor.commit();
    }

    public int getInterval(){
        return sp.getInt("interval", 5);
    }
    public void setInterval(int value){
        spEditor.putInt("interval", value);
        spEditor.commit();
    }

    public String getMessage(){
        return sp.getString("message", "Test\n");
    }
    public void setMessage(String value){
        spEditor.putString("message", value);
        spEditor.commit();
    }

    public int getWorkingTime(){
        return sp.getInt("workingTime", 1);
    }
    public void setWorkingTime(int value){
        spEditor.putInt("workingTime", value);
        spEditor.commit();
    }

    public int getTerminatingTime(){
        return sp.getInt("terminatingTime", 5);
    }
    public void setTerminatingTime(int value){
        spEditor.putInt("terminatingTime", value);
        spEditor.commit();
    }

}
