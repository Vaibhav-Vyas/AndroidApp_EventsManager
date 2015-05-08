package com.socialapp.eventmanager;

/**
 * Created by sujith on 28/3/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.socialapp.eventmanager.Models.Event;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainFragment extends Fragment {

    public class EventsAdapter extends ArrayAdapter<Event> {
        public EventsAdapter(Context context, List<Event> events) {
            super(context, 0, events);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Event event = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_item, parent, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.eventImage);
            TextView eventTitle = (TextView) convertView.findViewById(R.id.eventName);
            TextView eventDescription = (TextView) convertView.findViewById(R.id.event_description);

            if(event.image_url != null && !event.image_url.equals("")) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(event.image_url));
            }
            else {
                imageView.setImageResource(R.drawable.event_pic);
            }

            eventTitle.setText(event.name);
            eventDescription.setText(event.description);
            return convertView;
        }
    }

    private int fragmentNumber;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "MainFragment";
    private List<Event> events;
    EventsAdapter adapter;

    public MainFragment() { }

    // Returns a new instance of this fragment for the given section number.
    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //RelativeLayout rLayout = (RelativeLayout) rootView.findViewById (R.id.main_fragment_relative_layout);

        // Background image
        //Resources res = getResources(); //resource handle
        //Drawable drawable = res.getDrawable(R.drawable.background_main); //new Image that was added to the res folder
        //rLayout.setBackground(drawable);

        fragmentNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        events = new ArrayList<Event>();

        ///////////////////////////Events List//////////////////////////////

        ListView eventContainer = (ListView) rootView.findViewById(R.id.event_list);
        adapter = new EventsAdapter(getActivity(), events);
        eventContainer.setAdapter(adapter);
        eventContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event selectedEvent = events.get(position);
                String eventJSON = (new Gson()).toJson(selectedEvent, Event.class);
                Intent newActivity = new Intent(getActivity(), DisplayEventActivity.class);
                newActivity.putExtra("event", eventJSON);
                newActivity.putExtra("location", "local");
                startActivity(newActivity);
            }
        });
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        int status = ((MainActivity)getActivity()).status;
        updateEventsList(status);
    }

    public void updateEventsList(int status){
        String owner= PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString("email", null);
        ArrayList<String> queryargs = new ArrayList<String>();

        switch (fragmentNumber)
        {
            case 0:
                queryargs.add(getTimeAfterDays(0));
                queryargs.add(getTimeAfterDays(1));
                break;
            case 1:
                queryargs.add(getTimeAfterDays(1));
                queryargs.add(getTimeAfterDays(7));
                break;
            case 2:
                queryargs.add(getTimeAfterDays(7));
                queryargs.add(getTimeAfterDays(30));
                break;
        }
        switch (status)
        {
            case 0:
                queryargs.add("owner");
                break;
            case 1:
                queryargs.add("accepted");
                break;
            case 2:
                queryargs.add("invited");
                break;
            case 3:
                queryargs.add("declined");
                break;
        }

        List<Event> updatedEvents = null;
        if(fragmentNumber <= 2 && status <= 3)
        {
            updatedEvents = Event.find(Event.class, "startTime BETWEEN ? AND ? AND status = ?", queryargs.toArray(new String[queryargs.size()]), null, "startTime",null);
        }
        else if(fragmentNumber > 2 && status <= 3)
        {
            updatedEvents = Event.find(Event.class, "status = ?", queryargs.toArray(new String[queryargs.size()]), null, "startTime",null);
        }
        else if(fragmentNumber <= 2 && status > 3)
        {
            updatedEvents = Event.find(Event.class, "startTime BETWEEN ? AND ?", queryargs.toArray(new String[queryargs.size()]), null, "startTime",null);
        }
        else if(fragmentNumber > 2 && status > 3)
        {
            updatedEvents = Event.listAll(Event.class);
        }
        events.clear();
        events.addAll(updatedEvents);
        adapter.notifyDataSetChanged();
    }

    public static String getTimeAfterDays(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        return String.valueOf((cal.getTimeInMillis()));
    }
}