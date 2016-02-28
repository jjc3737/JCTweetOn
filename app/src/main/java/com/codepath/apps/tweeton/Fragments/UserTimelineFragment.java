package com.codepath.apps.tweeton.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.codepath.apps.tweeton.TwitterApplication;
import com.codepath.apps.tweeton.TwitterClient;
import com.codepath.apps.tweeton.Utils;
import com.codepath.apps.tweeton.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JaneChung on 2/20/16.
 */
public class UserTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    private String maxID;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();
        populateTimeline(null);
    }

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userFragment.setArguments(args);
        return userFragment;
    }

    public void populateTimeline(String id) {
        String screenName = getArguments().getString("screen_name");

        if (Utils.isNetworkAvailable(getActivity()) == false ) {
            if (id == null) {
                ArrayList<Tweet> t = Tweet.getUserTweets(screenName);
                if (t == null) {
                    Toast.makeText(getActivity(), "Error in saving Tweets", Toast.LENGTH_SHORT).show();
                    return;
                }
                addAll(t);
            }
            Toast.makeText(getActivity(), "Offline Mode", Toast.LENGTH_SHORT).show();
            return;
        }
        //Clear out tweets if needed

        client.getUserTimeline(screenName, id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addAll(Tweet.fromJSONArray(response));
                maxID = getIdOfLastTweet();
                stopRefreshing();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Utils.showToastForException(getActivity());
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
