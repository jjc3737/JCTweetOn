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
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JaneChung on 2/26/16.
 */
public class SearchedTweetsFragment extends TweetsListFragment {


    private TwitterClient client;
    private String maxID;
    static final String QUERY_ARGS = "query";
    String query;

    public static SearchedTweetsFragment newInstance(String query) {
        SearchedTweetsFragment fragment = new SearchedTweetsFragment();
        Bundle args = new Bundle();
        args.putString(QUERY_ARGS, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = getArguments().getString(QUERY_ARGS, "");
        client = TwitterApplication.getRestClient();
        populateTweetsWithQuery(query, null);

    }


    public void populateTweetsWithQuery(String query, String id) {
        if (Utils.isNetworkAvailable(getActivity()) == false) {
            //Search should not be available without internet
            Toast.makeText(getActivity(), "Search not available: Offline Mode", Toast.LENGTH_SHORT).show();
            return;
        }

        pd.show();

        client.searchTweets(query, id, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray statuses = response.getJSONArray("statuses");
                    addAll(Tweet.fromJSONArrayNotSaved(statuses));
                    JSONObject metadata = response.getJSONObject("search_metadata");
                    maxID = metadata.getString("max_id_str");
                    stopRefreshing();
                    pd.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    pd.dismiss();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Utils.showToastForException(getActivity());
                stopRefreshing();
                pd.dismiss();
            }
        });


    }

    @Override
    public void refresh(Boolean loading) {
        if (loading) {
            populateTweetsWithQuery(query, maxID);
        } else {
            populateTweetsWithQuery(query, null);
        }
    }
}
