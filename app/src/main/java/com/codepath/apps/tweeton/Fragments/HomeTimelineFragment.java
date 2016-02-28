package com.codepath.apps.tweeton.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.codepath.apps.tweeton.TwitterApplication;
import com.codepath.apps.tweeton.TwitterClient;
import com.codepath.apps.tweeton.Utils;
import com.codepath.apps.tweeton.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JaneChung on 2/20/16.
 */
public class HomeTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private String maxID;

    public static SharedPreferences sharedPreferences;
    public static final String prefName = "MY_SHARED_PREFS";
    private static final String currentUserScreenName = "CURRENT_USER_SCREEN_NAME";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
        getCurrentUserScreenName();
        populateTimeline(null);
    }

    public void populateTimeline(String id) {

        if (Utils.isNetworkAvailable(getActivity()) == false ) {
            if (id == null) {
                addAll(Tweet.getAllTweets());
            }
            Toast.makeText(getActivity(), "Offline Mode", Toast.LENGTH_SHORT).show();
            return;
        }
        pd.show();
        //Clear out tweets if needed
        client.getTimeline(id, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                new Delete().from(Tweet.class).execute();
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

    public void getCurrentUserScreenName() {

        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String s = response.getString("screen_name");
                    SharedPreferences.Editor et = sharedPreferences.edit();
                    et.putString(currentUserScreenName, s);
                    et.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
