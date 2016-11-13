package com.ats_qatar.smscampaign.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ats_qatar.smscampaign.R;
import com.ats_qatar.smscampaign.models.Converter;
import com.ats_qatar.smscampaign.models.Resource;
import com.ats_qatar.smscampaign.models.Schedule;
import com.ats_qatar.smscampaign.models.Scope;
import com.ats_qatar.smscampaign.models.Setting;
import com.ats_qatar.smscampaign.services.SmsDispatcher;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Rhalf on 9/28/2016.
 */

public class FragmentDashboard extends Fragment {
    private static final String TAG = FragmentDashboard.class.getSimpleName();

    Intent intentSmsDispatcher;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "SERVICE CONNECTED");
            Toast.makeText(getActivity().getApplicationContext(), "SERVICE CONNECTED", Toast.LENGTH_SHORT).show();
            messenger = new Messenger(service);

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    toggleButtonStart.post(new Runnable() {
                        @Override
                        public void run() {
                            sendRequest("UPDATE");
                        }
                    });
                }
            }, 1000, 1000);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "SERVICE DISCONNECTED");
            Toast.makeText(getActivity().getApplicationContext(), "SERVICE DISCONNECTED", Toast.LENGTH_SHORT).show();
        }
    };

    Messenger messenger;
    Setting setting;
    Schedule schedule;

    View view;
    Timer timer;

    ToggleButton toggleButtonStart;
    ScrollView scrollViewDebug;

    TextView
            textViewMode, textViewTimeStarted, textViewTimeFinished, textViewInterval,
            textViewCount, textViewDays, textViewStatus, textViewProcessed, textViewSent,
            textViewFailed,textViewDelivered, textViewNumber, textViewError;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_dashboard, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        setting = Setting.get(getActivity());
        schedule = Schedule.get(getActivity());

        intentSmsDispatcher = new Intent(getActivity().getApplicationContext(), SmsDispatcher.class);
        intentSmsDispatcher.putExtra("setting", setting);
        intentSmsDispatcher.putExtra("schedule", schedule);

        toggleButtonStart = (ToggleButton) getView().findViewById(R.id.toggleButtonStart);

        textViewMode = (TextView) getView().findViewById(R.id.textViewMode);
        textViewTimeStarted = (TextView) getView().findViewById(R.id.textViewTimeStarted);
        textViewTimeFinished = (TextView) getView().findViewById(R.id.textViewTimeFinished);
        textViewInterval = (TextView) getView().findViewById(R.id.textViewInterval);
        textViewCount = (TextView) getView().findViewById(R.id.textViewCount);

        textViewDays = (TextView) getView().findViewById(R.id.textViewDays);

        textViewNumber = (TextView) getView().findViewById(R.id.textViewNumber);
        textViewProcessed = (TextView) getView().findViewById(R.id.textViewProcessed);
        textViewSent = (TextView) getView().findViewById(R.id.textViewSent);
        textViewFailed = (TextView)  getView().findViewById(R.id.textViewFailed);
        textViewDelivered = (TextView) getView().findViewById(R.id.textViewDelivered);
        textViewStatus = (TextView) getView().findViewById(R.id.textViewStatus);
        textViewError = (TextView) getView().findViewById(R.id.textViewError);
        textViewDays = (TextView) getView().findViewById(R.id.textViewDays);

        scrollViewDebug = (ScrollView) getView().findViewById(R.id.scrollViewDebug);
        //=========================================================================================
        //Initialize
        //=========================================================================================
        switch (setting.mode){
            case Setting.SINGLE:
                textViewMode.setText(("SINGLE"));
                break;
            case Setting.LOOP:
                textViewMode.setText(("LOOP"));
                break;
            case Setting.FILE:
                textViewMode.setText(("FILE"));
                break;
            case Setting.API:
                textViewMode.setText(("API"));
                break;
        }
        textViewTimeStarted.setText(Converter.toString(schedule.timeStarted, Converter.TIME));
        textViewTimeFinished.setText(Converter.toString(schedule.timeFinished, Converter.TIME));

        String days = "";
        if (schedule.days[0]) days += "S,";
        if (schedule.days[1]) days += "M,";
        if (schedule.days[2]) days += "T,";
        if (schedule.days[3]) days += "W,";
        if (schedule.days[4]) days += "T,";
        if (schedule.days[5]) days += "F,";
        if (schedule.days[6]) days += "S";

        if (days.length() == 0)
            days = "NOT SET";

        textViewDays.setText(days);
        textViewInterval.setText(String.valueOf(schedule.interval));

        toggleButtonStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Resource resource = Resource.get(getActivity());
                if (resource.isNotExpired()) {
                    Toast.makeText(getActivity().getApplicationContext(), "ActivationKey is Expired", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isChecked) {
                    sendRequest("START");
                } else {
                    sendRequest("STOP");
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().getApplicationContext().bindService(intentSmsDispatcher, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getApplicationContext().unbindService(serviceConnection);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    public void sendRequest(String command) {
        Message message = Message.obtain(null, SmsDispatcher.REQUEST, 0, 0);
        message.replyTo = new Messenger(new ResponseHandler());
        Bundle bundle = new Bundle();

        bundle.putString("COMMAND", command);


        message.setData(bundle);
        message.replyTo = new Messenger(new ResponseHandler());

        try {
            messenger.send(message);
        } catch (Exception exception) {
            Toast.makeText(getActivity().getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    class ResponseHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            if (message.what == SmsDispatcher.RESPONSE) {
                Bundle bundle = message.getData();


                switch (bundle.getInt("STATUS")) {
                    case SmsDispatcher.RUNNING:
                        textViewStatus.setText("RUNNING");
                        toggleButtonStart.setChecked(true);
                        break;
                    case SmsDispatcher.ERROR:
                        textViewStatus.setText("ERROR");
                        break;
                    case SmsDispatcher.SENDING:
                        textViewStatus.setText("SENDING");
                        break;
                    case SmsDispatcher.WAITING:
                        textViewStatus.setText("WAITING");
                        break;
                    case SmsDispatcher.STOPPED:
                        textViewStatus.setText("STOPPED");
                        toggleButtonStart.setChecked(false);
//                        if (setting.report[2]) {
//                            Scope.writeSummary(
//                                    setting.mode,
//                                    bundle.getInt("SMS_PROCESSED"),
//                                    Scope.smsDetail.totalSent,
//                                    Scope.smsDetail.totalDelivered);
//                        }
                        break;
                }

                textViewCount.setText(String.valueOf(bundle.getInt("COUNT")));

                textViewProcessed.setText(String.valueOf(bundle.getInt("SMS_PROCESSED")));
                textViewDelivered.setText(String.valueOf(Scope.smsDetail.totalDelivered));
                textViewSent.setText(String.valueOf(Scope.smsDetail.totalSent));
                textViewFailed.setText(String.valueOf(Scope.smsDetail.totalFailed));

                textViewNumber.setText(bundle.getString("SMS_NUMBER"));
                textViewError.setText(bundle.getString("MESSAGE_ERROR"));

            }
        }
    }
}
