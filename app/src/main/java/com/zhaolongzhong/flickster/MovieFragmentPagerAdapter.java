package com.zhaolongzhong.flickster;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

public class MovieFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 4;

    public MovieFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MovieFragment.newInstance(MovieType.POPULAR.getName());
            case 1:
                return MovieFragment.newInstance(MovieType.TOP_RATED.getName());
            case 2:
                return MovieFragment.newInstance(MovieType.UPCOMING.getName());
            case 3:
                return MovieFragment.newInstance(MovieType.NOW_PLAYING.getName());
        }

        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }
}
