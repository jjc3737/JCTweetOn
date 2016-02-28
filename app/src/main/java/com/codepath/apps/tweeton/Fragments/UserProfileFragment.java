package com.codepath.apps.tweeton.Fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweeton.Activities.UsersActivity;
import com.codepath.apps.tweeton.R;
import com.codepath.apps.tweeton.TwitterApplication;
import com.codepath.apps.tweeton.TwitterClient;
import com.codepath.apps.tweeton.Utils;
import com.codepath.apps.tweeton.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JaneChung on 2/21/16.
 */
public class UserProfileFragment extends Fragment{

    TwitterClient client;
    User user;

    @Bind(R.id.ivBackgroundPic)
    ImageView background;
    @Bind(R.id.ivUserProfilePic)
    ImageView profile;
    @Bind(R.id.tvDetailUserName)
    TextView userName;
    @Bind(R.id.tvDetailScreenName)
    TextView screenName;
    @Bind(R.id.btnFollowers)
    Button followers;
    @Bind(R.id.btnFollowing)
    Button following;
    @Bind(R.id.tvTagline)
    TextView tagline;
    @Bind(R.id.btnDirectMessage)
    ImageButton directMessage;

    static final String USER_SCREENAME_EXTRA = "userScreenName";
    static final String IS_FOLLOWING_EXTRA = "isFollowing";
    public static SharedPreferences sharedPreferences;
    public static final String prefName = "MY_SHARED_PREFS";
    private static final String currentUserScreenName = "CURRENT_USER_SCREEN_NAME";

    public static UserProfileFragment newInstance(String screenName) {
        UserProfileFragment profileFramgemt = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        profileFramgemt.setArguments(args);
        return profileFramgemt;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, v);
        client = TwitterApplication.getRestClient();
        sharedPreferences = getActivity()
                .getApplicationContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
        getUserInfo();

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UsersActivity.class);
                i.putExtra(USER_SCREENAME_EXTRA, user.getScreenName());
                i.putExtra(IS_FOLLOWING_EXTRA, false);
                startActivity(i);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UsersActivity.class);
                i.putExtra(USER_SCREENAME_EXTRA, user.getScreenName());
                i.putExtra(IS_FOLLOWING_EXTRA, true);
                startActivity(i);
            }
        });

        directMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = ComposeDirectMessage.getInstance(user.getScreenName());
                fragment.show(getActivity().getFragmentManager(), "Compose Direct Message");
            }
        });

        return v;
    }

    public void getUserInfo() {
        String screenName = getArguments().getString("screen_name");
        final String currentUserScreenNameFromShared = sharedPreferences
                .getString(currentUserScreenName, "");

        client.getUserProfile(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.findOrCreate(response);
                populateProfileHeader(user);
                if (currentUserScreenNameFromShared.contentEquals(user.getScreenName())) {
                    directMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Utils.showToastForException(getActivity());
                following.setEnabled(false);
                followers.setEnabled(false);
                directMessage.setEnabled(false);
            }
        });
    }

    public void populateProfileHeader(User user) {
        Glide.with(this).load(user.getProfileImageUrl()).into(profile);
        if (user.getBackgroundUrl() != null) {
            Glide.with(this).load(user.getBackgroundUrl()).into(background);
        }
        userName.setText(user.getName());
        screenName.setText("@" + user.getScreenName());
        following.setText(Integer.toString(user.getFollowingCount()) + " following");
        followers.setText(Integer.toString(user.getFollowersCount()) + " followers");
        tagline.setText(user.getTagline());
    }


}
