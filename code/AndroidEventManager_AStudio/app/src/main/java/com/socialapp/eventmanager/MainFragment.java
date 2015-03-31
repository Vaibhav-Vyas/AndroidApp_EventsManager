package com.socialapp.eventmanager;

/**
 * Created by sujith on 28/3/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.socialapp.eventmanager.Models.Event;

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
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        TextView tv = (TextView)rootView.findViewById(R.id.heading);
        tv.setText("Fragment Number : " + getArguments().getInt(ARG_SECTION_NUMBER));

        final LinearLayout eventContainer = (LinearLayout) rootView.findViewById(R.id.eventContainer);

        final List<Event> events = Event.findWithQuery(Event.class, "Select * from Event");

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

        return rootView;
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
}