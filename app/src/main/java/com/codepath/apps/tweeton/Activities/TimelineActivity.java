package com.codepath.apps.tweeton.Activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.codepath.apps.tweeton.Adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.tweeton.Adapters.TweetsAdapter;
import com.codepath.apps.tweeton.Fragments.ComposeTweetFragment;
import com.codepath.apps.tweeton.R;
import com.codepath.apps.tweeton.TwitterApplication;
import com.codepath.apps.tweeton.TwitterClient;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        setUpViews();

        client = TwitterApplication.getRestClient(); //singleton client
        populateTimeline(null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.composeFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragmet = ComposeTweetFragment.getInstance();
                fragmet.show(getFragmentManager(), "Compost Tweet");
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
                populateTimeline(maxID);
            }
        });

    }

    //API request to get timeline json

    // fill recylce view by creating tweet object
    private void populateTimeline(String id) {
        //Clear out tweets if needed
        client.getTimeline(id, new JsonHttpResponseHandler() {
            //success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                tweets.addAll(Tweet.fromJSONArray(response));
                if (tweets.size() > 0) {
                    maxID = tweets.get(tweets.size() - 1).getUid();
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
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
