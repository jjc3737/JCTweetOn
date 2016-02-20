package com.codepath.apps.tweeton.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

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
            user.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
