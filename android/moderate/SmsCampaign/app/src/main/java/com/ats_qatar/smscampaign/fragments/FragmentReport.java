package com.ats_qatar.smscampaign.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ats_qatar.smscampaign.models.Scope;
import com.ats_qatar.smscampaign.R;
import com.ats_qatar.smscampaign.models.Sms;

/**
 * Created by Rhalf on 9/28/2016.
 */

public class FragmentReport extends Fragment implements View.OnClickListener {

    View view;

    TextView textViewDebug;
    ScrollView scrollViewDebug;
    Button buttonRefresh;
    Button buttonSave;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_report, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewDebug = (TextView) getView().findViewById(R.id.textViewDebug);
        scrollViewDebug = (ScrollView) getView().findViewById(R.id.scrollViewDebug);
        buttonRefresh = (Button) getView().findViewById(R.id.buttonRefresh);
        buttonSave = (Button) getView().findViewById(R.id.buttonSave);


        buttonRefresh.setOnClickListener(this);
        buttonSave.setOnClickListener(this);


//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//
//                    try {
//                        Thread.sleep(1000);
//                        showSms();
//                    } catch (Exception exception) {
//                        final String message = exception.getMessage();
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getActivity().getBaseContext(), message, Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                }
//            }
//        };
//        Thread threadUi = new Thread(runnable);
//        threadUi.start();
    }

    private void showSms() {
        textViewDebug.post(new Runnable() {
            @Override
            public void run() {
                textViewDebug.setText("");

                if (Scope.smsContainer.items.size() <= 0)
                    return;

                for (Sms sms : Scope.smsContainer.items) {
                    textViewDebug.append("============" + sms.dateTimeCreated + "============" + "\n");
                    textViewDebug.append("Id : " + sms.id + "\n");
                    textViewDebug.append("DtCreated : " + sms.dateTimeCreated + "\n");
                    textViewDebug.append("Number : " + sms.number + "\n");
                    textViewDebug.append("Message : " + sms.message + "\n");
                    textViewDebug.append("Center : " + sms.center + "\n");
                    textViewDebug.append("DtSent : " + sms.dateTimeSent + "\n");
                    textViewDebug.append("Sent : " + sms.sent + "\n");
                    textViewDebug.append("DtDelivered : " + sms.dateTimeDelivered + "\n");
                    textViewDebug.append("Delivered : " + sms.delivered + "\n");
                    textViewDebug.append("Error : " + sms.error + "\n");

                    textViewDebug.append("\n");
                }

                scrollViewDebug.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRefresh: {
                showSms();
                break;
            }
            case R.id.buttonSave: {
                //Scope.export(this.getActivity());
                break;
            }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
