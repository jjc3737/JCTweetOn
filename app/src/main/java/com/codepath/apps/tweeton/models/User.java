package com.codepath.apps.tweeton.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JaneChung on 2/15/16.
 */
public class User {

    private String name;

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

    private long uid;
    private String screenName;
    private String profileImageUrl;

    public static User fromJSON(JSONObject json) {
        User user = new User();

        try {
            user.name = json.getString("name");
            user.uid = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
