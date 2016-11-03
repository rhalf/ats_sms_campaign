package com.ats_qatar.smscampaign.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.ats_qatar.smscampaign.R;
import com.ats_qatar.smscampaign.models.Nation;
import com.ats_qatar.smscampaign.models.Scope;
import com.ats_qatar.smscampaign.models.Setting;

import java.util.ArrayList;

/**
 * Created by Rhalf on 9/28/2016.
 */

public class FragmentSetting extends Fragment {

    Setting setting;
    View view;

    LinearLayout linearLayoutSingle, linearLayoutLoop, linearLayoutFile, linearLayoutApi;

    Spinner spinnerAreaCode, spinnerFile;
    EditText editTextMessage;

    RadioGroup radioGroupProfile;
    CheckBox checkBoxSent, checkBoxDelivered, checkBoxSummary;
    RadioButton radioButtonSingle, radioButtonLoop, radioButtonFile, radioButtonApi;
    EditText editTextNumber, editTextPrefix, editTextPadding, editTextMin, editTextMax, editTextUrl;
    Button buttonTryParse;

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

        checkBoxSent = (CheckBox) this.getView().findViewById(R.id.checkboxSent);
        checkBoxDelivered = (CheckBox) this.getView().findViewById(R.id.checkboxDelivered);
        checkBoxSummary = (CheckBox) this.getView().findViewById(R.id.checkboxSummary);


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

        spinnerFile = (Spinner) this.getView().findViewById(R.id.spinnerFile);
        editTextUrl = (EditText) this.getView().findViewById(R.id.editTextUrl);

        buttonTryParse = (Button) this.getView().findViewById(R.id.buttonTryParse);

        //Initialize

        buttonTryParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<String> numbers = Scope.parseNumberFrom(spinnerFile.getSelectedItem().toString());
                    Toast.makeText(getActivity(), "Parsing : OK\nNumbers : " + numbers.size(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        checkBoxSent.setChecked(setting.report[0]);
        checkBoxDelivered.setChecked(setting.report[1]);
        checkBoxSummary.setChecked(setting.report[2]);

        editTextMessage.setText(setting.message);

        switch (setting.mode) {
            case Setting.SINGLE:
                radioButtonSingle.setChecked(true);
                break;
            case Setting.LOOP:
                radioButtonLoop.setChecked(true);
                break;
            case Setting.FILE:
                radioButtonFile.setChecked(true);
                break;
            case Setting.API:
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
                        update(Setting.LOOP);
                        break;
                    case R.id.radioButtonFile:
                        update(Setting.FILE);
                        break;
                    case R.id.radioButtonApi:
                        update(Setting.API);
                        break;
                }
            }
        });

        editTextNumber.setText(setting.number);

        editTextPrefix.setText(String.valueOf(setting.prefix));
        editTextPadding.setText(String.valueOf(setting.padding));
        editTextMin.setText(String.valueOf(setting.min));
        editTextMax.setText(String.valueOf(setting.max));

        //Initialize
        if (Scope.getImportFiles() != null) {
            ArrayAdapter<String> files = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_spinner_dropdown_item,
                    Scope.getImportFiles());
            spinnerFile.setAdapter(files);
            spinnerFile.setSelection(files.getPosition(setting.path));
        }

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
            case Setting.LOOP:
                linearLayoutSingle.setVisibility(View.GONE);
                linearLayoutLoop.setVisibility(View.VISIBLE);
                linearLayoutFile.setVisibility(View.GONE);
                linearLayoutApi.setVisibility(View.GONE);
                setting.mode = Setting.LOOP;
                break;
            case Setting.FILE:
                linearLayoutSingle.setVisibility(View.GONE);
                linearLayoutLoop.setVisibility(View.GONE);
                linearLayoutFile.setVisibility(View.VISIBLE);
                linearLayoutApi.setVisibility(View.GONE);
                setting.mode = Setting.FILE;
                break;
            case Setting.API:
                linearLayoutSingle.setVisibility(View.GONE);
                linearLayoutLoop.setVisibility(View.GONE);
                linearLayoutFile.setVisibility(View.GONE);
                linearLayoutApi.setVisibility(View.VISIBLE);
                setting.mode = Setting.API;
                break;
        }
    }


    @Override
    public void onDestroy() {
        try {
            Nation nation = (Nation) spinnerAreaCode.getSelectedItem();
            setting.areaCode = (nation.areaCode);
            setting.message = (editTextMessage.getText().toString());
            setting.report[0] = checkBoxSent.isChecked();
            setting.report[1] = checkBoxDelivered.isChecked();
            setting.report[2] = checkBoxSummary.isChecked();

            //SINGLE
            setting.number = editTextNumber.getText().toString();
            //LOOP
            setting.prefix = Integer.valueOf(editTextPrefix.getText().toString());
            setting.padding = Integer.valueOf(editTextPadding.getText().toString());
            setting.min = (Integer.valueOf(editTextMin.getText().toString()));
            setting.max = (Integer.valueOf(editTextMax.getText().toString()));
            //FILE
            if (spinnerFile.getSelectedItem() != null) {
                setting.path = spinnerFile.getSelectedItem().toString();
            } else {
                setting.path = "/";
            }

            //API
            setting.url = editTextUrl.getText().toString();

            Setting.set(this.getActivity(), setting);

            Toast.makeText(this.getActivity(), "Setting is saved...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }
}
