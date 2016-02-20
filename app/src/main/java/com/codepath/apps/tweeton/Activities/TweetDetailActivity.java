package com.codepath.apps.tweeton.Activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.codepath.apps.tweeton.Utils;
import com.codepath.apps.tweeton.models.Tweet;
import com.codepath.apps.tweeton.models.User;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String tweetId = intent.getStringExtra("tweet");
        if (tweetId == null || tweetId.isEmpty()) {
            Toast.makeText(this, "Could not get tweet details, please try again", Toast.LENGTH_SHORT).show();
            finish();
        }

        mTweet = Tweet.getTweetFromId(tweetId);
        User user = mTweet.getUser();
        Glide.with(this).load(user.getProfileImageUrl()).fitCenter().into(profile);
        userName.setText(user.getName());
        screenName.setText("@" + user.getScreenName());
        tweet.setText(mTweet.getBody());

        String mediaUrl = mTweet.getMediaURL();
        if (mediaUrl == null || mediaUrl.isEmpty()) {
            media.setVisibility(View.GONE);
        } else {
            Glide.with(this).load(mediaUrl).centerCrop().into(media);
        }

        String videoUrl = mTweet.getVideoURL();
        if (videoUrl == null || videoUrl.isEmpty()) {
            video.setVisibility(View.GONE);
        } else {
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
                DialogFragment fragment = ComposeTweetFragment.getInstance(screenName.getText().toString());
                fragment.show(getFragmentManager(), "Reply to Tweet");
            }
        });

    }

    @Override
    public void onTweetSubmit() {
        //Change the reply sign to blue!!
        // Would be nice to let regular activity know to refresh.. hmm! 
    }
}
