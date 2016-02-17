package com.codepath.apps.tweeton;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "wzu3AbP3TnR2u5naiXz91c0GO";       // Change this
	public static final String REST_CONSUMER_SECRET = "AWVTmfKdeFqH97QqDL8TcfnDlc39eJoBSkGUvoY8c43MdTHfh2"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://tweeton"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getInterestingnessList(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	//Home Timeline Endpoint

    public void getTimeline(String id, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/home_timeline.json");

        //params
        RequestParams params = new RequestParams();
		if (id != null) {
			params.put("max_id", "" + (Long.parseLong(id) - 1));
		}
        params.put("count", "25");
        params.put("since_id", "1");

        //execute Request
        client.get(url, params, handler);
    }

	//Post Tweet Endpoint

	public void postTweet(String tweet, AsyncHttpResponseHandler handler) {
		String url = getApiUrl("statuses/update.json");

		RequestParams params = new RequestParams();
		params.put("status", tweet);

		client.post(url, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}