package com.socialapp.eventmanager;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Set;

public class ContactSelectorActivity extends ActionBarActivity {

    public static final int CONTACT_QUERY_LOADER = 0;
    public static final String QUERY_KEY = "query";
    private CheckBox phoneContacts;
    private CheckBox fbContacts;
    private boolean bLoadPhoneContacts;
    private boolean bLoadFBContacts;

    String mSearchTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_retriever);

        phoneContacts = (CheckBox)findViewById(R.id.showPhoneContactsChkBox);
        phoneContacts.setSelected(true);
        bLoadPhoneContacts = phoneContacts.isChecked();
        phoneContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Update private variable based on checkbox status.
                if (((CheckBox) v).isChecked()) {
                    bLoadPhoneContacts = true;
                }
                else {
                    bLoadPhoneContacts = false;
                }
                searchContactsInfo("", v, true);
            }
        });

        fbContacts = (CheckBox)findViewById(R.id.showFBContactsChkBox);
        fbContacts.setSelected(true);
        bLoadFBContacts = fbContacts.isChecked();

        fbContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Update private variable based on checkbox status.
                if (((CheckBox) v).isChecked()) {
                    bLoadFBContacts = true;
                } else {
                    bLoadFBContacts = false;
                }
                searchContactsInfo("", v, true);
            }
        });
        View view = getWindow().getDecorView();
        searchContactsInfo("", view, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query.length() >= 3) {
                View view = getWindow().getDecorView();
                searchContactsInfo(query, view, false);
            }
        }
    }

    public void returnContactsInfo(View view){
        Intent i = getIntent();
        String emails = ((EditText)findViewById(R.id.manualEmails)).getText().toString();
        if(emails.length() > 0) {
            String[] emailsArray = emails.split(",");
            for(String email : emailsArray)
            {
                ContactsRetriever.invitedContactsMap.put(email, email);
            }
        }
        i.putExtra("contactsSelected", ContactsRetriever.invitedContactsMap);
        setResult(RESULT_OK, i);
        finish();
    }

    public void searchContactsInfo(String query, View viewTemp, boolean firstRunOnCreate)
        {
        String newFilter = !(query.isEmpty()) ? query : null;

        if ( !firstRunOnCreate) {
            // Don't do anything if the filter is empty
            if (mSearchTerm == null && newFilter == null) {
                return;
            }

            // Don't do anything if the new filter is the same as the current filter
            if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
                return;
            }

            // Updates current filter to new filter
            mSearchTerm = newFilter;
        }
        else
        {
            mSearchTerm = "";
        }

        LinearLayout eventContainer = (LinearLayout) findViewById(R.id.contactsSelector);
        eventContainer.removeAllViews();

        if (bLoadPhoneContacts) {
            // We need to create a bundle containing the query string to send along to the
            // LoaderManager, which will be handling querying the database and returning results.
            Bundle bundle = new Bundle();
            bundle.putString(QUERY_KEY, mSearchTerm);

            ContactsRetriever loaderCallbacks = new ContactsRetriever(this);

            // Start the loader with the new query, and an object that will handle all callbacks.
            getLoaderManager().restartLoader(CONTACT_QUERY_LOADER, bundle, loaderCallbacks);
        }

        if (bLoadFBContacts)
        {
            String fbUserName;

            Set<String> fbFriendsToinvite = SplashFragment.friendsUsingApp.keySet();
            String friends_to_invite_string = "";

            for (String fbIdCurrFriend : fbFriendsToinvite)
            {
                fbUserName = SplashFragment.friendsUsingApp.get(fbIdCurrFriend).toString();

                // Add new view objects
                View phoneContactView = (RelativeLayout) ((Activity) getApplicationContext()).getLayoutInflater().inflate(R.layout.phone_contact_item, null);

                TextView contactName = (TextView) phoneContactView.findViewById(R.id.contactName);
                contactName.setText(fbUserName);

                TextView emailIdText =
                        (TextView) phoneContactView.findViewById(R.id.emailId);
                emailIdText.setText(fbIdCurrFriend);

                QuickContactBadge iv = (QuickContactBadge) phoneContactView.findViewById(R.id.icon);

                CheckBox satView = (CheckBox) phoneContactView.findViewById(R.id.checkBoxSelectContact);
                satView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View parentView = (View) ((CheckBox) view).getParent();
                        TextView textViewEmailId = ((TextView) parentView.findViewById(R.id.emailId));
                        String fbId = textViewEmailId.getText().toString();
                        String fbName = ((TextView) parentView.findViewById(R.id.contactName)).getText().toString();
                        CheckBox checkBoxSelectContact = (CheckBox) parentView.findViewById(R.id.checkBoxSelectContact);
                        if (checkBoxSelectContact.isChecked()) {
                            // if checked, then add to hashMap
                            ContactsRetriever.invitedContactsMap.put(fbId, fbName);
                            Toast.makeText((getApplicationContext()), "Added FB ID: " + fbId + ", Contact name = " + fbName + " to the invitee list", Toast.LENGTH_SHORT).show();

                        } else {
                            // un-checked, then remove from hashMap
                            ContactsRetriever.invitedContactsMap.remove(fbId);
                            Toast.makeText((getApplicationContext()), "Removed FB ID: " + fbId + ", Contact name = " + fbName + " from the invitee list", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                eventContainer.addView(phoneContactView);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact_selector, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_contact).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String var1)
            {
                View view = getWindow().getDecorView();
                searchContactsInfo(var1, view, false);
                return false;
            }
            public boolean onQueryTextChange(String var1)
            {
                if(var1.length() > 3) {
                    View view = getWindow().getDecorView();
                    searchContactsInfo(var1, view, false);

                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_contact) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
