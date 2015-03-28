package com.socialapp.eventmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CreateEventActivity extends FragmentActivity {
    private Button startDate;
    private Button endDate;
    private Button startTime;
    private Button endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        startDate = (Button)findViewById(R.id.start_date);
        endDate = (Button)findViewById(R.id.end_date);
        startTime = (Button)findViewById(R.id.start_time);
        endTime = (Button)findViewById(R.id.end_time);

        startDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()));
        endDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()));

        startTime.setText(new SimpleDateFormat("hh:mm aa").format(Calendar.getInstance().getTime()));
        endTime.setText(new SimpleDateFormat("hh:mm aa").format(Calendar.getInstance().getTime()));
    }

    public void onClick(final View v)
    {
        switch (v.getId())
        {
            case R.id.start_date:
            case R.id.end_date:
            {
                DatePickerFragment date = new DatePickerFragment();
                Calendar calender = Calendar.getInstance();
                Bundle args = new Bundle();
                args.putInt("year", calender.get(Calendar.YEAR));
                args.putInt("month", calender.get(Calendar.MONTH));
                args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
                date.setArguments(args);
                date.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        ((Button) v).setText(new SimpleDateFormat("MM/dd/yyyy").format(calendar.getTime()));
                    }
                });
                date.show(getSupportFragmentManager(), "Date Picker");
            }
            break;

            case R.id.start_time:
            case R.id.end_time:
            {
                TimePickerFragment date = new TimePickerFragment();
                Calendar calender = Calendar.getInstance();
                Bundle args = new Bundle();
                args.putInt("hourOfDay", calender.get(Calendar.HOUR_OF_DAY));
                args.putInt("minute", calender.get(Calendar.MINUTE));
                date.setArguments(args);
                date.setListener(new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)  {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        ((Button)v).setText(new SimpleDateFormat("hh:mm aa").format(calendar.getTime()));
                    }});
                date.show(getSupportFragmentManager(), "Date Picker");
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
