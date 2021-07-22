package com.codepath.nutrifitsta.classes;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("FitnessPost")
public class FitnessPost extends ParseObject implements IPost{
    public static final String KEY_USER = "user";
    public static final String KEY_CATEGORY = "category";

    public FitnessPost(){}

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public int getDuration() {
        return getInt("duration");
    }

    public void setDuration(int time) {
        put("duration", time);
    }

    public JSONArray getWorkout() { return getJSONArray("workout"); }

    //add or remove something from recipe array

    public JSONArray getLikers() { return getJSONArray("likers"); }

    public ParseFile getImage() { return getParseFile("image"); }

    public void setImage(ParseFile parseFile) {
        put("image", parseFile);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public String getVideo() {
        return getString("video");
    }

    public void setVideo(String video) {
        put("video", video);
    }

    public int getLikesCount() {
        return getInt("likesCount");
    }

    public void setLikesCount(int likes) {
        put("likesCount", likes);
    }

    public String getLoc() {
        return getString("location");
    }

    public void setLoc(String loc) {
        put("location", loc);
    }

    public String getCategory() {
        return getString("type");
    }

    public void setCategory(String type) {
        put("type", type);
    }
}
