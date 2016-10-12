package com.ats_qatar.smscampaign.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.ats_qatar.smscampaign.models.Converter;
import com.ats_qatar.smscampaign.models.Schedule;
import com.ats_qatar.smscampaign.R;
import com.ats_qatar.smscampaign.models.Setting;
import com.ats_qatar.smscampaign.services.SmsDispatcher;


/**
 * Created by Rhalf on 9/28/2016.
 */

public class FragmentDashboard extends Fragment {
    private static final String TAG = FragmentDashboard.class.getSimpleName();

    Intent intentSmsDispatcher;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Connected to service...");
            Toast.makeText(getActivity().getApplicationContext(), "Connected to service...", Toast.LENGTH_SHORT).show();
            messenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected to service...");
            Toast.makeText(getActivity().getApplicationContext(), "Disconnected to service...", Toast.LENGTH_SHORT).show();
        }
    };

    Messenger messenger;
    Setting setting;
    Schedule schedule;

    View view;

    ToggleButton toggleButtonStart;
    ScrollView scrollViewDebug;

    TextView textViewTimeStarted, textViewTimeFinished, textViewInterval, textViewCount, textViewDays, textViewStatus, textViewSent, textViewNumber, textViewMessage;


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
        textViewTimeStarted = (TextView) getView().findViewById(R.id.textViewTimeStarted);
        textViewTimeFinished = (TextView) getView().findViewById(R.id.textViewTimeFinished);
        textViewInterval = (TextView) getView().findViewById(R.id.textViewInterval);
        textViewCount = (TextView) getView().findViewById(R.id.textViewCount);

        textViewDays = (TextView) getView().findViewById(R.id.textViewDays);

        textViewNumber = (TextView) getView().findViewById(R.id.textViewNumber);
        textViewSent = (TextView) getView().findViewById(R.id.textViewSent);
        textViewStatus = (TextView) getView().findViewById(R.id.textViewStatus);
        textViewMessage = (TextView) getView().findViewById(R.id.textViewMessage);
        textViewDays = (TextView) getView().findViewById(R.id.textViewDays);

        scrollViewDebug = (ScrollView) getView().findViewById(R.id.scrollViewDebug);


//        broadcastReceiverSmsDispatcher = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent == null)
//                    return;
//
//                String days = "";
//                if (schedule.days[0]) days += "S,";
//                if (schedule.days[1]) days += "M,";
//                if (schedule.days[2]) days += "T,";
//                if (schedule.days[3]) days += "W,";
//                if (schedule.days[4]) days += "T,";
//                if (schedule.days[5]) days += "F,";
//                if (schedule.days[6]) days += "S";
//
//                textViewTimeStarted.setText(Converter.toString(schedule.timeStarted, Converter.TIME));
//                textViewTimeFinished.setText(Converter.toString(schedule.timeFinished, Converter.TIME));
//                textViewInterval.setText(String.valueOf(schedule.interval));
//                textViewCount.setText(String.valueOf(intent.getExtras().getInt("count")));
//
//                textViewDays.setText(days);
//
//                textViewStatus.setText(String.valueOf(intent.getExtras().getInt("status")));
//                textViewSent.setText(String.valueOf(intent.getExtras().getInt("sent")));
//                textViewNumber.setText(intent.getExtras().getString("number"));
//                textViewMessage.setText(intent.getExtras().getString("message"));
//
//            }
//        };
//
//        getActivity().getApplicationContext().registerReceiver(broadcastReceiverSmsDispatcher, new IntentFilter("SMS_DISPATCHER"));

        toggleButtonStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendRequest();
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

    public void sendRequest() {
        Message message = Message.obtain(null, SmsDispatcher.REQUEST, 0, 0);
        message.replyTo = new Messenger(new ResponseHandler());

        Bundle bundle = new Bundle();
        bundle.putString("REQUEST", "REQUEST:XASDFVXBGGHHKL");
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
            switch (message.what) {
                case SmsDispatcher.RESPONSE:
                    Bundle bundle = message.getData();
                    Toast.makeText(getActivity().getApplicationContext(), bundle.getString("REPLY"), Toast.LENGTH_SHORT).show();
                default:
            }
        }
    }
}
