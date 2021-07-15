package com.codepath.nutrifitsta;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("FoodPost")
public class FoodPost extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_CATEGORY = "category";

    public FoodPost(){}

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getCategory() {
        return getString("category");
    }

    public void setCategory(String cat) {
        put("category", cat);
    }

    public int getNutrition() {
        return getInt("nutrition");
    }

    public void setNutrition(int cal) {
        put("nutrition", cal);
    }

    public JSONArray getRecipe() { return getJSONArray("recipe"); }

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
}
