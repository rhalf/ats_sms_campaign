package com.ats_qatar.smscampaign.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ats_qatar.smscampaign.R;
import com.ats_qatar.smscampaign.models.Nation;
import com.ats_qatar.smscampaign.models.Setting;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Rhalf on 9/28/2016.
 */

public class FragmentSetting extends Fragment {

    Setting setting;
    View view;

    LinearLayout linearLayoutSingle, linearLayoutLoop, linearLayoutFile, linearLayoutApi;

    Spinner spinnerAreaCode;
    EditText editTextMessage;

    RadioGroup radioGroupProfile;
    RadioButton radioButtonSingle, radioButtonLoop, radioButtonFile, radioButtonApi;

    EditText editTextNumber;

    EditText editTextPrefix;
    EditText editTextPadding;
    EditText editTextMin;
    EditText editTextMax;

    EditText editTextPath;

    EditText editTextUrl;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_setting, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setting = Setting.get(this.getActivity());


        linearLayoutSingle = (LinearLayout) this.getView().findViewById(R.id.linearLayoutSingle);
        linearLayoutLoop = (LinearLayout) this.getView().findViewById(R.id.linearLayoutLoop);
        linearLayoutFile = (LinearLayout) this.getView().findViewById(R.id.linearLayoutFile);
        linearLayoutApi = (LinearLayout) this.getView().findViewById(R.id.linearLayoutApi);


        spinnerAreaCode = (Spinner) this.getView().findViewById(R.id.spinnerAreaCode);
        editTextMessage = (EditText) this.getView().findViewById(R.id.editTextMessage);

        radioGroupProfile = (RadioGroup) this.getView().findViewById(R.id.radioGroupProfile);
        radioButtonSingle = (RadioButton) this.getView().findViewById(R.id.radioButtonSingle);
        radioButtonLoop = (RadioButton) this.getView().findViewById(R.id.radioButtonLoop);
        radioButtonFile = (RadioButton) this.getView().findViewById(R.id.radioButtonFile);
        radioButtonApi = (RadioButton) this.getView().findViewById(R.id.radioButtonApi);

        editTextNumber = (EditText) this.getView().findViewById(R.id.editTextNumber);

        editTextPrefix = (EditText) this.getView().findViewById(R.id.editTextPrefix);
        editTextPadding = (EditText) this.getView().findViewById(R.id.editTextPadding);
        editTextMin = (EditText) this.getView().findViewById(R.id.editTextMin);
        editTextMax = (EditText) this.getView().findViewById(R.id.editTextMax);

        editTextPath = (EditText) this.getView().findViewById(R.id.editTextPath);
        editTextUrl = (EditText) this.getView().findViewById(R.id.editTextUrl);

        //Initialize
        ArrayList<Nation> nations = Nation.getNations();
        ArrayAdapter<Nation> adapter = new ArrayAdapter<Nation>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                nations);
        spinnerAreaCode.setAdapter(adapter);

        for (int index = 0; index < nations.size(); index++) {
            if (nations.get(index).areaCode == setting.areaCode) {
                spinnerAreaCode.setSelection(index);
            }
        }

        editTextMessage.setText(setting.message);

        switch (setting.mode) {
            case Setting.SINGLE:
                radioButtonSingle.setChecked(true);
                break;
            case Setting.LOOPING:
                radioButtonLoop.setChecked(true);
                break;
            case Setting.IMPORT_FILE:
                radioButtonFile.setChecked(true);
                break;
            case Setting.WEB_SERVICE:
                radioButtonApi.setChecked(true);
                break;
        }

        radioGroupProfile.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonSingle:
                        update(Setting.SINGLE);
                        break;
                    case R.id.radioButtonLoop:
                        update(Setting.LOOPING);
                        break;
                    case R.id.radioButtonFile:
                        update(Setting.IMPORT_FILE);
                        break;
                    case R.id.radioButtonApi:
                        update(Setting.WEB_SERVICE);
                        break;
                }
            }
        });

        editTextNumber.setText(setting.number);

        editTextPrefix.setText(String.valueOf(setting.prefix));
        editTextPadding.setText(String.valueOf(setting.padding));
        editTextMin.setText(String.valueOf(setting.min));
        editTextMax.setText(String.valueOf(setting.max));

        editTextPath.setText(setting.path);
        editTextPath.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                }
            }
        });

        editTextUrl.setText(setting.url);

        update(setting.mode);
    }

    private void update(int mode) {
        switch (mode) {
            case Setting.SINGLE:
                linearLayoutSingle.setVisibility(View.VISIBLE);
                linearLayoutLoop.setVisibility(View.GONE);
                linearLayoutFile.setVisibility(View.GONE);
                linearLayoutApi.setVisibility(View.GONE);
                setting.mode = Setting.SINGLE;
                break;
            case Setting.LOOPING:
                linearLayoutSingle.setVisibility(View.GONE);
                linearLayoutLoop.setVisibility(View.VISIBLE);
                linearLayoutFile.setVisibility(View.GONE);
                linearLayoutApi.setVisibility(View.GONE);
                setting.mode = Setting.LOOPING;
                break;
            case Setting.IMPORT_FILE:
                linearLayoutSingle.setVisibility(View.GONE);
                linearLayoutLoop.setVisibility(View.GONE);
                linearLayoutFile.setVisibility(View.VISIBLE);
                linearLayoutApi.setVisibility(View.GONE);
                setting.mode = Setting.IMPORT_FILE;
                break;
            case Setting.WEB_SERVICE:
                linearLayoutSingle.setVisibility(View.GONE);
                linearLayoutLoop.setVisibility(View.GONE);
                linearLayoutFile.setVisibility(View.GONE);
                linearLayoutApi.setVisibility(View.VISIBLE);
                setting.mode = Setting.WEB_SERVICE;
                break;
        }
    }

    @Override
    public void onDestroyView() {
        try {
            Nation nation = (Nation) spinnerAreaCode.getSelectedItem();
            setting.areaCode = (nation.areaCode);
            setting.message = (editTextMessage.getText().toString());
            //SINGLE
            setting.number = editTextNumber.getText().toString();
            //LOOP
            setting.prefix = Integer.valueOf(editTextPrefix.getText().toString());
            setting.padding = Integer.valueOf(editTextPadding.getText().toString());
            setting.min = (Integer.valueOf(editTextMin.getText().toString()));
            setting.max = (Integer.valueOf(editTextMax.getText().toString()));
            //FILE
            setting.path = editTextPath.getText().toString();
            //API
            setting.url = editTextUrl.getText().toString();

            Setting.set(this.getActivity(), setting);
            Toast.makeText(this.getActivity(), "Setting is saved...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        super.onDestroyView();
    }


}
