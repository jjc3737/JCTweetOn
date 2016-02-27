package com.codepath.apps.tweeton.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.codepath.apps.tweeton.Fragments.SearchedTweetsFragment;
import com.codepath.apps.tweeton.R;

public class SearchActivity extends AppCompatActivity {

    static final String QUERY_EXTRA = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            String query = getIntent().getStringExtra(QUERY_EXTRA);
            SearchedTweetsFragment fragemnt = SearchedTweetsFragment.newInstance(query);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flSearchedTweets, fragemnt);
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
