package com.ats_qatar.smscampaign.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    TextView textViewUser, textViewDtExpire, editTextKey;

    Button buttonActivate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_about, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewUser = (TextView) getActivity().findViewById(R.id.textViewUser);
        textViewDtExpire = (TextView) getActivity().findViewById(R.id.textViewDtExpire);
        editTextKey = (TextView) getActivity().findViewById(R.id.editTextKey);

        buttonActivate = (Button) getActivity().findViewById(R.id.buttonActivate);

        Resource resource = Resource.get(this.getActivity().getApplicationContext());

        textViewUser.setText(resource.userId);
        textViewDtExpire.setText(Converter.toString(resource.dtExpire, Converter.DATE));
        buttonActivate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonActivate: {

                try {
                    Crypt crypt = Crypt.getInstance();
                    Resource resource = Resource.get(this.getActivity());
                    byte[] result = crypt.encrypt(resource.userId, "20-12-06".getBytes());
                    editTextKey.setText(Crypt.toHex(result));

                    byte[] raw = crypt.decrypt("5hCQazvy",result);
                    Toast.makeText(getActivity(),new String(raw,"UTF-8"),Toast.LENGTH_LONG).show();

                }catch (Exception exception) {
                    Toast.makeText(getActivity(),exception.getMessage(),Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


}
