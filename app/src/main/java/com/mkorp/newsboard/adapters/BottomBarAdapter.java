package com.mkorp.newsboard.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.SparseArray;

public class BottomBarAdapter extends SmartFragmentStatePagerAdapter {

    private final SparseArray<Fragment> fragments = new SparseArray<>();

    public BottomBarAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragment(int index, Fragment fragment){
        fragments.put(index, fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
