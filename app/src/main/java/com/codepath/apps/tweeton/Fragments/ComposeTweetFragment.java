package com.codepath.apps.tweeton.Fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tweeton.R;
import com.codepath.apps.tweeton.TwitterApplication;
import com.codepath.apps.tweeton.TwitterClient;
import com.codepath.apps.tweeton.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JaneChung on 2/16/16.
 */
public class ComposeTweetFragment extends DialogFragment {
    @Bind(R.id.etTweet)
    EditText tweetText;
    @Bind(R.id.tvTweetCount)
    TextView tweetCount;
    @Bind(R.id.btnTweet)
    Button tweetButton;

    private TwitterClient client;
    private onTweetListener callback;
    private static String mReply;

    public ComposeTweetFragment() {

    }

    public static ComposeTweetFragment getInstance(String reply) {
        mReply = reply;
        return new ComposeTweetFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (onTweetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onTweetListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_tweet, container, false);
        ButterKnife.bind(this, view);

        client = TwitterApplication.getRestClient();

        tweetButton.setEnabled(false);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTweet();
            }
        });

        tweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkTweetCount();

            }
        });

        if (mReply != null || TextUtils.isEmpty(mReply)) {
            tweetText.setText("");
            tweetText.append(mReply + " ");
        }

        return view;
    }

    private void submitTweet() {

        if (Utils.isNetworkAvailable(getActivity()) == false) {
            Toast.makeText(getActivity(), "Cannot post without internet", Toast.LENGTH_SHORT).show();
            return;
        }

        client.postTweet(tweetText.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                callback.onTweetSubmit();
                getDialog().dismiss();
            }
        });

    }

    private void checkTweetCount() {
        String tweet = tweetText.getText().toString();
        tweetCount.setText("" + (140 - tweet.length()));
        if (tweet.length() > 140) {
            tweetButton.setEnabled(false);
            //do more ui for disabled state
            tweetCount.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWarning));
        } else {
            tweetButton.setEnabled(true);
            tweetCount.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface onTweetListener {
        public void onTweetSubmit();
    }
}
