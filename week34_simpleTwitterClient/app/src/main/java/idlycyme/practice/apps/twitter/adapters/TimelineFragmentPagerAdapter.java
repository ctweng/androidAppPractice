package idlycyme.practice.apps.twitter.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import idlycyme.practice.apps.twitter.templates.TimelineFragment;

/**
 * Created by cyme on 12/17/15.
 */
public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {
    private int PAGE_COUNT;
    private String tabTitles[];
    private String types[];
    private SparseArray<android.support.v4.app.Fragment> fragments;

    public TimelineFragmentPagerAdapter(FragmentManager fm, String[] titles, String[] types) {
        super(fm);
        this.tabTitles = titles;
        this.types = types;
        fragments = new SparseArray<>();
        PAGE_COUNT = titles.length;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        android.support.v4.app.Fragment fragment = null;
        Log.i("postion ------- ", String.valueOf(position));
        if (fragments.size() > position) {
            fragment = fragments.get(position);
        }

        if (fragment == null) {
            fragment = TimelineFragment.newInstance(position, types[position]);
            fragments.put(position, fragment);
            Log.i("----fragments", fragments.toString());
        }
        Log.i("aaaa", fragment.toString());
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
