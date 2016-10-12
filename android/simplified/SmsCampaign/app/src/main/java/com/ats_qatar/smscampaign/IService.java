package com.ats_qatar.smscampaign;

/**
 * Created by Rhalf on 10/3/2016.
 */

interface OnEventListener {

    void onMessage(String message);

    void onInterval(int interval);
    

    void onSmsSent(Sms sms, int sent);

    void onStart(boolean status);

    void onFinish(boolean status);

}