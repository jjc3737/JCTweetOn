package com.codepath.apps.tweeton.Fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
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
 * Created by JaneChung on 2/26/16.
 */
public class ComposeDirectMessage extends DialogFragment {

    @Bind(R.id.tvRecipient)
    TextView recipient;
    @Bind(R.id.etDirectMessage)
    EditText directMessage;
    @Bind(R.id.btnSendMessage)
    Button sendButton;

    private TwitterClient client;
    private static String screenName;

    public ComposeDirectMessage() {

    }

    public static ComposeDirectMessage getInstance(String screenName) {
        ComposeDirectMessage fragment = new ComposeDirectMessage();
        fragment.screenName = screenName;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direct_message, container, false);
        ButterKnife.bind(this, view);

        client = TwitterApplication.getRestClient();
        recipient.setText("To: " + screenName);

        sendButton.setEnabled(false);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDirectMessage();
            }
        });

        directMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }
        });

        return view;
    }

    private void sendDirectMessage() {
        if (Utils.isNetworkAvailable(getActivity()) == false) {
            Toast.makeText(getActivity(), "Cannot send message without internet", Toast.LENGTH_SHORT).show();
            return;
        }

        client.postDirectMessage(screenName, directMessage.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getActivity(), "Direct Message sent", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), "Failed to send, try again", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
