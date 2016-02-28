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
                ArrayList<Tweet> t = getMentionTweets();
                if (t == null) {
                    Toast.makeText(getActivity(), "Error in saving tweets", Toast.LENGTH_SHORT).show();
                    return;
                }
                addAll(t);
            }
            Toast.makeText(getActivity(), "Offline Mode", Toast.LENGTH_SHORT).show();
            return;
        }
        //Clear out tweets if needed
        pd.show();
        client.getMentionsTimeline(id, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addAll(Tweet.fromJSONArray(response));
                maxID = getIdOfLastTweet();
                stopRefreshing();
                pd.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Utils.showToastForException(getActivity());
                stopRefreshing();
                pd.dismiss();
            }

        });
    }

    public ArrayList<Tweet> getMentionTweets() {
        ArrayList<Tweet> tweets = new ArrayList<>();
        String s = sharedPreferences.getString(currentUserScreenName, "");
        if (s.contentEquals("")) {
            return null;
        }
        return Tweet.getMentionTweets(s);
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
