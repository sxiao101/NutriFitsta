package com.codepath.nutrifitsta.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("PostActions")
public class PostActions extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_ID = "postId";
    public static final String KEY_POST = "post";

    public PostActions(){}

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getPostId() {
        return getString(KEY_ID);
    }

    public void setPostId(String id) {
        put(KEY_ID, id);
    }

    public Post getPost() {
        return ((Post)getParseObject(KEY_POST));
    }

    public void setPost(Post p) {
        put(KEY_POST, p);
    }

    public int getAction() {
        return getInt("action");
    }

    public void setAction(int i) {
        put("action", i);
    }

}
