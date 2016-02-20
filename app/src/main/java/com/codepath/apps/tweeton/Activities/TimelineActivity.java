package com.codepath.apps.tweeton.Activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.codepath.apps.tweeton.Adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.tweeton.Adapters.TweetsAdapter;
import com.codepath.apps.tweeton.Fragments.ComposeTweetFragment;
import com.codepath.apps.tweeton.R;
import com.codepath.apps.tweeton.TwitterApplication;
import com.codepath.apps.tweeton.TwitterClient;
import com.codepath.apps.tweeton.Utils;
import com.codepath.apps.tweeton.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.onTweetListener {

    private TwitterClient client;
    private TweetsAdapter adapter;
    private ArrayList<Tweet> tweets;
    private String maxID;

    LinearLayoutManager layoutManager;

    @Bind(R.id.rvTimelineTweets)
    RecyclerView rvTweets;

    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        setUpViews();

        client = TwitterApplication.getRestClient(); //singleton client
        populateTimeline(null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.composeFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragment = ComposeTweetFragment.getInstance(null, null);
                fragment.show(getFragmentManager(), "Compose Tweet");
            }
        });

    }

    public void setUpViews() {
        tweets = new ArrayList<>();

        adapter = new TweetsAdapter(tweets);
        rvTweets.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (Utils.isNetworkAvailable(TimelineActivity.this) == false) {
                    Toast.makeText(TimelineActivity.this, "No more tweets saved to view", Toast.LENGTH_SHORT).show();
                    return;
                }
                populateTimeline(maxID);
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkAvailable(TimelineActivity.this) == false) {
                    Toast.makeText(TimelineActivity.this, "Can't refresh without internet", Toast.LENGTH_SHORT).show();
                    swipeContainer.setRefreshing(false);
                    return;
                } else {
                    adapter.clear();
                    tweets.clear();
                    populateTimeline(null);
                }
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimaryDark,
                R.color.colorPrimary);

    }

    //API request to get timeline json

    // fill recylce view by creating tweet object
    private void populateTimeline(String id) {

        if (Utils.isNetworkAvailable(this) == false ) {
            if (id == null) {
                tweets.addAll(Tweet.getAllTweets());
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
            Toast.makeText(this, "Offline Mode", Toast.LENGTH_SHORT).show();
            return;
        }
        //Clear out tweets if needed
        client.getTimeline(id, new JsonHttpResponseHandler() {
            //success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                new Delete().from(Tweet.class).execute();
                tweets.addAll(Tweet.fromJSONArray(response));
                if (tweets.size() > 0) {
                    maxID = tweets.get(tweets.size() - 1).getUid();
                }
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                swipeContainer.setRefreshing(false);
            }

        });
    }

    @Override
    public void onTweetSubmit() {
        if (tweets.size() > 0) {
            tweets.clear();
            adapter.notifyDataSetChanged();
        }
        populateTimeline(null);
    }
}
