package com.socialapp.eventmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.socialapp.eventmanager.Models.Event;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CreateEventActivity extends FragmentActivity {
    private Button startDate;
    private Button endDate;
    private Button startTime;
    private Button endTime;
    private static final String TAG = "Sujith";

    private static final int RESULT_LOAD_IMG = 1;
    private String imgDecodableString;

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

            case R.id.eventImage:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            break;

            case R.id.invite_friends_button:
                Intent intent = new Intent(this, ContactSelectorActivity.class);
                startActivity(intent);
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


                // Owner
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                event.owner= prefs.getString("email", null);

                if((event.name).matches("")){
                    Toast.makeText(this, "Please insert a name for the event", Toast.LENGTH_SHORT).show();
                }else{

                    event.save();


                    //////////////////////////////////
                    /// Send to Server //////////////
                    //////////////////////////////////

                    Backend.createEvent(event, new Backend.CreateEventCallback() {
                        @Override
                        public void onRequestCompleted(final String result) {

                            //Log.d(TAG, "Login success. User: " + user.toString());

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
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
                    super.onBackPressed();
                }

                break;
        }
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
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
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
}
