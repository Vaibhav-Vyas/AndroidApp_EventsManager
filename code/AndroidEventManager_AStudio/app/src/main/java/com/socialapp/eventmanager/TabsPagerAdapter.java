package com.socialapp.eventmanager;

/**
 * Created by sujith on 28/3/15.
 */


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "Sujith";

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        Log.d(TAG, "Called getItem with " + index);

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return MainFragment.newInstance(1);
            case 1:
                // Games fragment activity
                return MainFragment.newInstance(2);
            case 2:
                // Movies fragment activity
                return MainFragment.newInstance(3);
            case 3:
                // Movies fragment activity

                return MainFragment.newInstance(4);
            case 4:
                // Movies fragment activity
                return MainFragment.newInstance(5);
            case 5:
                // Movies fragment activity
                return MainFragment.newInstance(6);
            case 6:
                // Movies fragment activity
                return MainFragment.newInstance(7);
            case 7:
                // Movies fragment activity
                return MainFragment.newInstance(8);
            case 8:
                // Movies fragment activity
                return MainFragment.newInstance(9);
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 9;
    }

}
