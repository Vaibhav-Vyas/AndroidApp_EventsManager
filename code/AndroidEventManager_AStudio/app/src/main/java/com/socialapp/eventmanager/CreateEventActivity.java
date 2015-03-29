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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.socialapp.eventmanager.Models.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CreateEventActivity extends FragmentActivity {
    private Button startDate;
    private Button endDate;
    private Button startTime;
    private Button endTime;


    private Switch public_private_switch;
    private Switch invitation_allowed_switch;

    Calendar start, end;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        startDate = (Button)findViewById(R.id.start_date);
        endDate = (Button)findViewById(R.id.end_date);
        startTime = (Button)findViewById(R.id.start_time);
        endTime = (Button)findViewById(R.id.end_time);

        start = Calendar.getInstance();
        end = Calendar.getInstance();

        startDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(start.getTime()));
        endDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(end.getTime()));

        startTime.setText(new SimpleDateFormat("hh:mm aa").format(start.getTime()));
        endTime.setText(new SimpleDateFormat("hh:mm aa").format(end.getTime()));
    }

    public void onClick(final View v)
    {
        switch (v.getId())
        {
            case R.id.start_date:
            {
                DatePickerFragment date = new DatePickerFragment();
                Bundle args = new Bundle();
                args.putInt("year", start.get(Calendar.YEAR));
                args.putInt("month", start.get(Calendar.MONTH));
                args.putInt("day", start.get(Calendar.DAY_OF_MONTH));
                date.setArguments(args);
                date.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        start.set(Calendar.YEAR, year);
                        start.set(Calendar.MONTH, monthOfYear);
                        start.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        ((Button) v).setText(new SimpleDateFormat("MM/dd/yyyy").format(start.getTime()));
                    }
                });
                date.show(getSupportFragmentManager(), "Date Picker");
            }
            break;
            case R.id.end_date:
            {
                DatePickerFragment date = new DatePickerFragment();
                Bundle args = new Bundle();
                args.putInt("year", end.get(Calendar.YEAR));
                args.putInt("month", end.get(Calendar.MONTH));
                args.putInt("day", end.get(Calendar.DAY_OF_MONTH));
                date.setArguments(args);
                date.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        end.set(Calendar.YEAR, year);
                        end.set(Calendar.MONTH, monthOfYear);
                        end.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        ((Button) v).setText(new SimpleDateFormat("MM/dd/yyyy").format(end.getTime()));
                    }
                });
                date.show(getSupportFragmentManager(), "Date Picker");
            }
            break;

            case R.id.start_time:
            {
                TimePickerFragment date = new TimePickerFragment();
                Bundle args = new Bundle();
                args.putInt("hourOfDay", start.get(Calendar.HOUR_OF_DAY));
                args.putInt("minute", start.get(Calendar.MINUTE));
                date.setArguments(args);
                date.setListener(new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)  {
                        start.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        start.set(Calendar.MINUTE, minute);
                        ((Button)v).setText(new SimpleDateFormat("hh:mm aa").format(start.getTime()));
                    }});
                date.show(getSupportFragmentManager(), "Date Picker");
            }
            break;
            case R.id.end_time:
            {
                TimePickerFragment date = new TimePickerFragment();
                Bundle args = new Bundle();
                args.putInt("hourOfDay", end.get(Calendar.HOUR_OF_DAY));
                args.putInt("minute", end.get(Calendar.MINUTE));
                date.setArguments(args);
                date.setListener(new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)  {
                        end.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        end.set(Calendar.MINUTE, minute);
                        ((Button)v).setText(new SimpleDateFormat("hh:mm aa").format(end.getTime()));
                    }});
                date.show(getSupportFragmentManager(), "Date Picker");
            }
            break;


            case R.id.create_event_button:
                Event event = new Event();

                event.eventId = "dummy" + (long)Calendar.getInstance().getTimeInMillis()/1000;

                EditText editText = (EditText)findViewById(R.id.event_name);
                event.name= editText.getText().toString();

                editText = (EditText)findViewById(R.id.event_location);
                event.location = editText.getText().toString();

                editText = (EditText)findViewById(R.id.event_description);
                event.description = editText.getText().toString();


                public_private_switch = (Switch) findViewById(R.id.publicPrivate);
                if(public_private_switch.isChecked()){
                    event.public_event=true;
                }else {
                    event.public_event=false;
                }

                invitation_allowed_switch = (Switch) findViewById(R.id.invitationAllowed);
                if(invitation_allowed_switch.isChecked()){
                    event.invitation_allowed=true;
                }else {
                    event.invitation_allowed=false;
                }


                /////// Date and time
                event.start_time = start.getTimeInMillis() / 1000;
                event.end_time = end.getTimeInMillis() / 1000;


                event.save();


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
