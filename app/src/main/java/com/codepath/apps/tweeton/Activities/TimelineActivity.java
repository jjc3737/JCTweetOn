package com.codepath.apps.tweeton.Activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.tweeton.Fragments.ComposeTweetFragment;
import com.codepath.apps.tweeton.Fragments.HomeTimelineFragment;
import com.codepath.apps.tweeton.Fragments.MentionsTimelineFragment;
import com.codepath.apps.tweeton.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.onTweetListener {

    @Bind(R.id.viewpager)
    ViewPager pager;

    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;

    HomeTimelineFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        //Set the viewpager adapter for the pager
        pager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        //Attach the tabstrip to the viewpager
        tabs.setViewPager(pager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.composeFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragment = ComposeTweetFragment.getInstance(null, null);
                fragment.show(getFragmentManager(), "Compose Tweet");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        //do something with item

        return true;
    }

    public void onProfileView(MenuItem mi) {
        //launch profile view
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    @Override
    public void onTweetSubmit() {
        homeFragment.clearAll();
        homeFragment.populateTimeline(null);
    }


    //Return the order of the fragments
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = { "Home", "Mentions" };

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                homeFragment = new HomeTimelineFragment();
                return homeFragment;
            } else if (position == 1){
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            homeFragment = null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }



}
