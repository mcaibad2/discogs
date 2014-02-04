package com.discogs.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.discogs.fragments.ResultFragment;

import java.util.HashMap;
import java.util.Map;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {
    private Map<Integer, ResultFragment> mPageReferenceMap = new HashMap<Integer, ResultFragment>();

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        ResultFragment searchResultsFragment = new ResultFragment();
        mPageReferenceMap.put(Integer.valueOf(index), searchResultsFragment);
        return searchResultsFragment;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 5;
    }

    public ResultFragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }
}