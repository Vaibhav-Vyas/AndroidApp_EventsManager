package com.socialapp.eventmanager;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private int year, month, day;
    private OnDateSetListener listener;

    public DatePickerFragment()
    {
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }
    public void setListener(OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Dialog);
        return new DatePickerDialog(wrapper, listener, year, month, day);
    }
}