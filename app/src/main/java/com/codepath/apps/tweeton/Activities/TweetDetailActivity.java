package com.codepath.apps.tweeton.Activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweeton.Fragments.ComposeTweetFragment;
import com.codepath.apps.tweeton.R;
import com.codepath.apps.tweeton.TwitterApplication;
import com.codepath.apps.tweeton.TwitterClient;
import com.codepath.apps.tweeton.Utils;
import com.codepath.apps.tweeton.models.Tweet;
import com.codepath.apps.tweeton.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetDetailActivity extends AppCompatActivity implements ComposeTweetFragment.onTweetListener {

    Tweet mTweet;
    @Bind(R.id.ivUserProfilePic)
    ImageView profile;
    @Bind(R.id.tvDetailUserName)
    TextView userName;
    @Bind(R.id.tvDetailScreenName)
    TextView screenName;
    @Bind(R.id.tvDetailTweet)
    TextView tweet;
    @Bind(R.id.ivImageMedia)
    ImageView media;
    @Bind(R.id.tvDetailTimeStamp)
    TextView timeStamp;
    @Bind(R.id.btnReply)
    ImageButton reply;
    @Bind(R.id.vDividerTwo)
    View divider;
    @Bind(R.id.vvVideoMedia)
    VideoView video;
    @Bind(R.id.btnFavorite)
    ImageButton favorite;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Detail");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        client = TwitterApplication.getRestClient();

        ButterKnife.bind(this);
        setUpViews();

    }

    public void setUpViews() {
        Intent intent = getIntent();
        String id = intent.getStringExtra("tweet");
        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "Could not get tweet details, please try again", Toast.LENGTH_SHORT).show();
            finish();
        }

        mTweet = Tweet.getTweetFromId(id);
        User user = mTweet.getUser();
        Glide.with(this).load(user.getProfileImageUrl()).fitCenter().into(profile);
        userName.setText(user.getName());
        screenName.setText("@" + user.getScreenName());
        tweet.setText(mTweet.getBody());
        if (mTweet.getFavorited()) {
            favorite.setImageResource(R.drawable.favorited);
        } else {
            favorite.setImageResource(R.drawable.not_favorited);
        }


        String mediaUrl = mTweet.getMediaURL();
        if (mediaUrl == null || mediaUrl.isEmpty()) {
            media.setVisibility(View.GONE);
        } else {
            Glide.with(this).load(mediaUrl).placeholder(R.drawable.placeholder_tweet).centerCrop().into(media);
        }

        String videoUrl = mTweet.getVideoURL();
        if (videoUrl == null || videoUrl.isEmpty() || !videoUrl.contains("mp4")) {
            video.setVisibility(View.GONE);
        } else {
            media.setVisibility(View.GONE);
            video.setVideoPath(videoUrl);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(video);
            video.setMediaController(mediaController);
            video.requestFocus();
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    video.start();
                }
            });
        }

        timeStamp.setText(Utils.getTimeStamp(mTweet.getCreatedAt()));

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = ComposeTweetFragment.getInstance(screenName.getText().toString(), Integer.toString(screenName.getId()));
                fragment.show(getFragmentManager(), "Reply to Tweet");
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFavorited();
            }
        });


    }

    public void changeFavorited() {
        if (mTweet.getFavorited()) {
            //First change UI
            favorite.setImageResource(R.drawable.not_favorited);
            client.destroyFavorite(mTweet.getUid(), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet.setFavorited(mTweet.getUid(), false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    favorite.setImageResource(R.drawable.favorited);
                    Toast.makeText(TweetDetailActivity.this, "Error: Please try again later", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            favorite.setImageResource(R.drawable.favorited);

            client.postFavoriate(mTweet.getUid(), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet.setFavorited(mTweet.getUid(), true);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    favorite.setImageResource(R.drawable.not_favorited);
                    Toast.makeText(TweetDetailActivity.this, "Error: Please try again later", Toast.LENGTH_SHORT).show();
                    return;
                }

            });

        }
    }

    @Override
    public void onTweetSubmit() {
        //Change the reply sign to blue!!
        reply.setImageResource(R.drawable.blue_reply);
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
