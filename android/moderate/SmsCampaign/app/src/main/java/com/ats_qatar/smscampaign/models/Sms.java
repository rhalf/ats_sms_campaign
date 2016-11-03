package com.ats_qatar.smscampaign.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Sms implements Serializable {
    public long id;
    public String timeProcessed = "";
    public String number = "";
    public String message = "";
    public String center = "";

    public String sent = "";
    public String timeSent = "";

    public String delivered = "";
    public String timeDelivered = "";

    public String error = "";


}
