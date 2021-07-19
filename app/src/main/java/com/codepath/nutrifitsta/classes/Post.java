package com.codepath.nutrifitsta.classes;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_TYPE = "type";
    public static final String KEY_POST = "postId";
    public static final String KEY_FOOD = "foodPost";
    public static final String KEY_FIT = "fitnessPost";

    public Post(){}

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(String type) {
        put(KEY_TYPE, type);
    }

    public String getPostId() {
        return getString(KEY_POST);
    }

    public void setPostId(String id) {
        put(KEY_POST, id);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public FoodPost getFood() {
        return ((FoodPost)getParseObject(KEY_FOOD));
    }

    public void setFood(FoodPost fp) {
        put(KEY_FOOD, fp);
    }

    public FitnessPost getFitness() {
        return ((FitnessPost)getParseObject(KEY_FIT));
    }

    public void setFitness(FitnessPost fp) {
        put(KEY_FIT, fp);
    }

}
