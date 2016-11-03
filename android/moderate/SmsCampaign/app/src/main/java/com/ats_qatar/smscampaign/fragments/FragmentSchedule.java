package com.ats_qatar.smscampaign.fragments;

import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ats_qatar.smscampaign.R;
import com.ats_qatar.smscampaign.models.Converter;
import com.ats_qatar.smscampaign.models.Schedule;
import com.ats_qatar.smscampaign.models.Setting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rhalf on 9/28/2016.
 */

public class FragmentSchedule extends Fragment implements View.OnClickListener {

    Schedule schedule;
    View view;

    CheckBox
            checkBoxSunday, checkBoxMonday, checkBoxTuesday, checkBoxWednesday,
            checkBoxThursday, checkBoxFriday, checkBoxSaturday;
    EditText editTextTimeStarted, editTextTimeFinished, editTextTimeInterval;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_schedule, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextTimeStarted = (EditText) this.getView().findViewById(R.id.editTextTimeStarted);
        editTextTimeFinished = (EditText) this.getView().findViewById(R.id.editTextTimeFinished);
        editTextTimeInterval = (EditText) this.getView().findViewById(R.id.editTextInterval);

        checkBoxSunday = (CheckBox) this.getActivity().findViewById(R.id.checkboxSunday);
        checkBoxMonday = (CheckBox) this.getActivity().findViewById(R.id.checkboxMonday);
        checkBoxTuesday = (CheckBox) this.getActivity().findViewById(R.id.checkboxTuesday);
        checkBoxWednesday = (CheckBox) this.getActivity().findViewById(R.id.checkboxWednesday);
        checkBoxThursday = (CheckBox) this.getActivity().findViewById(R.id.checkboxThursday);
        checkBoxFriday = (CheckBox) this.getActivity().findViewById(R.id.checkboxFriday);
        checkBoxSaturday = (CheckBox) this.getActivity().findViewById(R.id.checkboxSaturday);

        //Initialize
        try {
            schedule = Schedule.get(this.getActivity());
        } catch (Exception exception) {
            Toast.makeText(this.getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        editTextTimeStarted.setText(Converter.toString(schedule.timeStarted, Converter.TIME));
        editTextTimeFinished.setText(Converter.toString(schedule.timeFinished, Converter.TIME));

        checkBoxSunday.setChecked(schedule.days[0]);
        checkBoxMonday.setChecked(schedule.days[1]);
        checkBoxTuesday.setChecked(schedule.days[2]);
        checkBoxWednesday.setChecked(schedule.days[3]);
        checkBoxThursday.setChecked(schedule.days[4]);
        checkBoxFriday.setChecked(schedule.days[5]);
        checkBoxSaturday.setChecked(schedule.days[6]);

        editTextTimeInterval.setText(String.valueOf(schedule.interval));
        editTextTimeStarted.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute);
                                    calendar.set(Calendar.SECOND, 0);
                                    editTextTimeStarted.setText(Converter.toString(calendar, Converter.TIME));
                                }
                            },
                            Converter.toCalendar(editTextTimeStarted.getText().toString(),Converter.TIME).get(Calendar.HOUR_OF_DAY),
                            Converter.toCalendar(editTextTimeStarted.getText().toString(),Converter.TIME).get(Calendar.MINUTE),true);
                    timePickerDialog.show();
                }
            }
        });
        editTextTimeFinished.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute);
                                    calendar.set(Calendar.SECOND, 0);
                                    editTextTimeFinished.setText(Converter.toString(calendar, Converter.TIME));
                                }
                            },
                            Converter.toCalendar(editTextTimeFinished.getText().toString(),Converter.TIME).get(Calendar.HOUR_OF_DAY),
                            Converter.toCalendar(editTextTimeFinished.getText().toString(),Converter.TIME).get(Calendar.MINUTE),true);
                    timePickerDialog.show();
                }
            }

        });
    }

    @Override
    public void onDestroyView() {
        try {

            schedule.days[0] = checkBoxSunday.isChecked();
            schedule.days[1] = checkBoxMonday.isChecked();
            schedule.days[2] = checkBoxTuesday.isChecked();
            schedule.days[3] = checkBoxWednesday.isChecked();
            schedule.days[4] = checkBoxThursday.isChecked();
            schedule.days[5] = checkBoxFriday.isChecked();
            schedule.days[6] = checkBoxSaturday.isChecked();

            schedule.interval = Integer.valueOf(editTextTimeInterval.getText().toString());
            schedule.timeStarted = Converter.toCalendar(editTextTimeStarted.getText().toString(), Converter.TIME);
            schedule.timeFinished = Converter.toCalendar(editTextTimeFinished.getText().toString(), Converter.TIME);

            Schedule.set(this.getActivity(), schedule);
            Toast.makeText(this.getActivity(), "Schedule is saved...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {

    }
}
