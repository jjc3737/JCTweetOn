package com.codepath.apps.tweeton.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.tweeton.Adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.tweeton.Adapters.TweetsAdapter;
import com.codepath.apps.tweeton.R;
import com.codepath.apps.tweeton.Utils;
import com.codepath.apps.tweeton.models.Tweet;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JaneChung on 2/20/16.
 */
public class TweetsListFragment extends Fragment {
    @Bind(R.id.rvTimelineTweets)
    RecyclerView rvTweets;

    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private TweetsAdapter adapter;
    private ArrayList<Tweet> tweets;

    LinearLayoutManager layoutManager;
    //inflation logic

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        ButterKnife.bind(this, v);
        setUpViews();


        return v;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        tweets = new ArrayList<>();

        adapter = new TweetsAdapter(tweets);
        super.onCreate(savedInstanceState);
    }

    public void setUpViews() {
        rvTweets.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (Utils.isNetworkAvailable(getActivity()) == false) {
                    Toast.makeText(getActivity(), "No more tweets saved to view", Toast.LENGTH_SHORT).show();
                    return;
                }
                refresh(true);
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkAvailable(getActivity()) == false) {
                    Toast.makeText(getActivity(), "Can't refresh without internet", Toast.LENGTH_SHORT).show();
                    swipeContainer.setRefreshing(false);
                    return;
                } else {
                    adapter.clear();
                    tweets.clear();
                    refresh(false);
                }
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimaryDark,
                R.color.colorPrimary);

    }

    public void addAll(ArrayList<Tweet> tweets) {
        adapter.addAll(tweets);
    }

    public void stopRefreshing() {
        swipeContainer.setRefreshing(false);
    }

    public void clearAll() {
        if (tweets.size() > 0) {
            tweets.clear();
            adapter.notifyDataSetChanged();
        }
    }

    public String getIdOfLastTweet() {
       return tweets.get(tweets.size() - 1).getUid();
    }


    public void refresh(Boolean loading) {
        //to be overridden
    }

}
