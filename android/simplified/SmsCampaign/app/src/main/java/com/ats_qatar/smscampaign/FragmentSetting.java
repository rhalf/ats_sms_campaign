package com.ats_qatar.smscampaign;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by Rhalf on 9/28/2016.
 */

public class FragmentSetting extends Fragment implements View.OnClickListener {

    Setting setting;
    View view;

    ToggleButton toggleButtonLoop;
    Spinner spinnerAreaCode;
    EditText editTextPrefix;
    EditText editTextMin;
    EditText editTextMax;
    EditText editTextInterval;
    EditText editTextWorkingHours;
    EditText editTextMessage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_setting, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setting = new Setting(getActivity());

        toggleButtonLoop = (ToggleButton) this.getView().findViewById(R.id.toggleButtonLoop);
        spinnerAreaCode = (Spinner) this.getView().findViewById(R.id.spinnerAreaCode);
        editTextPrefix = (EditText) this.getView().findViewById(R.id.editTextPrefix);
        editTextMin = (EditText) this.getView().findViewById(R.id.editTextMin);
        editTextMax = (EditText) this.getView().findViewById(R.id.editTextMax);
        editTextInterval = (EditText) this.getView().findViewById(R.id.editTextInterval);
        editTextWorkingHours = (EditText) this.getView().findViewById(R.id.editTextWorkingHours);
        editTextMessage = (EditText) this.getView().findViewById(R.id.editTextMessage);

        //Initialize

        toggleButtonLoop.setChecked(setting.getLoop());
        toggleButtonLoop.setOnClickListener(this);

        ArrayList<Nation> nations = Nation.getNations();
        ArrayAdapter<Nation> adapter = new ArrayAdapter<Nation>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                nations);
        spinnerAreaCode.setAdapter(adapter);

        for (int index = 0; index < nations.size(); index++) {
            if (nations.get(index).areaCode == setting.getAreaCode()) {
                spinnerAreaCode.setSelection(index);
            }
        }

        editTextPrefix.setText(setting.getPrefix());
        editTextMin.setText(String.valueOf(setting.getMinimum()));
        editTextMax.setText(String.valueOf(setting.getMaximum()));
        editTextInterval.setText(String.valueOf(setting.getInterval()));
        editTextWorkingHours.setText(String.valueOf(setting.getWorkingTime()));
        editTextMessage.setText(setting.getMessage());
        editTextMin.setEnabled(toggleButtonLoop.isChecked());
        editTextMax.setEnabled(toggleButtonLoop.isChecked());

    }

    @Override
    public void onDestroyView() {

        setting.setLoop(toggleButtonLoop.isChecked());
        Nation nation = (Nation) spinnerAreaCode.getSelectedItem();
        setting.setAreaCode(nation.areaCode);
        setting.setPrefix(editTextPrefix.getText().toString());
        setting.setMinimum(Integer.valueOf(editTextMin.getText().toString()));
        setting.setMaximum(Integer.valueOf(editTextMax.getText().toString()));
        setting.setInterval(Integer.valueOf(editTextInterval.getText().toString()));
        setting.setWorkingTime(Integer.valueOf(editTextWorkingHours.getText().toString()));
        setting.setMessage(editTextMessage.getText().toString());

        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggleButtonLoop: {
                editTextMin.setEnabled(toggleButtonLoop.isChecked());
                editTextMax.setEnabled(toggleButtonLoop.isChecked());
            }
        }
    }
}
