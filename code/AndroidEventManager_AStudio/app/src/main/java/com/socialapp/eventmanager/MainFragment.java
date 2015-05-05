package com.socialapp.eventmanager;

/**
 * Created by sujith on 28/3/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.socialapp.eventmanager.Models.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private LayoutInflater layout_inflater;
    private int section_number;



    private static final String ARG_SECTION_NUMBER = "section_number";
    View rootView;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout_inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_main, container, false);


        RelativeLayout rLayout = (RelativeLayout) rootView.findViewById (R.id.main_fragment_relative_layout);
        Resources res = getResources(); //resource handle
        Drawable drawable = res.getDrawable(R.drawable.background_main); //new Image that was added to the res folder

        rLayout.setBackground(drawable);


        TextView tv = (TextView)rootView.findViewById(R.id.heading);
        tv.setText("Fragment Number : " + getArguments().getInt(ARG_SECTION_NUMBER));
        section_number = getArguments().getInt(ARG_SECTION_NUMBER);

        /*
        final LinearLayout eventContainer = (LinearLayout) rootView.findViewById(R.id.eventContainer);

        //final List<Event> events = Event.findWithQuery(Event.class, "Select * from Event ORDER BY startTime");
        //final List<Event> events = Event.find(Event.class, "startTime > 0", null, null, "startTime",null);

        ////////////////////////////////////////////////////////////////////////////////////
        final List<Event> events;

        Calendar calendar1 = Calendar.getInstance();
        long current_time = calendar1.getTimeInMillis()/1000;
        String[] queryargs;

        //Log.d("Sujith", "current time string = " + queryargs[0]);


        switch (section_number){
            case 1: // Today
                queryargs = new String[2];
                queryargs[0]= getTimeAfterDays(0);
                queryargs[1]= getTimeAfterDays(1);
                events = Event.find(Event.class, "startTime BETWEEN ? AND ?", queryargs, null, "startTime",null);
                break;
            case 2: // This week
                queryargs = new String[2];
                queryargs[0]=getTimeAfterDays(0);
                queryargs[1]=getTimeAfterDays(7);
                events = Event.find(Event.class, "startTime BETWEEN ? AND ?", queryargs, null, "startTime",null);
                break;
            case 3: // This month
                queryargs = new String[2];
                queryargs[0]=getTimeAfterDays(0);
                queryargs[1]=getTimeAfterDays(30);
                events = Event.find(Event.class, "startTime BETWEEN ? AND ?", queryargs, null, "startTime",null);
                break;
            case 4: // UW
                events = Event.find(Event.class, "organization = UW", null, null, "startTime",null);
                break;
            default: // All events
                events = Event.find(Event.class, "startTime > 0", null, null, "startTime",null);
                break;
        }

        ///////////////////////////////////////////////////////////////////////////////////////




        for (int i = 0; i < events.size(); i++) {
            Event currEvent = events.get(i);
            View eventItem = inflater.inflate(R.layout.event_item, null);

            eventItem.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int index=((ViewGroup)v.getParent()).indexOfChild(v);
                    Event selectedEvent=events.get(index);

                    Gson gson=new Gson();
                    String eventJSON=gson.toJson(selectedEvent,Event.class);

                    Intent newActivity=new Intent(getActivity(),DisplayEventActivity.class);
                    newActivity.putExtra("event",eventJSON);
                    startActivity(newActivity);

                }
            });

            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            View view = eventItem.findViewById(R.id.tempRel);
            view.setBackgroundColor(color);


            TextView eventNameText = (TextView) eventItem.findViewById(R.id.eventName);
            eventNameText.setText(currEvent.name);

            TextView eventLocationText =
                    (TextView) eventItem.findViewById(R.id.textview1);
            eventLocationText.setText(currEvent.location);

            ImageView iv= (ImageView)eventItem.findViewById(R.id.eventImage);
            iv.setImageResource(R.drawable.event_pic);

            eventContainer.addView(eventItem);
        }
        */
        return rootView;
    }


    @Override
    public void onResume(){
        super.onResume();

        final LinearLayout eventContainer = (LinearLayout) rootView.findViewById(R.id.eventContainer);
        eventContainer.removeAllViews();

        //final List<Event> events = Event.findWithQuery(Event.class, "Select * from Event ORDER BY startTime");
        //final List<Event> events = Event.find(Event.class, "startTime > 0", null, null, "startTime",null);

        ////////////////////////////////////////////////////////////////////////////////////
        final List<Event> events;

        Calendar calendar1 = Calendar.getInstance();
        long current_time = calendar1.getTimeInMillis()/1000;
        String[] queryargs;

        //Log.d("Sujith", "current time string = " + queryargs[0]);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String owner= prefs.getString("email", null);

        switch (section_number){
            case 1: // Today
                queryargs = new String[3];
                queryargs[0]= getTimeAfterDays(0);
                queryargs[1]= getTimeAfterDays(1);
                queryargs[2] = owner;
                Log.d("Sujith", " Section 1: Finding events between = " + queryargs[0] + " and " + queryargs[1]);
                events = Event.find(Event.class, "startTime BETWEEN ? AND ? and owner = ?", queryargs, null, "startTime",null);
                break;
            case 2: // This week
                queryargs = new String[3];
                queryargs[0]=getTimeAfterDays(1);
                queryargs[1]=getTimeAfterDays(7);
                queryargs[2] = owner;
                Log.d("Sujith", " Section 2: Finding events between = " + queryargs[0] + " and " + queryargs[1]);
                events = Event.find(Event.class, "startTime BETWEEN ? AND ? and owner = ?", queryargs, null, "startTime",null);
                break;
            case 3: // This month
                queryargs = new String[3];
                queryargs[0]=getTimeAfterDays(7);
                queryargs[1]=getTimeAfterDays(30);
                queryargs[2] = owner;
                Log.d("Sujith", " Section 3: Finding events between = " + queryargs[0] + " and " + queryargs[1]);
                events = Event.find(Event.class, "startTime BETWEEN ? AND ? and owner = ?", queryargs, null, "startTime",null);
                break;
            case 4: // Created Events
                queryargs = new String[2];
                queryargs[0]=getTimeAfterDays(0);
                queryargs[1] = "owner";
                events = Event.find(Event.class, "startTime > ? and status = ?", queryargs, null, "startTime",null);
                break;

            case 5: // Accepted Events
                queryargs = new String[2];
                queryargs[0]=getTimeAfterDays(0);
                queryargs[1] = "accepted";
                events = Event.find(Event.class, "startTime > ? and status = ?", queryargs, null, "startTime",null);
                break;
            case 6: // Invited Events
                queryargs = new String[2];
                queryargs[0]=getTimeAfterDays(0);
                queryargs[1] = "invited";
                events = Event.find(Event.class, "startTime > ? and status = ?", queryargs, null, "startTime",null);
                break;
            case 7: // Declined Events
                queryargs = new String[2];
                queryargs[0]=getTimeAfterDays(0);
                queryargs[1] = "declined";
                events = Event.find(Event.class, "startTime > ? and status = ?", queryargs, null, "startTime",null);
                break;
            default: // All events
                //events = Event.find(Event.class, "startTime > 0", null, null, "startTime",null);
                events = Event.listAll(Event.class);
                break;
        }

        ///////////////////////////////////////////////////////////////////////////////////////




        for (int i = 0; i < events.size(); i++) {
            Event currEvent = events.get(i);
            View eventItem = layout_inflater.inflate(R.layout.event_item, null);

            eventItem.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int index=((ViewGroup)v.getParent()).indexOfChild(v);
                    Event selectedEvent=events.get(index);

                    Gson gson=new Gson();
                    String eventJSON=gson.toJson(selectedEvent,Event.class);

                    Intent newActivity=new Intent(getActivity(),DisplayEventActivity.class);
                    newActivity.putExtra("event", eventJSON);
                    newActivity.putExtra("location", "local");
                    startActivity(newActivity);

                }
            });

            Random rnd = new Random();
            int transparency = 160;
            int color = Color.argb(transparency, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            View view = eventItem.findViewById(R.id.tempRel);
            view.setBackgroundColor(color);


            TextView eventNameText = (TextView) eventItem.findViewById(R.id.eventName);
            eventNameText.setText(currEvent.name);

            TextView eventLocationText =
                    (TextView) eventItem.findViewById(R.id.textview1);
            eventLocationText.setText(currEvent.location);

            ImageView iv= (ImageView)eventItem.findViewById(R.id.eventImage);

            if(currEvent.image_url != null && !currEvent.image_url.equals(""))
            {
                iv.setImageBitmap(BitmapFactory.decodeFile(currEvent.image_url));
            }
            else
            {
                iv.setImageResource(R.drawable.event_pic);
            }


            eventContainer.addView(eventItem);
        }

    }


    @Override
    public void onStart() {
        Log.d("Sujith", "Called onStart on the MainFragment");
        super.onStart();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }


    public static String getTimeAfterDays(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        return String.valueOf((cal.getTimeInMillis()));
    }



}