package com.zhaolongzhong.flickster;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieActivity extends AppCompatActivity {
    private static final String TAG = MovieActivity.class.getSimpleName();

    /*
       Reference
    */
    @BindView(R.id.movie_activity_tab_layout_id) TabLayout tabLayout;
    @BindView(R.id.movie_activity_tab_view_pager_id) ViewPager viewPager;

    private MovieFragmentPagerAdapter movieFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity);
        ButterKnife.bind(this);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.popular));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.top_rated));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.upcoming));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.now_playing));

        movieFragmentPagerAdapter = new MovieFragmentPagerAdapter(getFragmentManager());
        viewPager.setAdapter(movieFragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(onTabSelectedListener);
    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };
}
