package com.codepath.nutrifitsta.classes;

import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Date;

public interface IPost {

    ParseUser getUser();
    void setUser(ParseUser user);
    String getCategory();
    void setCategory(String cat);
    JSONArray getLikers();
    ParseFile getImage();
    void setImage(ParseFile parseFile);
    String getDescription();
    void setDescription(String description);
    String getVideo();
    void setVideo(String video);
    int getLikesCount();
    void setLikesCount(int likes);
    String getLoc();
    void setLoc(String loc);
    Date getCreatedAt();
}
