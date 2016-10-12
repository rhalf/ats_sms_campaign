package com.ats_qatar.smscampaign;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class SmsContainer implements Serializable{
    public int id;
    public Date dateTimeStarted;
    public Date dateTimeCompleted;
    public ArrayList<Sms> items;

    public SmsContainer() {
        this.items = new ArrayList<Sms>();
    }

}
