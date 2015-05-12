package com.socialapp.eventmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.socialapp.eventmanager.Models.Event;
import com.socialapp.eventmanager.Models.Invitee;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class DisplayEventActivity extends Activity {

    Event event;

    private static final String TAG = "DisplayEventActivity";
    private static final int CONTACT_SELECT_REQUEST = 1;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayevent);

        String location = getIntent().getStringExtra("location");
        String type = getIntent().getStringExtra("type");
        Log.d(TAG, "location : " + location);

        if (location.equals("local")) {
            Gson gson = new GsonBuilder().create();
            event = gson.fromJson(getIntent().getStringExtra("event"), Event.class);
            showEventOnUI(true);
        } else if (location.equals("server")) {
            if(type.equals("1")) {
                event = new Event();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String email = prefs.getString("email", null);
                event.event_id = getIntent().getStringExtra("eventId");

                Backend.getEventFromServer(event.event_id, email, new Backend.BackendCallback() {
                    @Override
                    public void onRequestCompleted(final String result) {
                        try {
                            System.out.println("Result1 is : " + result);
                            JSONObject obj = new JSONObject(result);
                            event.owner = obj.getString("owner");
                            event.name = obj.getString("name");
                            event.description = obj.getString("description");
                            event.location = obj.getString("location");
                            event.image_url = obj.getString("imageUrl");
                            event.start_time = Long.parseLong(obj.getString("startTime"));
                            event.end_time = Long.parseLong(obj.getString("endTime"));
                            event.organization = obj.getString("organization");
                            event.status = "invited";
                            Log.d(TAG, "Image url: " + event.image_url);
                            if (event.image_url != "") {
                                saveImageToGallery(event);
                            }
                            event.save();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    showEventOnUI(false);
                                }
                            });
                        } catch (Throwable t) {
                            Log.d(TAG, "Error converting result to json");
                        }
                    }

                    @Override
                    public void onRequestFailed(final String message) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Unfortunately there is some issue in getting the event from server !", Toast.LENGTH_SHORT).show();
                               // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }else if(type.equals("2")){

                String eventid = getIntent().getStringExtra("eventId");
                String invitee_name = getIntent().getStringExtra("user_who_responded");
                String response = getIntent().getStringExtra("response");
                Invitee current_invitee;
                int i;

                final List<Event> events;
                final List<Invitee> invitees;

                String[] queryargs;
                queryargs = new String[1];
                queryargs[0]=eventid;

                Log.d("Sujith", " Searching for accepted event in the database");

                // Take up the event and show it

                events = Event.find(Event.class, "eventId = ?", queryargs, null, "startTime",null);
                Event currEvent = events.get(0); // Taking only the first event
                event = currEvent;

                invitees = currEvent.getInvitees();
                int found = 0;
                for (i=0;i<invitees.size();i++){
                    current_invitee = invitees.get(i);
                    if(current_invitee.name.equals(invitee_name)){
                        found = 1;
                        Log.d(TAG,"Got the invitee name, response =  " + response);
                        current_invitee.status=response;
                        current_invitee.save();
                    }
                }

                if(found==0){
                    Log.d(TAG, "Invitee not found, name = " + invitee_name);
                }


                showEventOnUI(true);
            }else if(type.equals("3")){
                String eventid = getIntent().getStringExtra("eventId");
                final List<Event> events;
                String[] queryargs;
                queryargs = new String[1];
                queryargs[0]=eventid;

                events = Event.find(Event.class, "eventId = ?", queryargs, null, "startTime",null);
                Event currEvent = events.get(0); // Taking only the first event
                event = currEvent;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String email = prefs.getString("email", null);
                Backend.getEventFromServer(event.event_id, email, new Backend.BackendCallback() {
                    @Override
                    public void onRequestCompleted(final String result) {
                        try {
                            System.out.println("Result3 is : " + result);
                            JSONObject obj = new JSONObject(result);
                            event.owner = obj.getString("owner");
                            event.name = obj.getString("name");
                            event.description = obj.getString("description");
                            event.location = obj.getString("location");
                            event.image_url = obj.getString("imageUrl");
                            event.start_time = Long.parseLong(obj.getString("startTime"));
                            event.end_time = Long.parseLong(obj.getString("endTime"));
                            event.organization = obj.getString("organization");
                            Log.d(TAG, "Image url: " + event.image_url);
                            if (event.image_url != "") {
                                saveImageToGallery(event);
                            }
                            event.save();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    showEventOnUI(false);
                                }
                            });
                        } catch (Throwable t) {
                            Log.d(TAG, "Error converting result to json");
                        }
                    }

                    @Override
                    public void onRequestFailed(final String message) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Unfortunately there is some issue in getting the event from server !", Toast.LENGTH_SHORT).show();
                               // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

        }

    }

    private void showEventOnUI(boolean local)
    {
        Calendar cl = Calendar.getInstance();

        TextView tv = (TextView)findViewById(R.id.eventName);
        tv.setText(event.name);

        tv = (TextView)findViewById(R.id.eventLocation);
        tv.setText(event.location);

        tv = (TextView)findViewById(R.id.eventStartTime);
        cl.setTimeInMillis((event.start_time));
        String date = new SimpleDateFormat("MMM dd").format(cl.getTime());
        String time = new SimpleDateFormat("hh:mm aa").format(cl.getTime());
        tv.setText(date + "    " + time);

        tv = (TextView)findViewById(R.id.eventEndTime);
        cl.setTimeInMillis((event.end_time));
        date = new SimpleDateFormat("MMM dd").format(cl.getTime());
        time = new SimpleDateFormat("hh:mm aa").format(cl.getTime());
        tv.setText(date + "    " + time);

        ImageView iv = (ImageView) findViewById(R.id.eventImage);
        if(event.image_url != null && !event.image_url.equals("")) {
            Log.d(TAG, "Showing image :" + event.image_url);
            iv.setImageBitmap(BitmapFactory.decodeFile(event.image_url));
            Log.d(TAG, "Image showed");
        }

        RelativeLayout detailsLayout = (RelativeLayout)findViewById(R.id.descriptionLayout);
        if(event.description != null && !event.description.equals(""))
        {
            tv = (TextView)findViewById(R.id.eventDetails);
            tv.setText(event.description);
            if(event.description.length() < 150)
            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                float d = this.getResources().getDisplayMetrics().density;
                params.setMargins((int)(52*d), (int)(10*d), 0, (int)(10*d));
                tv.setLayoutParams(params);
            }
        }
        else
        {
            detailsLayout.setVisibility(View.GONE);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String this_user= prefs.getString("email", null);
        if((event.owner).equals(this_user)){
            Log.d(TAG, "Owner = this user");
            LinearLayout lLayout2 = (LinearLayout)findViewById(R.id.bottomLayout2);
            RelativeLayout hostedByLayout = (RelativeLayout)findViewById(R.id.hostedByLayout);

            lLayout2.setVisibility(View.GONE);
            hostedByLayout.setVisibility(View.GONE);

            setStatusCounts();
        }else{
            Log.d(TAG, "Owner = not this user" + event.owner + "," + this_user);
            LinearLayout lLayout1 = (LinearLayout)findViewById(R.id.bottomLayout1);
            RelativeLayout statusLayout = (RelativeLayout)findViewById(R.id.statusLayout);
            tv = (TextView)findViewById(R.id.eventHost);
            tv.setText(event.owner);

            lLayout1.setVisibility(View.GONE);
            statusLayout.setVisibility(View.GONE);
        }
    }

    public void setStatusCounts()
    {
        int accept_count = 0, maybe_count = 0, invite_count = 0, decline_count = 0;
        List<Invitee> invitees = event.getInvitees();
        for(Invitee invitee : invitees)
        {
            if(invitee.status.equals("invited"))
                invite_count += 1;
            else if(invitee.status.equals("undecided"))
                maybe_count += 1;
            else if(invitee.status.equals("accepted"))
                accept_count += 1;
            else if(invitee.status.equals("declined"))
                decline_count += 1;
        }
        ((TextView)findViewById(R.id.acceptCount)).setText("" + accept_count);
        ((TextView)findViewById(R.id.maybeCount)).setText("" + maybe_count);
        ((TextView)findViewById(R.id.invitedCount)).setText("" + invite_count);
        ((TextView)findViewById(R.id.declinedCount)).setText("" + decline_count);
    }

    public void locationClicked(final View v)
    {
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", event.location);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        this.startActivity(intent);
    }

    public void onStatusClicked(final View v)  {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout myRoot = new LinearLayout(this);
        View itemView = inflater.inflate(R.layout.show_invitees, myRoot);
        final LinearLayout inviteeContainer = (LinearLayout) myRoot.findViewById(R.id.inviteeContainer);
        inviteeContainer.removeAllViews();

        List<Invitee> invitees = event.getInvitees();
        String status = "invited";
        switch (v.getId())
        {
            case R.id.accepted:
                status = "accepted";
                break;
            case R.id.maybed:
                status = "undecided";
                break;
            case R.id.declined:
                status = "declined";
                break;
        }
        for (Invitee invitee : invitees) {
            if(invitee.status.equals(status)) {
                View inviteeItem = inflater.inflate(R.layout.invitee_item, null);
                TextView inviteeNameText = (TextView) inviteeItem.findViewById(R.id.inviteeName);
                inviteeNameText.setText(invitee.name);
                TextView inviteeStatus = (TextView) inviteeItem.findViewById(R.id.inviteeStatus);
                inviteeStatus.setText(invitee.status);
                inviteeContainer.addView(inviteeItem);
            }
        }
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(myRoot);
        dialog.setTitle(status);
        dialog.show();
    }

    public void onDeleteClick(final View v) {
        new AlertDialog.Builder(this)
            .setTitle("Delete " + event.name)
            .setMessage("Are you sure you want to delete this event?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Log.d(TAG, "Deleted event " + event.name);
                    deleteEventFromBackend();
                }
            })
            .setNegativeButton(android.R.string.no, null).show();
    }

    public void deleteEventFromBackend(){
        Backend.deleteEvent(event, new Backend.CreateEventCallback() {
            @Override
            public void onRequestCompleted(final String result) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            //JSONObject obj = new JSONObject(result);
                            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Event deleted successfully !", Toast.LENGTH_SHORT).show();
                            event.delete();
                            finish();
                            //if (event.image_url != null) {
                            //  sendEventImageToBackend(event);
                            //}

                        } catch (Throwable t) {
                            Log.d(TAG, "Error converting result to json in delete event");
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
                        Toast.makeText(getApplicationContext(), "Unfortunately there is some issue in deleting event from server !", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }





    public void onEditClick(final View v) {
        Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
        intent.putExtra("editEvent", event.event_id );
        startActivity(intent);
    }


    public void onResponseClick(final View v) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String email = prefs.getString("email", null);
        String response = null;
        switch (v.getId()) {
            case R.id.accept:
                response = "accepted";
                event.status = response;
                break;
            case R.id.decline:
                response = "declined";
                event.status = response;
                break;
            case R.id.maybe:
                response = "maybe";
                event.status = "invited";
                break;
        }

        event.save();
        Backend.respondToInvite(email, event.event_id, response, new Backend.BackendCallback() {
            @Override
            public void onRequestCompleted(final String result) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Your response has been sent to the owner !", Toast.LENGTH_SHORT).show();
                       // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onRequestFailed(final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Unfortunately there is some issue sending the response to owner !", Toast.LENGTH_SHORT).show();
                      //  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    public void add_friends(View view) {
        // Cleanup Selected Contacts state.
        ContactsRetriever.invitedContactsMap.clear();
        Intent intent = new Intent(this, ContactSelectorActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        startActivityForResult(intent, CONTACT_SELECT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == CONTACT_SELECT_REQUEST && resultCode == RESULT_OK) {

            // Get the selected contacts from Intent data.
            // HashMap<String, String> hashMap = (HashMap<String, String>)data.getSerializableExtra("contactsSelected");
            Log.d(TAG, "Successfully returned from ContactSelectorActivity.");

            HashMap<String, String > invitedContactsMap = ContactsRetriever.invitedContactsMap;
            Set<String> friends_to_invite = invitedContactsMap.keySet();
            String friends_to_invite_string = "";

            for (String invitedFriendEmail : friends_to_invite)
            {
                String invitedFriendName = invitedContactsMap.get(invitedFriendEmail).toString();
                Invitee invitee = new Invitee();

                invitee.name =  (null == invitedFriendEmail) ?
                        " " : invitedFriendEmail;
                invitee.event = event;
                invitee.status = "invited";
                Log.d(TAG, "Saving the invitee = " + invitee.name);
                invitee.save();
                friends_to_invite_string += invitedFriendEmail +",";
            }
            friends_to_invite_string = friends_to_invite_string.length() > 0 ?
                    friends_to_invite_string.substring(0,friends_to_invite_string.length()-1) : " ";

            //Toast.makeText(getApplicationContext(), "Invited friends email ID includes: " + friends_to_invite_string, Toast.LENGTH_SHORT).show();

            Backend.InviteFriends(event, friends_to_invite_string, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(final String result) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Your friends have been invited !", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onRequestFailed(final String message) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Unfortunately there is some issue in inviting your friends. Please check their email addresses !", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            // ==========

        } else {
            //Toast.makeText(this, "Invalid result or error code for ContactSelectorActivity",
                   // Toast.LENGTH_LONG).show();
        }
    }

    private void saveImageToGallery(Event event){
        try {
            URL url = new URL(event.image_url);
            Bitmap bmp = BitmapFactory.decodeStream((InputStream)url.getContent());

            String imagePath = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, event.event_id + "_" + event.name, "EventImage");
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(Uri.parse(imagePath), filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            event.image_url = cursor.getString(columnIndex);;
            Log.d(TAG, "Image saved to gallery at: " + imagePath);

        } catch (Exception t) {
            Log.d(TAG, t.toString());
            Log.d(TAG, "Error in getting image from url");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //Intent intent = new Intent(this, MainActivity.class);
       // startActivity(intent);
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
