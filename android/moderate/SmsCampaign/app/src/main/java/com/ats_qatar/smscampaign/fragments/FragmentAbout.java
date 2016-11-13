package com.ats_qatar.smscampaign.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ats_qatar.smscampaign.R;
import com.ats_qatar.smscampaign.models.Converter;
import com.ats_qatar.smscampaign.models.Crypt;
import com.ats_qatar.smscampaign.models.Resource;

/**
 * Created by Rhalf on 9/28/2016.
 */

public class FragmentAbout extends Fragment implements View.OnClickListener {

    View view;

    TextView textViewImei, textViewDtExpire, textViewSmsCreditConsumed, textViewSmsCreditLimit;

    EditText editTextKey;

    Button buttonActivate;

    int trials = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_about, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewImei = (TextView) getActivity().findViewById(R.id.textViewImei);

        textViewDtExpire = (TextView) getActivity().findViewById(R.id.textViewDtExpire);

        textViewSmsCreditConsumed = (TextView) getActivity().findViewById(R.id.textViewSmsCreditConsumed);

        textViewSmsCreditLimit = (TextView) getActivity().findViewById(R.id.textViewSmsCreditLimit);

        editTextKey = (EditText) getActivity().findViewById(R.id.editTextKey);

        buttonActivate = (Button) getActivity().findViewById(R.id.buttonActivate);

        buttonActivate.setOnClickListener(this);

        load();
    }

    private void load() {
        Resource resource = Resource.get(this.getActivity().getApplicationContext());

        textViewImei.setText(resource.imei);

        textViewDtExpire.setText(Converter.toString(resource.dtExpire, Converter.DATE));

        textViewSmsCreditConsumed.setText(String.valueOf(resource.smsCreditConsumed));

        textViewSmsCreditLimit.setText(String.valueOf(resource.smsCreditLimit));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonActivate: {

                if (trials == 3) {
                    getActivity().finish();
                } else {
                    trials++;
                }

                Crypt crypt = Crypt.getInstance();
                Resource resource = Resource.get(this.getActivity());

                if (resource.activationKey != null) {
                    if (editTextKey.getText().toString().compareTo(resource.activationKey) == 0) {
                        Toast.makeText(getActivity(), "Key is already in used...", Toast.LENGTH_LONG).show();
                        return;
                    }
                }


                try {

                    byte[] raw = crypt.decrypt(resource.imei + "0", Crypt.toByte(editTextKey.getText().toString()));
                    String data = new String(raw, "UTF-8");

                    String[] datas = data.split(",");

                    resource.dtExpire = Converter.toCalendar(datas[0], Converter.DATE);
                    resource.smsCreditLimit = Integer.valueOf(datas[1]);
                    resource.smsCreditConsumed = 0;
                    resource.activationKey = editTextKey.getText().toString();
                    Resource.set(getActivity(), resource);

                    load();

                    Toast.makeText(getActivity(), "New ActivationKey has been loaded...", Toast.LENGTH_LONG).show();
                } catch (Exception exception) {
                    //Toast.makeText(getActivity(),exception.getMessage(),Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "Wrong ActivationKey! " + String.valueOf(3 - trials) + " trials remaining.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


}
