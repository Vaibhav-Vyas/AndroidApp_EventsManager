package com.socialapp.eventmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.socialapp.eventmanager.Models.Event;

import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CreateEventActivity extends FragmentActivity {
    private static final String TAG = "Create event tag:";

    private static final int RESULT_LOAD_IMG = 1;
    private String imgDecodableString;

    private Button startDateButton;
    private Button endDateButton;
    private Button startTimeButton;
    private Button endTimeButton;
    private Switch public_private_switch;
    private Switch invitation_allowed_switch;
    Calendar start, end;

    private Event editedEvent = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        startDateButton = (Button)findViewById(R.id.start_date);
        endDateButton = (Button)findViewById(R.id.end_date);
        startTimeButton = (Button)findViewById(R.id.start_time);
        endTimeButton = (Button)findViewById(R.id.end_time);
        public_private_switch = (Switch) findViewById(R.id.publicPrivate);
        invitation_allowed_switch = (Switch) findViewById(R.id.invitationAllowed);

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY) + 1);

        startDateButton.setText(new SimpleDateFormat("MM/dd/yyyy").format(start.getTime()));
        endDateButton.setText(new SimpleDateFormat("MM/dd/yyyy").format(end.getTime()));
        startTimeButton.setText(new SimpleDateFormat("hh:mm aa").format(start.getTime()));
        endTimeButton.setText(new SimpleDateFormat("hh:mm aa").format(end.getTime()));

        initializeIfEditingEvent();
    }

    public void initializeIfEditingEvent()
    {
        String eventId = getIntent().getStringExtra("editEvent");
        if(eventId != null)
        {
            String[] queryArgs = new String[1];
            queryArgs[0] = "" + eventId;
            editedEvent = Event.find(Event.class, "eventId = ?", queryArgs, null, null, null).get(0);
            ((EditText)findViewById(R.id.event_name)).setText(editedEvent.name);
            ((EditText)findViewById(R.id.event_location)).setText(editedEvent.location);
            ((EditText)findViewById(R.id.event_description)).setText(editedEvent.description);

            start.setTimeInMillis(editedEvent.start_time);
            end.setTimeInMillis(editedEvent.end_time);
            public_private_switch.setChecked(editedEvent.public_event);
            invitation_allowed_switch.setChecked(editedEvent.invitation_allowed);
            startDateButton.setText(new SimpleDateFormat("MM/dd/yyyy").format(start.getTime()));
            endDateButton.setText(new SimpleDateFormat("MM/dd/yyyy").format(end.getTime()));
            startTimeButton.setText(new SimpleDateFormat("hh:mm aa").format(start.getTime()));
            endTimeButton.setText(new SimpleDateFormat("hh:mm aa").format(end.getTime()));
            ((TextView)findViewById(R.id.create_event_button)).setText("Save");
        }
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
                        end.set(Calendar.YEAR, year);
                        end.set(Calendar.MONTH, monthOfYear);
                        end.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        startDateButton.setText(new SimpleDateFormat("MM/dd/yyyy").format(start.getTime()));
                        endDateButton.setText(new SimpleDateFormat("MM/dd/yyyy").format(start.getTime()));
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
                        endDateButton.setText(new SimpleDateFormat("MM/dd/yyyy").format(end.getTime()));
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
                        end.set(Calendar.MINUTE, start.get(Calendar.MINUTE));
                        end.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY) + 1);
                        startTimeButton.setText(new SimpleDateFormat("hh:mm aa").format(start.getTime()));
                        endTimeButton.setText(new SimpleDateFormat("hh:mm aa").format(end.getTime()));
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
                        endTimeButton.setText(new SimpleDateFormat("hh:mm aa").format(end.getTime()));
                    }});
                date.show(getSupportFragmentManager(), "Date Picker");
            }
            break;

            case R.id.eventImage:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            break;

            case R.id.cancelEvent:
                finish();
                break;

            case R.id.create_event_button:
                final Event event = (editedEvent == null) ? new Event() : editedEvent;

                EditText editText = (EditText)findViewById(R.id.event_name);
                event.name= editText.getText().toString();

                editText = (EditText)findViewById(R.id.event_location);
                event.location = editText.getText().toString();

                editText = (EditText)findViewById(R.id.event_description);
                event.description = editText.getText().toString();

                event.image_url = imgDecodableString;
                event.public_event = public_private_switch.isChecked();
                event.invitation_allowed = invitation_allowed_switch.isChecked();

                /////// Date and time
                event.start_time = start.getTimeInMillis();
                event.end_time = end.getTimeInMillis();

                // Owner
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                event.owner= prefs.getString("email", null);
                event.status = "owner";

                if((event.name).matches("")){
                    Toast.makeText(this, "Please insert a name for the event", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("Sujith", " Saving event with start_time = " + event.start_time);
                    event.save();
                    saveEventToBackend(event);
                }
                break;
        }
    }

    public void saveEventToBackend(final Event event)
    {
        if(editedEvent == null) {
            Backend.createEvent(event, new Backend.CreateEventCallback() {
                @Override
                public void onRequestCompleted(final String result) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                JSONObject obj = new JSONObject(result);
                                event.event_id = obj.getString("eventId");
                                event.save();

                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                if (event.image_url != null) {
                                    sendEventImageToBackend(event);
                                }

                                Gson gson = new GsonBuilder().create();
                                Intent intent = new Intent(getApplicationContext(), DisplayEventActivity.class);
                                String eventJSON = gson.toJson(event, Event.class);
                                intent.putExtra("event", eventJSON);
                                intent.putExtra("location", "local");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                //addEventToCalender(event);
                            } catch (Throwable t) {
                                Log.d(TAG, "Error converting result to json");
                            }
                        }
                    });
                }

                @Override
                public void onRequestFailed(final String message) {
                    //NOTE: parameter validation and filtering is handled by the backend, just show the
                    //returned error message to the user
                    Log.d(TAG, "Received error from Backend: " + message);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        else {
            event.save();
            Backend.editEvent(event, new Backend.CreateEventCallback() {
                @Override
                public void onRequestCompleted(final String result) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                //JSONObject obj = new JSONObject(result);
                                event.save();

                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                if (event.image_url != null) {
                                    sendEventImageToBackend(event);
                                }

                                Gson gson = new GsonBuilder().create();
                                Intent intent = new Intent(getApplicationContext(), DisplayEventActivity.class);
                                String eventJSON = gson.toJson(event, Event.class);
                                intent.putExtra("event", eventJSON);
                                intent.putExtra("location", "local");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                //addEventToCalender(event);
                            } catch (Throwable t) {
                                Log.d(TAG, "Error converting result to json in edit event");
                            }
                        }
                    });
                }

                @Override
                public void onRequestFailed(final String message) {
                    //NOTE: parameter validation and filtering is handled by the backend, just show the
                    //returned error message to the user
                    Log.d(TAG, "Received error from Backend: " + message);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    public void sendEventImageToBackend(final Event event)
    {
        Backend.saveImage(event, new Backend.BackendCallback() {
            @Override
            public void onRequestCompleted(final String result) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "Image saved successfully on backend");
                    }
                });
            }

            @Override
            public void onRequestFailed(final String message) {
                Log.d(TAG, "Received error from Backend: " + message);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();

                Log.d(TAG, selectedImage.toString());

                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);

                Log.d(TAG, imgDecodableString);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
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

    public void addEventToCalender(Event event)
    {
        String title = event.name;
        String description = event.description;
        String location = event.location;
        long start_time = event.start_time * 1000;
        long end_time = event.end_time * 1000;
        int access_level = event.public_event ?
                CalendarContract.Events.ACCESS_PUBLIC :
                CalendarContract.Events.ACCESS_PRIVATE;

        if (Build.VERSION.SDK_INT >= 14) {
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start_time)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end_time)
                    .putExtra(CalendarContract.Events.TITLE, "AndroidEventManager: " + title)
                    .putExtra(CalendarContract.Events.DESCRIPTION, "AndroidEventManager: " + description)
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "AndroidEventManager: " + location)
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                    .putExtra(CalendarContract.Events.ACCESS_LEVEL, access_level)
                    .putExtra(Intent.EXTRA_EMAIL, "a@b.com,c@d.com");
            Toast.makeText(this, "Event about to be added to the default calender. Please click SAVE.", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        else {
            Calendar cal = Calendar.getInstance();
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", start_time);
            intent.putExtra("allDay", true);
            intent.putExtra("rrule", "FREQ=YEARLY");
            intent.putExtra("endTime", end_time);
            intent.putExtra("title", "AndroidEventManager: " + title);
            startActivity(intent);
        }

    }
}
