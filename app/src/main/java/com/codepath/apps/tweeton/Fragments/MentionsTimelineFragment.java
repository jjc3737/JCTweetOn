package com.codepath.apps.tweeton.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.tweeton.TwitterApplication;
import com.codepath.apps.tweeton.TwitterClient;
import com.codepath.apps.tweeton.Utils;
import com.codepath.apps.tweeton.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by JaneChung on 2/20/16.
 */
public class MentionsTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    private String maxID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();
        populateTimeline(null);
    }

    public void populateTimeline(String id) {

        if (Utils.isNetworkAvailable(getActivity()) == false ) {
            if (id == null) {
                //Todo: Can't get all tweets here, must get only metnions
                addAll(Tweet.getAllTweets());
            }
            Toast.makeText(getActivity(), "Offline Mode", Toast.LENGTH_SHORT).show();
            return;
        }
        //Clear out tweets if needed
        client.getMentionsTimeline(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addAll(Tweet.fromJSONArray(response));
                maxID = getIdOfLastTweet();
                stopRefreshing();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                stopRefreshing();
            }

        });
    }

    @Override
    public void refresh(Boolean loading) {
        if (loading) {
            populateTimeline(maxID);
        } else {
            populateTimeline(null);
        }
    }
}
