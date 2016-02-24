package com.codepath.apps.tweeton.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.codepath.apps.tweeton.Fragments.UserProfileFragment;
import com.codepath.apps.tweeton.Fragments.UserTimelineFragment;
import com.codepath.apps.tweeton.R;

public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the screenName from Activity
        String screenName = getIntent().getStringExtra("screenName");
        //Create user timeline fragment
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            UserProfileFragment fragmentUserProfile = UserProfileFragment.newInstance(screenName);
            ft.replace(R.id.flProfile, fragmentUserProfile);

            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            //Display fragment dynamically

            ft.replace(R.id.flTimeline, fragmentUserTimeline);
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }

        return true;
    }


}
