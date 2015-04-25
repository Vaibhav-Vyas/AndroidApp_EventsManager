package com.socialapp.eventmanager;

import android.content.SharedPreferences;
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

    private static final String TAG = "DisplayEventActivity";


    private void showEventOnUI(final Event event, boolean local)
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

        if(local) {
            ImageView iv = (ImageView) findViewById(R.id.eventImage);
            iv.setImageBitmap(BitmapFactory.decodeFile(event.image_url));
        }
        else
        {
            event.save();
            Backend.getImageFromServer(event, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(final String path) {
                            try {
                                URL url = new URL(path);
                                Log.d(TAG, "Image url is: " + path);
                                final Bitmap bmp = BitmapFactory.decodeStream((InputStream)url.getContent());
                                //InputStream is = (InputStream)url.getContent();
                                //final Drawable d = Drawable.createFromStream(is, "src");
                                final ImageView iv = (ImageView) findViewById(R.id.eventImage);

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        //iv.setImageDrawable(d);
                                        iv.setImageBitmap(bmp);
                                        String imagePath = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, event.eventId + "_" + event.name, "EventImage");
                                        event.image_url = imagePath;
                                        Log.d(TAG, "Image saved at: " + imagePath);
                                        event.save();
                                    }
                                });
                            } catch (Exception t) {
                                Log.d(TAG, t.toString());
                                Log.d(TAG, "Error in getting image from url");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayevent);

        String location = getIntent().getStringExtra("location");

        Log.d(TAG, "location : " + location);

        if (location.equals("local"))
        {
            Gson gson = new GsonBuilder().create();
            Event event = gson.fromJson(getIntent().getStringExtra("event"), Event.class);
            showEventOnUI(event, true);
        }
        else if(location.equals("server"))
        {
            final Event event = new Event();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String email = prefs.getString("email", null);

            String eventId = getIntent().getStringExtra("eventId");
            event.owner = getIntent().getStringExtra("eventOwner");
            event.eventId = getIntent().getStringExtra("eventId");
            Backend.getEventFromServer(eventId, email, new Backend.BackendCallback() {
                @Override
                public void onRequestCompleted(final String result) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                System.out.println("Result is : " + result);
                                JSONObject obj = new JSONObject(result);
                                event.name = obj.getString("name");
                                event.description = obj.getString("description");
                                event.location = obj.getString("location");
                                //event.event_type = obj.getString("type");
                                showEventOnUI(event, false);
                            }
                            catch(Throwable t)
                            {
                                Log.d(TAG, "Error converting result to json");
                            }
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
    }



    @Override
    protected void onResume() {
        super.onResume();
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
