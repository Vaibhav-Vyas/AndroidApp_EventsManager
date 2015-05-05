package com.socialapp.eventmanager;

import android.app.SearchManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ContactSelectorActivity extends ActionBarActivity {

    public static final int CONTACT_QUERY_LOADER = 0;
    public static final String QUERY_KEY = "query";
    EditText mEdit;
    String mSearchTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_retriever);
        mEdit = (EditText) findViewById(R.id.searchBoxInput);

        mEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // adding this check to reduce the number of queries.
                if (s.length() >= 3 ) {
                    View view = getWindow().getDecorView();
                    searchContactsInfo(view, false);
                 }

}
});
        View view = getWindow().getDecorView();
        searchContactsInfo(view, true);
    }

    public void searchContactsInfo(View view, boolean firstRunOnCreate)
        {
        String query =  mEdit.getText().toString();   // intent.getStringExtra(SearchManager.QUERY);

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

        // We need to create a bundle containing the query string to send along to the
        // LoaderManager, which will be handling querying the database and returning results.
        Bundle bundle = new Bundle();
        bundle.putString(QUERY_KEY, mSearchTerm);

        ContactsRetriever loaderCallbacks = new ContactsRetriever(this);

        // Start the loader with the new query, and an object that will handle all callbacks.
        getLoaderManager().restartLoader(CONTACT_QUERY_LOADER, bundle, loaderCallbacks);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
