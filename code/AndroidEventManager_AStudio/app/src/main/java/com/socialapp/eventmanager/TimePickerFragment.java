package com.socialapp.eventmanager;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    private int hourOfDay, minute;
    private TimePickerDialog.OnTimeSetListener listener;

    public TimePickerFragment()
    {
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        hourOfDay = args.getInt("hourOfDay");
        minute = args.getInt("minute");
    }
    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), listener, hourOfDay, minute, true);
    }
}