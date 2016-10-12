package com.ats_qatar.smscampaign;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rhalf on 9/28/2016.
 */

public class FragmentDashboard extends Fragment implements View.OnClickListener {

    Setting setting;

    View view;
    Thread thread;

    ToggleButton toggleButtonStart;
    ScrollView scrollViewDebug;

    TextView
            textViewDtStarted, textViewDtTarget, textViewInterval, textViewElapsed, textViewStatus, textViewSent, textViewNumber, textViewMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_dashboard, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setting = new Setting(getActivity());

        toggleButtonStart = (ToggleButton) getView().findViewById(R.id.toggleButtonStart);
        textViewDtStarted = (TextView) getView().findViewById(R.id.textViewDtStarted);
        textViewDtTarget = (TextView) getView().findViewById(R.id.textViewDtTarget);
        textViewInterval = (TextView) getView().findViewById(R.id.textViewInterval);
        textViewNumber = (TextView) getView().findViewById(R.id.textViewNumber);
        textViewSent = (TextView) getView().findViewById(R.id.textViewSent);
        textViewStatus = (TextView) getView().findViewById(R.id.textViewStatus);
        textViewMessage = (TextView) getView().findViewById(R.id.textViewMessage);
        textViewElapsed = (TextView) getView().findViewById(R.id.textViewElapsed);

        scrollViewDebug = (ScrollView) getView().findViewById(R.id.scrollViewDebug);

        toggleButtonStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toggleButtonStart: {
                if (toggleButtonStart.isChecked()) {

                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                    textViewStatus.setText(String.valueOf(setting.getInterval()));

                    textViewDtStarted.setText(simpleDateFormat.format(new Date()));
                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.MINUTE, setting.getWorkingTime());
                    textViewDtTarget.setText(simpleDateFormat.format(calendar.getTime()));
                    textViewInterval.setText(String.valueOf(setting.getInterval()));


                    GlobalScope.smsCampaignService.setOnEventListener(new OnEventListener() {
                        @Override
                        public void onMessage(final String message) {
                            textViewMessage.post(new Runnable() {
                                @Override
                                public void run() {
                                    textViewMessage.setText(message);
                                }
                            });
                        }

                        @Override
                        public void onInterval(final int interval) {
                            textViewElapsed.post(new Runnable() {
                                @Override
                                public void run() {
                                    textViewElapsed.setText(String.valueOf(interval));
                                }
                            });
                        }

                        @Override
                        public void onSmsSent(final Sms sms, final int sent) {
                            textViewNumber.post(new Runnable() {
                                @Override
                                public void run() {
                                    textViewNumber.setText(sms.number);
                                    Calendar calendar1 = Calendar.getInstance();
                                    calendar1.add(Calendar.MINUTE, (-1 * setting.getWorkingTime()));
                                    textViewSent.setText(String.valueOf(sent));
                                }
                            });
                        }

                        @Override
                        public void onStart(final boolean status) {
                            toggleButtonStart.post(new Runnable() {
                                @Override
                                public void run() {
                                    textViewStatus.setText("Running");
                                }
                            });
                        }

                        @Override
                        public void onFinish(final boolean status) {

                            final Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    for (int count = 0; count < (setting.getTerminatingTime()); count++) {
                                        try {
                                            Thread.sleep(1000);
                                            final int number = count;
                                            textViewStatus.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    textViewStatus.setText("Terminated in " + String.valueOf(setting.getInterval() - number));
                                                }
                                            });
                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                        }
                                    }

                                    toggleButtonStart.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            toggleButtonStart.setChecked(status);
                                            GlobalScope.export(getActivity());
                                            textViewStatus.setText("Stopped ");
                                        }
                                    });
                                }
                            });
                            thread.start();
                        }
                    });
                    GlobalScope.smsCampaignService.start();
                } else {
                    if (GlobalScope.smsCampaignService != null) {
                        GlobalScope.smsCampaignService.stop();
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (GlobalScope.smsCampaignService != null) {
            GlobalScope.smsCampaignService.stop();
        }
        super.onDestroy();
    }
}
