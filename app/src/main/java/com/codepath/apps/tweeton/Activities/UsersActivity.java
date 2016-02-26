package com.codepath.apps.tweeton.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.tweeton.Adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.tweeton.Adapters.UsersAdapter;
import com.codepath.apps.tweeton.R;
import com.codepath.apps.tweeton.TwitterApplication;
import com.codepath.apps.tweeton.TwitterClient;
import com.codepath.apps.tweeton.Utils;
import com.codepath.apps.tweeton.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UsersActivity extends AppCompatActivity {

    @Bind(R.id.rvUsers)
    RecyclerView rvUsers;

    @Bind(R.id.userSwipeContainer)
    SwipeRefreshLayout swipeContainer;

    ArrayList<User> users;
    UsersAdapter adapter;
    User currentUser;
    Boolean showFollowing;
    String currentCursor;

    LinearLayoutManager layoutManager;
    ProgressDialog pd;

    private TwitterClient client;

    static final String USER_SCREENAME_EXTRA = "userScreenName";
    static final String IS_FOLLOWING_EXTRA = "isFollowing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String userScreenName = getIntent().getStringExtra(USER_SCREENAME_EXTRA);
        showFollowing = getIntent().getBooleanExtra(IS_FOLLOWING_EXTRA, true);
        currentUser = User.getUserFromScreenName(userScreenName);
        client = TwitterApplication.getRestClient();

        users = new ArrayList<>();
        adapter = new UsersAdapter(users);

        setUpViews();
        setUpProgressDialogForLoading();
        populateUsers(null);

    }

    public void setUpViews() {
        rvUsers.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        rvUsers.setLayoutManager(layoutManager);
        rvUsers.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (Utils.isNetworkAvailable(UsersActivity.this) == false) {
                    Toast.makeText(UsersActivity.this, "No more tweets saved to view", Toast.LENGTH_SHORT).show();
                    return;
                }
                refresh(true);
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkAvailable(UsersActivity.this) == false) {
                    Toast.makeText(UsersActivity.this, "Can't refresh without internet", Toast.LENGTH_SHORT).show();
                    swipeContainer.setRefreshing(false);
                    return;
                } else {
                    adapter.clear();
                    users.clear();
                    refresh(false);
                }
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimaryDark,
                R.color.colorPrimary);



    }

    public void populateUsers(String cursor) {
        if (Utils.isNetworkAvailable(this) == false ) {
            if (cursor == null) {
                if (showFollowing) {
                    adapter.addAll(User.getFollowingOfUser(currentUser));
                } else {
                    adapter.addAll(User.getFollowersOfUser(currentUser));
                }
            }
            Toast.makeText(this, "Offline Mode", Toast.LENGTH_SHORT).show();
            return;
        }
        pd.show();
        //Clear out tweets if needed

        if (showFollowing) {
            client.getFollowing(currentUser.getScreenName(), cursor, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    adapter.addAll(User.fromJSONObject(response));
                    currentCursor = Utils.getCurrentCursor(response);
                    swipeContainer.setRefreshing(false);
                    pd.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Utils.showToastForException(UsersActivity.this);
                    swipeContainer.setRefreshing(false);
                    pd.dismiss();
                }

            });

        } else {
            client.getFollowers(currentUser.getScreenName(), cursor, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    adapter.addAll(User.fromJSONObject(response));
                    currentCursor = Utils.getCurrentCursor(response);
                    swipeContainer.setRefreshing(false);
                    pd.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Utils.showToastForException(UsersActivity.this);
                    swipeContainer.setRefreshing(false);
                    pd.dismiss();
                }

            });
        }

    }

    public void refresh(Boolean loading) {

        if (loading) {
            if (currentCursor.contentEquals("0")) {
                return;
            }
            populateUsers(currentCursor);
        } else {
            populateUsers(null);
        }

    }

    public void setUpProgressDialogForLoading() {
        pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
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
