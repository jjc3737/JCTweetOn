package com.codepath.apps.tweeton.models;

import android.os.AsyncTask;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JaneChung on 2/15/16.
 */

@Table(name = "Users")
public class User extends Model {


    @Column(name = "remote_id", unique = true)
    private long uid;
    @Column(name = "ScreenName")
    private String screenName;
    @Column(name = "Profile_image")
    private String profileImageUrl;
    @Column(name = "Name")
    private String name;
    @Column(name = "Tagline")
    private String tagline;
    @Column(name = "FollowingCount")
    private int followingCount;
    @Column(name = "FollwersCount")
    private int followersCount;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public String getTagline() {
        return tagline;
    }


    public User() {
        super();
    }

    public static User findOrCreate(JSONObject json) {
        try {
            String name = json.getString("name");
            User user = new Select().from(User.class).where("name = ?", name).executeSingle();
            
            if (user != null) {
                return user;
            } else {
                return fromJSON(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;


    }

    public static User fromJSON(JSONObject json) {
        User user = new User();

        try {
            user.name = json.getString("name");
            user.uid = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");
            user.tagline = json.getString("description");
            user.followersCount = json.getInt("followers_count");
            user.followingCount = json.getInt("friends_count");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new UserAsyncTask().execute(user);
        return user;
    }

    public static ArrayList<User> fromJSONObject(JSONObject jsonObject) {
        ArrayList<User> users = new ArrayList<>();
        try {
            JSONArray userJson = jsonObject.getJSONArray("users");

            for (int i = 0; i < userJson.length(); i++) {
                JSONObject user = userJson.getJSONObject(i);
                User u = findOrCreate(user);

                if (u != null) {
                    users.add(u);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

        return users;

    }

    public static ArrayList<User> getFollowersOfUser(User user) {
        ArrayList<User> users = new ArrayList<>();
        //Todo
        return users;
    }

    public static ArrayList<User> getFollowingOfUser(User user) {
        ArrayList<User> users = new ArrayList<>();
        //Todo
        return users;
    }
    public static User getUserFromScreenName(String screenName) {
        return new Select()
                .from(User.class)
                .where("ScreenName = ?", screenName)
                .executeSingle();
    }

    public static class UserAsyncTask extends AsyncTask<User, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(User... params) {

            User user = params[0];
            user.save();
            return null;
        }
    }

}
