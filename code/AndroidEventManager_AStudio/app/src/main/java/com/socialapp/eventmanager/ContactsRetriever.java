package com.socialapp.eventmanager;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.socialapp.eventmanager.Models.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Helper class to handle all the callbacks that occur when interacting with loaders.  Most of the
 * interesting code in this sample app will be in this file.
 */
public class ContactsRetriever implements LoaderManager.LoaderCallbacks<Cursor> {

    Context mContext;

    public static final String QUERY_KEY = "query";

    public static final String TAG = "ContactsRetriever";

    public ContactsRetriever(Context context) {
        mContext = context;
    }

    // create map to emails
    public static HashMap<String, String > invitedContactsMap = new HashMap<String, String>();

    @Override
    public Loader<Cursor> onCreateLoader(int loaderIndex, Bundle args) {
        // Where the Contactables table excels is matching text queries,
        // not just data dumps from Contacts db.  One search term is used to query
        // display name, email address and phone number.  In this case, the query was extracted
        // from an incoming intent in the handleIntent() method, via the
        // intent.getStringExtra() method.
        boolean includeOnlyPhoneContacts = false;

        // BEGIN_INCLUDE(uri_with_query)
        String query = args.getString(QUERY_KEY);
        Uri uri; // = Uri.withAppendedPath(CommonDataKinds.Contactables.CONTENT_FILTER_URI, query);


        // There are two types of searches, one which displays all contacts and
        // one which filters contacts by a search query. If mSearchTerm is set
        // then a search query has been entered and the latter should be used.

        if (query == null ||
            query.length() == 0) {
            // Since there's no search string, use the content URI that searches the entire
            // Contacts table
            uri = Uri.withAppendedPath(CommonDataKinds.Contactables.CONTENT_URI, query);
        } else {
            // Since there's a search string, use the special content Uri that searches the
            // Contacts table. The URI consists of a base Uri and the search string.
            uri =
                    Uri.withAppendedPath(CommonDataKinds.Contactables.CONTENT_FILTER_URI, query);
        }



        // END_INCLUDE(uri_with_query)


        // BEGIN_INCLUDE(cursor_loader)
        // Easy way to limit the query to contacts with phone numbers.
        String selection = "";

        // TODO: include only email contacts
        if (includeOnlyPhoneContacts) {
            selection = CommonDataKinds.Contactables.HAS_PHONE_NUMBER + " = " + 1;
        }

        // Sort results such that rows for the same contact stay together.
        String sortBy = CommonDataKinds.Contactables.DISPLAY_NAME;

        return new CursorLoader(
                mContext,  // Context
                uri,       // URI representing the table/resource to be queried
                null,      // projection - the list of columns to return.  Null means "all"
                selection, // selection - Which rows to return (condition rows must match)
                null,      // selection args - can be provided separately and subbed into selection.
                sortBy);   // string specifying sort order
        // END_INCLUDE(cursor_loader)
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        TextView tv  = (TextView) ((Activity)mContext).findViewById(R.id.sample_output);
        if(tv == null) {
            Log.e(TAG, "TextView is null?!");
        } else if (mContext == null) {
            Log.e(TAG, "Context is null?");
        } else {
            Log.e(TAG, "Nothing is null?!");
        }

        // Reset text in case of a previous query
        //tv.setText(mContext.getText(R.string.intro_message) + "\n\n");

        if (cursor.getCount() == 0) {
            Log.e(TAG, "No contacts found on this device.");
            return;
        }

        // Pulling the relevant value from the cursor requires knowing the column index to pull
        // it from.
        // BEGIN_INCLUDE(get_columns)
        int phoneColumnIndex = cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER);
        int emailColumnIndex = cursor.getColumnIndex(CommonDataKinds.Email.ADDRESS);
        int nameColumnIndex = cursor.getColumnIndex(CommonDataKinds.Contactables.DISPLAY_NAME);
        int lookupColumnIndex = cursor.getColumnIndex(CommonDataKinds.Contactables.LOOKUP_KEY);
        int typeColumnIndex = cursor.getColumnIndex(CommonDataKinds.Contactables.MIMETYPE);
        // END_INCLUDE(get_columns)

        cursor.moveToFirst();
        // Lookup key is the easiest way to verify a row of data is for the same
        // contact as the previous row.
        String lookupKey = "";
        int showMaxEntries = 50;
        final LinearLayout eventContainer = (LinearLayout) ((Activity)mContext).findViewById(R.id.contactsSelector);
        eventContainer.removeAllViews();
        do {
            // BEGIN_INCLUDE(lookup_key)
            String emailId = "";
            String currentLookupKey = cursor.getString(lookupColumnIndex);
            String displayName = "";

            if (!lookupKey.equals(currentLookupKey)) {
                displayName = cursor.getString(nameColumnIndex);
                //tv.append(displayName + "\n");
                lookupKey = currentLookupKey;
            }
            // END_INCLUDE(lookup_key)

            // BEGIN_INCLUDE(retrieve_data)
            // The data type can be determined using the mime type column.
            String mimeType = cursor.getString(typeColumnIndex);
            if (mimeType.equals(CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                //tv.append("\tPhone Number: " + cursor.getString(phoneColumnIndex) + "\n");
            } else if (mimeType.equals(CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                emailId = cursor.getString(emailColumnIndex);
                //tv.append("\tEmail Address: " + emailId + "\n");
            }
            // END_INCLUDE(retrieve_data)

            // Skip this entry if the email id is NOT present.
            if (emailId.length() == 0)
                continue;

            // Look at DDMS to see all the columns returned by a query to Contactables.
            // Behold, the firehose!
            for(String column : cursor.getColumnNames()) {
                Log.d(TAG, column + column + ": " +
                        cursor.getString(cursor.getColumnIndex(column)) + "\n");
            }

            //Event currEvent = events.get(i);
            View phoneContactView = (RelativeLayout)((Activity)mContext).getLayoutInflater().inflate(R.layout.phone_contact_item, null);

            phoneContactView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    /*
                    int index=((ViewGroup)v.getParent()).indexOfChild(v);

                    Event selectedEvent=events.get(index);

                    Gson gson=new Gson();
                    String eventJSON=gson.toJson(selectedEvent,Event.class);

                    Intent newActivity=new Intent(getActivity(),DisplayEventActivity.class);
                    newActivity.putExtra("event", eventJSON);
                    newActivity.putExtra("location", "local");
                    startActivity(newActivity);
                    */
                }
            });

            Random rnd = new Random();
            int transparency = 160;
            int color = Color.argb(transparency, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            View view = (RelativeLayout)  phoneContactView.findViewById(R.id.phoneContactEntry);
            view.setBackgroundColor(color);


            TextView contactName = (TextView) phoneContactView.findViewById(R.id.contactName);
            contactName.setText(displayName);

            TextView emailIdText =
                    (TextView) phoneContactView.findViewById(R.id.emailId);
            emailIdText.setText(emailId);

            QuickContactBadge iv= (QuickContactBadge)phoneContactView.findViewById(R.id.icon);

            CheckBox satView = (CheckBox) phoneContactView.findViewById(R.id.checkBoxSelectContact);
            satView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View parentView = (View)((CheckBox) view).getParent();
                    TextView textViewEmailId = ((TextView) parentView.findViewById(R.id.emailId));
                    String emailId = textViewEmailId.getText().toString();
                    String contactName = ((TextView) parentView.findViewById(R.id.contactName)).getText().toString();
                    CheckBox checkBoxSelectContact = (CheckBox) parentView.findViewById(R.id.checkBoxSelectContact);
                    if (checkBoxSelectContact.isChecked()) {
                        // if checked, then add to hashMap
                        invitedContactsMap.put(emailId, contactName);
                        Toast.makeText((mContext), "Added emailID: " + emailId + ", Contact name = " + contactName + " to the invitee list", Toast.LENGTH_SHORT).show();

                    } else {
                        // un-checked, then remove from hashMap
                        invitedContactsMap.remove(emailId);
                        Toast.makeText((mContext), "Removed emailID: " + emailId + ", Contact name = " + contactName + " from the invitee list", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            /*if(currEvent.image_url != null && !currEvent.image_url.equals(""))
            {
                iv.setImageBitmap(BitmapFactory.decodeFile(currEvent.image_url));
            }
            else
            */ {
                //iv.setImageResource(R.drawable.event_pic);
            }

            if(0 >= showMaxEntries--)
            {
                cursor.moveToLast();
                break;
            }

            eventContainer.addView(phoneContactView);

        } while (cursor.moveToNext());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }
}
