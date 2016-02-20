package com.codepath.apps.tweeton.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JaneChung on 2/15/16.
 */
@Table(name = "Tweets")
public class Tweet extends Model {

    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String uid; //unique id for tweet
    @Column(name = "Body")
    public String body;
    @Column(name = "User")
    public User user;
    @Column(name = "Created_At")
    public String createdAt;
    @Column(name = "Media_url")
    public String mediaURL;

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public String getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public Tweet() {
        super();
    }

    //Deserialize JSON
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getString("id_str");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.findOrCreate(jsonObject.getJSONObject("user"));

            JSONArray media = jsonObject.getJSONObject("entities").getJSONArray("media");

           if (media != null) {
               tweet.mediaURL = media.getJSONObject(0).getString("media_url");
           }

            tweet.save();

        } catch (JSONException e) {
            e.printStackTrace();

        }

        tweet.save();
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = fromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    public static List<Tweet> getAllTweets() {

        return new Select()
                .from(Tweet.class).orderBy("remote_id ASC")
                .execute();
    }

    public static Tweet getTweetFromId(String tweetID) {
        return new Select()
                .from(Tweet.class)
                .where("remote_id = ?", tweetID)
                .orderBy("remote_id DESC")
                .executeSingle();

    }

}
