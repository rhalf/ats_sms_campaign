package com.ats_qatar.smscampaign;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Sms implements Serializable {
    public int id;
    public String dateTimeCreated = "";
    public String number = "";
    public String message = "";
    public String center = "";

    public String sent = "";
    public String dateTimeSent = "";

    public String delivered = "";
    public String dateTimeDelivered = "";

    public String error = "";


}
