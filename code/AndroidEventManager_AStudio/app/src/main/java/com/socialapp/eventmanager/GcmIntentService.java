package com.socialapp.eventmanager;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.socialapp.eventmanager.Models.Event;

import java.util.List;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    private static final String TAG = "GCMINTENTSERVICE";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(extras);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                /*for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }*/
                //Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle msg) {
        String type = msg.getString("type");
        Log.d(TAG, "type is :" + type);
        //Log.d(TAG, msg.toString());
        switch (type)
        {
            case "1":
                invitedToEvent(msg,type);
                break;
            case "2":
                responseFromInvitee(msg,type);
                break;
            case "3": // edit
                updatedEvent(msg,type);
                break;
            case "4":
                deletedEvent(msg,type);
                break;
        }

    }


    private void updatedEvent(Bundle msg,String type)
    {
        String invitedBy = msg.getString("invitedBy");
        String eventName = msg.getString("eventName");
        String eventId = msg.getString("eventId");


        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent displayActivityIntent = new Intent(this, DisplayEventActivity.class);
        displayActivityIntent.putExtra("eventId", eventId);
        displayActivityIntent.putExtra("location", "server");
        displayActivityIntent.putExtra("invitedBy", invitedBy);
        displayActivityIntent.putExtra("type",type);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, displayActivityIntent , PendingIntent.FLAG_CANCEL_CURRENT);




        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.plus)   //TODO: change this icon
                        .setContentTitle(invitedBy + " updated event " + eventName)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(eventId))
                        .setContentText(eventId);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        Log.d(TAG, "Notification displayed :" + invitedBy + " updated event " + eventName);

    }

    private void deletedEvent(Bundle msg,String type)
    {
        String invitedBy = msg.getString("invitedBy");
        String eventName = msg.getString("eventName");
        String eventId = msg.getString("eventId");


        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent displayActivityIntent = new Intent(this, MainActivity.class);
        //displayActivityIntent.putExtra("eventId", eventId);
        //displayActivityIntent.putExtra("location", "server");
        //displayActivityIntent.putExtra("type",type);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, displayActivityIntent , PendingIntent.FLAG_CANCEL_CURRENT);

        String[] queryargs;
        queryargs = new String[1];
        queryargs[0]=eventId;
        final List<Event> events;

        events = Event.find(Event.class, "eventId = ?", queryargs, null, "startTime", null);
        Event currEvent = events.get(0); // Taking only the first event

        currEvent.delete();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.plus)   //TODO: change this icon
                        .setContentTitle(invitedBy + " invited you to " + eventName)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(eventId))
                        .setContentText(eventId);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        Log.d(TAG, "Notification displayed :" + invitedBy + " deleted event " + eventName);

    }










    private void responseFromInvitee(Bundle msg, String type)
    {

        Log.d(TAG, "User accepted your invitation");
        String user = msg.getString("email");
        String response = msg.getString("response");
        String eventId = msg.getString("eventId");


        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent displayActivityIntent = new Intent(this, DisplayEventActivity.class);
        displayActivityIntent.putExtra("eventId", eventId);
        displayActivityIntent.putExtra("location", "server");
        displayActivityIntent.putExtra("user_who_responded", user);
        displayActivityIntent.putExtra("type",type);
        displayActivityIntent.putExtra("response",response);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, displayActivityIntent , PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.plus)   //TODO: change this icon
                        .setContentTitle(user + " " + response + " your event")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(eventId))
                        .setContentText(eventId);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());



    }


    private void invitedToEvent(Bundle msg,String type)
    {
        String invitedBy = msg.getString("invitedBy");
        String eventName = msg.getString("eventName");
        String eventId = msg.getString("eventId");


        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent displayActivityIntent = new Intent(this, DisplayEventActivity.class);
        displayActivityIntent.putExtra("eventId", eventId);
        displayActivityIntent.putExtra("location", "server");
        displayActivityIntent.putExtra("invitedBy", invitedBy);
        displayActivityIntent.putExtra("type",type);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, displayActivityIntent , PendingIntent.FLAG_CANCEL_CURRENT);




        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.plus)   //TODO: change this icon
                        .setContentTitle(invitedBy + " invited you to " + eventName)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(eventId))
                        .setContentText(eventId);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        Log.d(TAG, "Notification displayed :" + invitedBy + " invited you to " + eventName);

    }
}