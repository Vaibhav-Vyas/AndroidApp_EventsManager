package com.socialapp.eventmanager;

/**
 * Created by sujith on 28/3/15.
 */


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "TabsPagerAdapter";
    private FragmentManager fragmentManager;
    private Map<Integer, String> fragmentTags;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentTags = new HashMap<Integer, String>();
        fragmentManager = fm;
    }

    @Override
    public Fragment getItem(int index) {
        Log.d(TAG, "Called getItem with " + index);
        switch (index) {
            case 0:   // Today's events
                return MainFragment.newInstance(0);
            case 1:   // This week's events
                return MainFragment.newInstance(1);
            case 2:   // This month's events
                return MainFragment.newInstance(2);
            case 3:   // Later's events
                return MainFragment.newInstance(3);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof MainFragment)
        {
            Fragment f = (Fragment)obj;
            fragmentTags.put(position, f.getTag());
        }
        return obj;
    }

    public MainFragment getFragment(int position) {
        String tag = fragmentTags.get(position);
        if(tag == null)
            return null;
        return (MainFragment)fragmentManager.findFragmentByTag(tag);
    }

}
