package com.socialapp.eventmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.socialapp.eventmanager.Models.Event;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.List;


public class DisplayEventActivity extends ActionBarActivity {

    Event event;


    private static final String TAG = "DisplayEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayevent);

        String location = getIntent().getStringExtra("location");

        Log.d(TAG, "location : " + location);

        if (location.equals("local"))
        {
            Gson gson = new GsonBuilder().create();
            event = gson.fromJson(getIntent().getStringExtra("event"), Event.class);
            showEventOnUI(true);
        }
        else if(location.equals("server"))
        {
            event = new Event();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String email = prefs.getString("email", null);



            event.eventId = getIntent().getStringExtra("eventId");

            Backend.getEventFromServer(event.eventId, email, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(final String result) {
                    try {
                        System.out.println("Result is : " + result);
                        JSONObject obj = new JSONObject(result);
                        event.owner = obj.getString("owner");
                        event.name = obj.getString("name");
                        event.description = obj.getString("description");
                        event.location = obj.getString("location");
                        event.image_url=obj.getString("imageUrl");
                        event.start_time= Long.parseLong(obj.getString("startTime"));
                        event.end_time= Long.parseLong(obj.getString("endTime"));
                        event.organization=obj.getString("organization");
                        Log.d(TAG, "Image url: " + event.image_url);
                        if(event.image_url!=""){
                            saveImageToGallery(event);
                        }
                        event.save();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                    showEventOnUI(false);
                                }
                        });
                    }  catch(Throwable t)
                    {
                        Log.d(TAG, "Error converting result to json");
                    }
                }

                @Override
                public void onRequestFailed(final String message) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

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
        cl.setTimeInMillis((event.start_time)*1000);
        String date = "" + cl.get(Calendar.DAY_OF_MONTH) + "/" + cl.get(Calendar.MONTH) + "/" + cl.get(Calendar.YEAR);
        String time = "" + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
        tv.setText(date + " , " + time);

        tv = (TextView)findViewById(R.id.eventEndTime);
        cl.setTimeInMillis((event.end_time)*1000);
        date = "" + cl.get(Calendar.DAY_OF_MONTH) + "/" + cl.get(Calendar.MONTH) + "/" + cl.get(Calendar.YEAR);
        time = "" + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
        tv.setText(date + " , " + time);

        ImageView iv = (ImageView) findViewById(R.id.eventImage);
        if(event.image_url != "") {
            Log.d(TAG, "Showing image :" + event.image_url);
            iv.setImageBitmap(BitmapFactory.decodeFile(event.image_url));
            Log.d(TAG, "Image showed");
        }else
        {
            iv.setImageResource(R.drawable.event_pic);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String this_user= prefs.getString("email", null);
        if((event.owner).equals(this_user)){
            Log.d(TAG, "Owner = this user");
            Button add_friends_button = (Button)findViewById(R.id.addFriendsButton);
            add_friends_button.setVisibility(View.VISIBLE);
        }else{
            Log.d(TAG, "Owner = not this user" + event.owner + "," + this_user);
        }


    }



    public void add_friends(View view){

        //Intent intent = new Intent(this, ContactSelectorActivity.class);
        //startActivity(intent);


        String friends_to_invite="avinaash@cs.wisc.edu";

        Backend.InviteFriends(event, friends_to_invite, new Backend.BackendCallback() {
            @Override
            public void onRequestCompleted(final String result) {
                runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
                    });

            }

            @Override
            public void onRequestFailed(final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




    }



    private void saveImageToGallery(Event event){
        try {
            URL url = new URL(event.image_url);
            Bitmap bmp = BitmapFactory.decodeStream((InputStream)url.getContent());

            String imagePath = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, event.eventId + "_" + event.name, "EventImage");
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
