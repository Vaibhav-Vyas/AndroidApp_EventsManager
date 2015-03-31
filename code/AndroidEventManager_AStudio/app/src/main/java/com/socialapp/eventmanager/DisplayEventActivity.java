package com.socialapp.eventmanager;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.socialapp.eventmanager.Models.Event;

import java.util.Calendar;


public class DisplayEventActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayevent);

        String routineJSON=getIntent().getStringExtra("event");


        Gson gson = new GsonBuilder().create();
        Event event = gson.fromJson(routineJSON, Event.class);




        Calendar cl = Calendar.getInstance();


        TextView tv = (TextView)findViewById(R.id.eventName);
        tv.setText(event.name);

        tv = (TextView)findViewById(R.id.eventLocation);
        tv.setText(event.location);

        tv = (TextView)findViewById(R.id.eventStartTime);
        cl.setTimeInMillis((event.start_time)*1000);
        String date = "" + cl.get(Calendar.DAY_OF_MONTH) + "/" + cl.get(Calendar.MONTH) + "/" + cl.get(Calendar.YEAR);
        String time = "" + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
        tv.setText(date + " , " + time);

        tv = (TextView)findViewById(R.id.eventEndTime);
        cl.setTimeInMillis((event.end_time)*1000);
        date = "" + cl.get(Calendar.DAY_OF_MONTH) + "/" + cl.get(Calendar.MONTH) + "/" + cl.get(Calendar.YEAR);
        time = "" + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
        tv.setText(date + " , " + time);

        ImageView iv= (ImageView)findViewById(R.id.eventImage);
        iv.setImageResource(R.drawable.event_pic);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_event, menu);
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
