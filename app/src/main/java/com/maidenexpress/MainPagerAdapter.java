package com.maidenexpress;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_PAGES = 3;

    public MainPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1: return new PaymentFragment();
            case 2: return new AccountFragment();
            default: return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
